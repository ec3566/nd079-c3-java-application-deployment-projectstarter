package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith ( MockitoExtension.class )
public class SecurityTestService {

	private SecurityService ss;
	private Sensor testSensor;
	private String randomUUID = UUID.randomUUID ( ).toString ( );

	@Mock
	private StatusListener statusListener;
	@Mock
	private SecurityRepository sr; //too much of a pain to type it out everytime sorry
	@Mock
	private FakeImageService imageService;

	private Sensor getSensor ( ) {
		return new Sensor ( randomUUID, SensorType.DOOR );
	}

	private Set < Sensor > getAllSensors ( int count, boolean status ) {
		Set < Sensor > sensorSet = new HashSet <> ( );
		for ( int i = 0; i < count; i++ ) {
			sensorSet.add ( new Sensor ( randomUUID, SensorType.DOOR ) );
		}
		sensorSet.forEach ( sensor -> sensor.setActive ( status ) );

		return sensorSet;
	}

	@BeforeEach
	void init ( ) {
		ss = new SecurityService ( sr, imageService );
		testSensor = getSensor ( );
	}

	//maybe shorten instead of just copy pasting lol
	// 1
	@Test
	void if_alarm_is_armed_and_a_sensor_becomes_activated_put_the_system_into_pending_alarm_status ( ) {
		// ARMED
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		// QUIET
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.NO_ALARM );
		// ON
		ss.changeSensorActivationStatus ( testSensor, true );

