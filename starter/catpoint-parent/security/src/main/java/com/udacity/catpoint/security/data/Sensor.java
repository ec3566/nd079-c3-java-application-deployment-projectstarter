package com.udacity.catpoint.security.data;


import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.UUID;

/**
 * Sensor POJO. Needs to know how to sort itself for display purposes.
 */
public class Sensor implements Comparable < Sensor > {
	private UUID sensorId;
	private String name;
	private Boolean active;
	private com.udacity.catpoint.security.data.SensorType sensorType;

	public Sensor ( String name, com.udacity.catpoint.security.data.SensorType sensorType ) {
		this.name = name;
		this.sensorType = sensorType;
		this.sensorId = UUID.randomUUID ( );
		this.active = Boolean.FALSE;
	}

	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass ( ) != o.getClass ( ) ) return false;
		Sensor sensor = ( Sensor ) o;
		return sensorId.equals ( sensor.sensorId );
	}

	@Override
	public int hashCode ( ) {
		return Objects.hash ( sensorId );
	}

	public String getName ( ) {
		return name;
	}

	public void setName ( String name ) {
		this.name = name;
	}

	public Boolean getActive ( ) {
		return active;
	}

	public void setActive ( Boolean active ) {
		this.active = active;
	}

	public com.udacity.catpoint.security.data.SensorType getSensorType ( ) {
		return sensorType;
	}

	public void setSensorType ( com.udacity.catpoint.security.data.SensorType sensorType ) {
		this.sensorType = sensorType;
	}

	public UUID getSensorId ( ) {
		return sensorId;
	}

	public void setSensorId ( UUID sensorId ) {
		this.sensorId = sensorId;
	}

	@Override
	public int compareTo ( Sensor o ) {
		return ComparisonChain.start ( )
				.compare ( this.name, o.name )
				.compare ( this.sensorType.toString ( ), o.sensorType.toString ( ) )
				.compare ( this.sensorId, o.sensorId )
				.result ( );
	}
}
