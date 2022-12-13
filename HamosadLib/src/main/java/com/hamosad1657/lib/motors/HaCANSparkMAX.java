// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.HaUnitConvertor;
import com.hamosad1657.lib.HaUnits.PIDGains;
import com.hamosad1657.lib.HaUnits.Positions;
import com.hamosad1657.lib.HaUnits.Velocities;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import edu.wpi.first.util.sendable.SendableBuilder;


public class HaCANSparkMAX extends HaMotorController {

	public CANSparkMax motor;
	private SparkMaxPIDController pid;
	private double wheelRadiusMeters;
	private RelativeEncoder encoder;

	public HaCANSparkMAX(CANSparkMax motor, PIDGains pidGains, double wheelRadiusMeters) {
		this.motor = motor;
		configPID(pidGains);
		this.wheelRadiusMeters = wheelRadiusMeters;
		this.encoder = this.motor.getEncoder();

	}

	@Override
	public void configPID(PIDGains pidGains) {
		this.pid.setP(pidGains.p);
		this.pid.setI(pidGains.i);
		this.pid.setD(pidGains.d);
		this.pid.setIZone(pidGains.iZone);
	}

	@Override
	public void set(double value, Velocities type) {
		switch (type) {
			case kMPS:
				value = HaUnitConvertor.MPSToRPM(value, this.wheelRadiusMeters);
				break;
			case kRPM:
				break;
			case kDegPS:
				value = HaUnitConvertor.degPSToRPM(value);
				break;
			case kRadPS:
				value = HaUnitConvertor.radPSToRPM(value);
				break;
		}
		this.pid.setReference(value, ControlType.kVelocity);
	}

	@Override
	public double get(Velocities type) {
		switch (type) {
			case kMPS:
				return HaUnitConvertor.RPMToMPS(this.encoder.getVelocity(), this.wheelRadiusMeters);
			case kRPM:
				return this.encoder.getVelocity();
			case kDegPS:
				return HaUnitConvertor.RPMToDegPS(this.encoder.getVelocity());
			case kRadPS:
				return HaUnitConvertor.RPMToRadPS(this.encoder.getVelocity());
			default:
				return this.encoder.getVelocity();
		}
	}

	@Override
	public void set(double value, Positions type) {
		switch (type) {
			case kDegrees:
				value = value / 360;
				break;
			case kRad:
				value = value / (Math.PI * 2);
				break;
			case kRotations:
				break;
		}
		this.pid.setReference(value, ControlType.kPosition);
	}

	@Override
	public double get(Positions type) {
		switch (type) {
			case kDegrees:
				return this.encoder.getPosition() * 360;
			case kRad:
				return this.encoder.getPosition() * (Math.PI * 2);
			case kRotations:
				return this.encoder.getPosition();
		}
		return 0;
	}

	@Override
	public void set(double value) {
		this.motor.set(value);
	}

	@Override
	public double get() {
		return this.motor.get();
	}

	@Override
	public void setCurrent(double value) {
		this.pid.setReference(value, ControlType.kCurrent);
		
	}

	@Override
	public double getCurrent() {
		return this.motor.getOutputCurrent();
	}

	@Override
	public void initSendable(SendableBuilder builder) {

	}

	@Override
	public void initShuffleboard() {

	}

	@Override
	public void setEncoderPosition(double value, Positions type) {
		switch (type) {
			case kDegrees:
				this.encoder.setPosition(value / 360);
				break;
			case kRad:
				this.encoder.setPosition(Math.PI * 2);
				break;
			case kRotations:
				this.encoder.setPosition(value);
				break;
		}
	}
}
