
package com.hamosad1657.lib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.hamosad1657.lib.HaUnitConvertor;
import com.hamosad1657.lib.HaUnits.PIDGains;
import com.hamosad1657.lib.HaUnits.Position;
import com.hamosad1657.lib.HaUnits.Velocity;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.SendableBuilder;

/** Add your docs here. */
public class HaTalonFX extends HaBaseTalon {
	private double kCANCoderTicksPerRev = 4096.0;
	private double kIntegratedEncoderTicksPerRev = 2048.0;

	public WPI_TalonFX motor;

	private double wheelRadiusM;
	private double encoderTicksPerRev;
	private double percentOutput;

	private TalonFXSimCollection simMotor;
	private double simVelocity = 0;
	private double simPosition = 0;

	/**
	 * This class ony supports the integrated encoder or a CANCoder / another CTRE magnetic encoder as feedback devices.
	 * If you want support for other feedback devices, add it yourself👍
	 */
	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains, double wheelRadiusMeters, FeedbackDevice feedbackDevice) {
		this.motor = motor;
		this.motor.configSelectedFeedbackSensor(feedbackDevice);
		this.simMotor = this.motor.getSimCollection();
		
		this.wheelRadiusM = wheelRadiusMeters;
		this.configPID(PIDGains);
		switch (feedbackDevice) {
			case CTRE_MagEncoder_Absolute:
				this.encoderTicksPerRev = this.kCANCoderTicksPerRev;
				break;
			case IntegratedSensor:
				this.encoderTicksPerRev = this.kIntegratedEncoderTicksPerRev;
				break;
			default:
				break;
		}
	}

	// TalonFX takes encoder ticks per 100 ms as velocity setpoint.
	@Override
	public void set(double value, Velocity type) {
		switch (type) {
			case kMPS:
				value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusM) * this.encoderTicksPerRev) / 600;
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRPM:
				value = (value * this.encoderTicksPerRev) / 600;
				break;
			case kDegPS:
				value = (HaUnitConvertor.degPSToRPM(value) * this.encoderTicksPerRev) / 600;
				this.motor.set(ControlMode.Velocity, value);
				break;
			case kRadPS:
				value = (HaUnitConvertor.radPSToRPM(value) * this.encoderTicksPerRev) / 600;
				this.motor.set(ControlMode.Velocity, value);
				break;
		}
	}

	// TODO: check math
	@Override
	public double get(Velocity type) {
		switch (type) {
			case kMPS:
				return HaUnitConvertor.RPMToMPS(this.motor.getSelectedSensorVelocity() * 600 / this.encoderTicksPerRev,
						this.wheelRadiusM);
			case kRPM:
				return this.motor.getSelectedSensorVelocity() * 600 / this.encoderTicksPerRev;
			case kDegPS:
				return HaUnitConvertor
						.RPMToDegPS(this.motor.getSelectedSensorVelocity() * 600 / this.encoderTicksPerRev);
			case kRadPS:
				return HaUnitConvertor
						.RPMToRadPS(this.motor.getSelectedSensorVelocity() * 600 / this.encoderTicksPerRev);
		}
		return 0;
	}

	// TalonFX takes encoder ticks as position setpoint
	@Override
	public void set(double value, Position type) {
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
	public double get(Position type) {
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
	public void setEncoderPosition(double value, Position type) {
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

	public void setSim(double value, Velocity type) {
		switch (type) {
			case kMPS:
				value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusM) * this.encoderTicksPerRev) / 600;
				this.simMotor.setIntegratedSensorVelocity((int)value);
				this.simVelocity = value;
				break;
			case kRPM:
				value = (value * this.encoderTicksPerRev) / 600;
				this.simVelocity = value;
				break;
			case kDegPS:
				value = (HaUnitConvertor.degPSToRPM(value) * this.encoderTicksPerRev) / 600;
				this.simMotor.setIntegratedSensorVelocity((int)value);
				this.simVelocity = value;
				break;
			case kRadPS:
				value = (HaUnitConvertor.radPSToRPM(value) * this.encoderTicksPerRev) / 600;
				this.simMotor.setIntegratedSensorVelocity((int)value);
				this.simVelocity = value;
				break;
		}
	}

	public void setSim(double value, Position type) {
		switch (type) {
			case kDegrees:
				value = (value / 360) * this.encoderTicksPerRev;
				this.simMotor.setIntegratedSensorRawPosition((int)value);
				this.simPosition = value;
				break;
			case kRad:
				value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
				this.simMotor.setIntegratedSensorRawPosition((int)value);
				this.simPosition = value;
				break;
			case kRotations:
				value = value * this.encoderTicksPerRev;
				this.simMotor.setIntegratedSensorRawPosition((int)value);
				this.simPosition = value;
				break;
		}
	}

	public double getSim(Velocity type) {
		switch (type) {
			case kMPS:
				return HaUnitConvertor.RPMToMPS(this.simVelocity * 600 / this.encoderTicksPerRev,
						this.wheelRadiusM);
			case kRPM:
				return this.simVelocity * 600 / this.encoderTicksPerRev;
			case kDegPS:
				return HaUnitConvertor
						.RPMToDegPS(this.simVelocity * 600 / this.encoderTicksPerRev);
			case kRadPS:
				return HaUnitConvertor
						.RPMToRadPS(this.simVelocity * 600 / this.encoderTicksPerRev);
			default:
				return 0;
		}
	}

	public double getSim(Position type) {
		switch (type) {
			case kDegrees:
				return (this.simPosition / this.encoderTicksPerRev) * 360;
			case kRad:
				return (this.simPosition / this.encoderTicksPerRev) * (Math.PI * 2);
			case kRotations:
				return this.simPosition / this.encoderTicksPerRev;
			default:
				break;
		}
		return 0;
	}
}
