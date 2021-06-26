package com.udacity.catpoint.security.data;

import java.util.Set;

/**
 * Interface showing the methods our security repository will need to support
 */
public interface SecurityRepository {
    void addSensor( com.udacity.catpoint.security.data.Sensor sensor);
    void removeSensor( com.udacity.catpoint.security.data.Sensor sensor);
    void updateSensor( com.udacity.catpoint.security.data.Sensor sensor);
    void setAlarmStatus(AlarmStatus alarmStatus);
    void setArmingStatus(ArmingStatus armingStatus);
    Set< com.udacity.catpoint.security.data.Sensor > getSensors();
    AlarmStatus getAlarmStatus();
    ArmingStatus getArmingStatus();


}
