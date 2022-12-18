// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.hamosad1657.lib.HaUnitConvertor;
import com.hamosad1657.lib.HaUnits.PIDGains;
import com.hamosad1657.lib.HaUnits.Positions;
import com.hamosad1657.lib.HaUnits.Velocities;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.SendableBuilder;

/** Add your docs here. */
public class HaTalonFX extends HaBaseTalon {
	public WPI_TalonFX motor;
	
	private double wheelRadiusMeters;
	private double encoderTicksPerRev;
	private double percentOutput;

	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains, double wheelRadiusMeters, FeedbackDevice feedbackDevice) throws Exception {
		this.motor = motor;
		this.motor.configSelectedFeedbackSensor(feedbackDevice);
		this.wheelRadiusMeters = wheelRadiusMeters;
		this.configPID(PIDGains);
		switch (feedbackDevice) {
			case CTRE_MagEncoder_Absolute:
				this.encoderTicksPerRev = 4096;
				break;
			case IntegratedSensor:
				this.encoderTicksPerRev = 2084;
				break;
			default:
				throw new Exception(
					"This class currently only supports the integrated encoder and CANCoder/other CTRE magnetic encoder as feedback devices for the Talon. Add support yourself if you want");
		}
	}

	// TalonFX takes encoder ticks per 100 ms as velocity setpoint
	@Override
	public void set(double value, Velocities type) {
		switch (type) {
			case kMPS:
				value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusMeters) * 600 * this.encoderTicksPerRev);
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRPM:
				value = value / 600 * this.encoderTicksPerRev;
				break;
			case kDegPS:
				value = (HaUnitConvertor.degPSToRPM(value)) * 600 * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRadPS:
				value = (HaUnitConvertor.radPSToRPM(value)) * 600 * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Velocity, value);
				break;
		}
	}

	//TODO: check math
	@Override
	public double get(Velocities type) {
		switch (type) {
			case kMPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * this.wheelRadiusMeters;
			case kRPM:
				return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
			case kDegPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
			case kRadPS:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
		}
		return 0;
	}

	// TalonFX takes encoder ticks as position setpoint
	@Override
	public void set(double value, Positions type) {
		switch (type) {
			case kDegrees:
				value = (value / 360) * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			case kRad:
				value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			case kRotations:
				value = value * this.encoderTicksPerRev;
				this.motor.set(ControlMode.Position, value);
				break;
			default:
				break;
		}

	}

	@Override
	public double get(Positions type) {
		switch (type) {
			case kDegrees:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
			case kRad:
				return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
			case kRotations:
				return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
			default:
				break;
		}

		return 0;
	}

	@Override
	public void set(double value) {
		this.motor.set(ControlMode.PercentOutput, value);
		this.percentOutput = value;
	}

	@Override
	public double get() {
		return this.percentOutput;
	}

	@Override
	public void setCurrent(double value) {
		this.motor.set(ControlMode.Current, value);
		
	}

	@Override
	public double getCurrent() {
		return this.motor.getSupplyCurrent();
	}

	@Override
	public void configPID(PIDGains PIDGains) {
		this.motor.config_kP(0, PIDGains.p);
		this.motor.config_kI(0, PIDGains.i);
		this.motor.config_kD(0, PIDGains.d);
		this.motor.config_IntegralZone(0, PIDGains.iZone);
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
				value = (value / 360) * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			case kRad:
				value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			case kRotations:
				value = value * this.encoderTicksPerRev;
				this.motor.setSelectedSensorPosition(value);
				break;
			default:
				break;
		}
	}

	@Override
	public void setIdleMode(IdleMode idleMode) {
		switch (idleMode) {
			case kBrake:
				this.motor.setNeutralMode(NeutralMode.Brake);
				break;
			case kCoast:
				this.motor.setNeutralMode(NeutralMode.Coast);
		}
	}
}