		// VERIFY ALARM PROPERLY PENDING
		verify ( sr ).setAlarmStatus ( AlarmStatus.PENDING_ALARM );
	}

	// 2
	@Test
	void if_alarm_is_armed_and_a_sensor_becomes_activated_and_the_system_is_already_pending_alarm_set_the_alarm_status_to_alarm ( ) {
		// ARMED
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		// PENDING
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.PENDING_ALARM );
		// ON
		ss.changeSensorActivationStatus ( testSensor, true );

		// VERIFY ALARM PROPERLY STARTS ALARMING
		verify ( sr ).setAlarmStatus ( AlarmStatus.ALARM );
	}

	// 3
	@Test
	void if_pending_alarm_and_all_sensors_are_inactive_return_to_no_alarm_state ( ) {
		// ARMED
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		// PENDING
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.PENDING_ALARM );
		testSensor.setActive ( false );
		// OFF
		ss.changeSensorActivationStatus ( testSensor );

		// VERIFY ALRRM PROPERLY DEACTIVATES
		verify ( sr ).setAlarmStatus ( AlarmStatus.NO_ALARM );
	}

	//4
	@ParameterizedTest
	@ValueSource ( booleans = { true, false } )
	@MockitoSettings ( strictness =  Strictness.LENIENT )
	void if_alarm_is_active_change_in_sensor_state_should_not_affect_the_alarm_state ( boolean senseChange ) {
		// ALARMING
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.ALARM );
		// CHANGE SENSOR
		ss.changeSensorActivationStatus ( testSensor, senseChange );

		//V ERIFY ALARM NEVER CHANGES STATE
		verify ( sr, never ( ) ).setAlarmStatus ( ( any ( AlarmStatus.class ) ));
	}

	// 5
	@Test
	void if_a_sensor_is_activated_while_already_active_and_the_system_is_in_pending_state_change_it_to_alarm_state ( ) {
		// ARMED
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		// PENDING
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.PENDING_ALARM );
		// ON
		ss.changeSensorActivationStatus ( testSensor, true );

		// VERIFY ALARM PROPERLY STARTS ALARMING
		verify ( sr ).setAlarmStatus ( AlarmStatus.ALARM );

	}

	// 6
	@ParameterizedTest
	@EnumSource ( value = AlarmStatus.class, names = { "ALARM", "PENDING_ALARM", "NO_ALARM" } )
	@MockitoSettings ( strictness = Strictness.LENIENT )
	void if_a_sensor_is_deactivated_while_already_inactive_make_no_changes_to_the_alarm_state ( AlarmStatus alarmStatus ) {
		// ALARM PAREMETERIZED
		when ( sr.getAlarmStatus ( ) ).thenReturn ( alarmStatus );
		// OFF
		testSensor.setActive ( false );
		ss.changeSensorActivationStatus ( testSensor, false );

		// VERIFY ALRAM NEVER CHANGED ALARMSTATUS
		verify ( sr, never ( ) ).setAlarmStatus ( ( any ( AlarmStatus.class ) ) );
	}

	// 7
	@Test
	void if_the_image_service_identifies_an_image_containing_a_cat_while_the_system_is_armed_home_put_the_system_into_alarm_status ( ) {
		// ARMED HOME
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		// CAT
		when ( imageService.imageContainCat ( any ( ), ArgumentMatchers.anyFloat ( ) ) ).thenReturn ( true );
		// PROCESS CAT
		ss.processImage ( mock ( BufferedImage.class ) );

		// VERIFY ALARM SETS OFF WHEN CAT
		verify ( sr, times ( 1 ) ).setAlarmStatus ( AlarmStatus.ALARM );

	}

	// 8
	@Test
	@MockitoSettings ( strictness = Strictness.LENIENT )
	void If_the_image_service_identifies_an_image_that_does_not_contain_a_cat_change_the_status_to_no_alarm_as_long_as_the_sensors_are_not_active ( ) {
		Set < Sensor > sensorSet = getAllSensors ( 2, false );
		when ( sr.getSensors ( ) ).thenReturn ( sensorSet );
		// NO CAT
		when ( imageService.imageContainCat ( any ( ), ArgumentMatchers.anyFloat ( ))).thenReturn ( false );
		// PROCESS CAT
		ss.processImage ( mock ( BufferedImage.class ) );

		// VERIFY ALARM TURNS OFF WHEN NO CAT
		verify ( sr, times ( 1 ) ).setAlarmStatus ( AlarmStatus.NO_ALARM );
	}

	// 9
	@Test
	void if_the_system_is_disarmed_set_the_status_to_no_alarm ( ) {
		// DISARMED
		ss.setArmingStatus ( ArmingStatus.DISARMED );

		// VERIFY ALARM TURNED OFF
		verify ( sr, times ( 1 ) ).setAlarmStatus ( AlarmStatus.NO_ALARM );
	}

	// 10
	@ParameterizedTest
	@EnumSource ( value = ArmingStatus.class, names = { "ARMED_HOME", "ARMED_AWAY" } )
	void if_the_system_is_armed_reset_all_sensors_to_inactive ( ArmingStatus armingStatus ) {
		Set < Sensor > sensorSet = getAllSensors ( 2, true );
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.PENDING_ALARM );
		when ( sr.getSensors ( ) ).thenReturn ( sensorSet );
		ss.setArmingStatus ( armingStatus );

		ss.getSensors ( ).forEach ( testSensor -> {
			assertEquals ( false, testSensor.getActive ( ) );
		} );
	}

	// 11
	@Test
	void if_the_system_is_armed_home_while_the_camera_shows_a_cat_set_the_alarm_status_to_alarm ( ) {
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.ARMED_HOME );
		when ( imageService.imageContainCat ( any ( ), ArgumentMatchers.anyFloat ( ) )).thenReturn ( true );
		ss.processImage ( mock ( BufferedImage.class ) );

		verify ( sr, times ( 1 ) ).setAlarmStatus ( AlarmStatus.ALARM );
	}

	// status listeners
	@Test
	void setStatusListener ( ) {
		ss.addStatusListener ( statusListener );
		ss.removeStatusListener ( statusListener );
	}

	// handle sensor activated
	@Test
	void handleSensorActivatedWhenDisarmed ( ) {
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.DISARMED );
		ss.changeSensorActivationStatus ( testSensor, true );
	}

	//handle sensor deactivated coverage
	@Test
	void handleSensorDeactivatedWhenAlarm ( ){
		when ( sr.getArmingStatus ( ) ).thenReturn ( ArmingStatus.DISARMED );
		when ( sr.getAlarmStatus ( ) ).thenReturn ( AlarmStatus.ALARM );
		ss.changeSensorActivationStatus ( testSensor );
	}
}



































