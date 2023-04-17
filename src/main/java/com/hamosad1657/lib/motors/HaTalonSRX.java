
package com.hamosad1657.lib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.hamosad1657.lib.math.HaUnitConvertor;
import com.hamosad1657.lib.math.HaUnits.PIDGains;
import com.hamosad1657.lib.math.HaUnits.Position;
import com.hamosad1657.lib.math.HaUnits.Velocity;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.SendableBuilder;

public class HaTalonSRX extends HaBaseTalon {
	private static final double kWheelRadNone = -1;
	private static final double kCANCoderTicksPerRev = 4096.0;
	private static final double kMaxPossibleMotorCurrent = 40.0;

	public WPI_TalonSRX motor;

	private double encoderTicksPerRev;
	private double p, i, d, ff, iZone;
	private String controlType, idleMode;
	private double controlReference;
	private final double wheelRadiusM;
	private final double maxAmpere;

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, double wheelRadiusM, FeedbackDevice feedbackDevice,
			double maxAmpere) {
		this.motor = motor;
		this.motor.configSelectedFeedbackSensor(feedbackDevice);
		this.wheelRadiusM = wheelRadiusM;
		this.controlType = "None";
		this.idleMode = "Unknown (no get function implemented)";
		this.maxAmpere = maxAmpere;
		this.controlReference = 0;
		this.configPID(PIDGains);
		switch (feedbackDevice) {
		case CTRE_MagEncoder_Absolute:
			this.encoderTicksPerRev = kCANCoderTicksPerRev;
			break;
		default:
			break;
		}
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, double wheelRadiusM) {
		this(motor, PIDGains, wheelRadiusM, FeedbackDevice.None, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains, kWheelRadNone, feedbackDevice, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor, PIDGains PIDGains) {
		this(motor, PIDGains, kWheelRadNone, FeedbackDevice.None, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor, double wheelRadiusM, FeedbackDevice feedbackDevice) {
		this(motor, new PIDGains(), wheelRadiusM, feedbackDevice, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor, double wheelRadiusM) {
		this(motor, new PIDGains(), wheelRadiusM, FeedbackDevice.None, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor, FeedbackDevice feedbackDevice) {
		this(motor, new PIDGains(), kWheelRadNone, feedbackDevice, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(WPI_TalonSRX motor) {
		this(motor, new PIDGains(), kWheelRadNone, FeedbackDevice.None, kMaxPossibleMotorCurrent);
	}

	public HaTalonSRX(int motorID) {
		this(new WPI_TalonSRX(motorID), new PIDGains(), kWheelRadNone, FeedbackDevice.None, kMaxPossibleMotorCurrent);
	}

	public void setInverted(boolean inverted) {
		this.motor.setInverted(inverted);
	}

	// TalonSRX takes encoder ticks per 100 ms as velocity setpoint.
	@Override
	public void set(double value, Velocity type) {
		this.controlReference = value;
		switch (type) {
		case kMPS:
			value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusM) * 600 * this.encoderTicksPerRev);
			this.motor.set(ControlMode.Velocity, value);
			this.controlType = "Meters Per Second (Velocity)";
			break;
		case kRPM:
			value = value / 600 * this.encoderTicksPerRev;
			// TO DO: set motor type
			this.controlType = "Rotatins Per Minute (Velocity) (currently unimplemented!)";
			break;
		case kDegPS:
			value = (HaUnitConvertor.degPSToRPM(value)) * 600 * this.encoderTicksPerRev;
			this.motor.set(ControlMode.Velocity, value);
			this.controlType = "Degrees Per Second (Velocity)";
			break;
		case kRadPS:
			value = (HaUnitConvertor.radPSToRPM(value)) * 600 * this.encoderTicksPerRev;
			this.motor.set(ControlMode.Velocity, value);
			this.controlType = "Radians Per Second (Velocity)";
			break;
		}
	}

	@Override
	public double get(Velocity type) {
		switch (type) {
		case kMPS:
			return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * this.wheelRadiusM;
		case kRPM:
			return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
		case kDegPS:
			return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
		case kRadPS:
			return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
		}
		return 0;
	}

	// TalonSRX takes encoder ticks as position setpoint
	@Override
	public void set(double value, Position type) {
		this.controlReference = value;
		switch (type) {
		case kDeg:
			value = (value / 360) * this.encoderTicksPerRev;
			this.motor.set(ControlMode.Position, value);
			this.controlType = "Degrees (Position)";
			break;
		case kRad:
			value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
			this.motor.set(ControlMode.Position, value);
			this.controlType = "Radians (Position)";
			break;
		case kRot:
			value = value * this.encoderTicksPerRev;
			this.motor.set(ControlMode.Position, value);
			this.controlType = "Rotations (Position)";
			break;
		default:
			break;
		}
	}

	@Override
	public double get(Position type) {
		switch (type) {
		case kDeg:
			return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * 360;
		case kRad:
			return (this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev) * (Math.PI * 2);
		case kRot:
			return this.motor.getSelectedSensorPosition() / this.encoderTicksPerRev;
		default:
			return 0;
		}
	}

	@Override
	public void set(double value) {
		this.controlReference = value;
		this.controlType = "Percent output";
		if (value >= -1.0 && value <= 1.0) {
			this.motor.set(ControlMode.PercentOutput, value);
		}
	}

	@Override
	public double get() {
		return this.motor.get();
	}

	@Override
	public void setCurrent(double value) {
		if (value >= 0.0 && value <= this.maxAmpere) {
			this.motor.set(ControlMode.Current, value);
		}
	}

	@Override
	public double getCurrent() {
		return this.motor.getSupplyCurrent();
	}

	@Override
	public void setEncoderPosition(double value, Position type) {
		switch (type) {
		case kDeg:
			value = (value / 360) * this.encoderTicksPerRev;
			break;
		case kRad:
			value = (value / (Math.PI * 2)) * this.encoderTicksPerRev;
			break;
		case kRot:
			value = value * this.encoderTicksPerRev;
			break;
		default:
			break;
		}
		this.motor.setSelectedSensorPosition(value);
	}

	@Override
	public void setIdleMode(IdleMode idleMode) {
		switch (idleMode) {
		case kBrake:
			this.motor.setNeutralMode(NeutralMode.Brake);
			this.idleMode = idleMode.name();
			break;
		case kCoast:
			this.motor.setNeutralMode(NeutralMode.Coast);
			this.idleMode = idleMode.name();
		}
	}

	@Override
	protected String getIdleMode() {
		return this.idleMode;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("RobotPreferences");

		builder.addStringProperty("Control Type", this::getControlType, null);

		builder.addDoubleProperty("MotorID", this.motor::getDeviceID, null);
		builder.addDoubleProperty("Speed [-1.0, 1.0](Percent)", this::get, this::set);
		builder.addDoubleProperty("Current", this::getCurrent, this::setCurrent);
		builder.addStringProperty("IdleMode", this::getIdleMode, null);

		builder.addDoubleProperty("P", this::getP, this::setP);
		builder.addDoubleProperty("F", this::getFF, this::setFF);
		builder.addDoubleProperty("I", this::getI, this::setI);
		builder.addDoubleProperty("D", this::getD, this::setD);
		builder.addDoubleProperty("IZone", this::getIZone, this::setIZone);
	}

	@Override
	public void configPID(PIDGains pidGains) {
		this.p = pidGains.kP;
		this.motor.config_kP(0, this.p);
		this.i = pidGains.kI;
		this.motor.config_kI(0, this.i);
		this.d = pidGains.kD;
		this.motor.config_kD(0, this.d);
		this.ff = pidGains.kFF;
		this.motor.config_kF(0, this.ff);
		this.iZone = pidGains.kIZone;
		this.motor.config_IntegralZone(0, this.iZone);
	}

	@Override
	public double getP() {
		return this.p;
	}

	@Override
	public double getI() {
		return this.i;
	}

	@Override
	public double getD() {
		return this.d;
	}

	@Override
	public double getFF() {
		return this.ff;
	}

	@Override
	public double getIZone() {
		return this.iZone;
	}

	@Override
	public void setP(double value) {
		if (value >= 0) {
			this.p = value;
			this.motor.config_kP(0, this.p);
		}
	}

	@Override
	public void setI(double value) {
		if (value >= 0) {
			this.i = value;
			this.motor.config_kI(0, this.i);
		}
	}

	@Override
	public void setD(double value) {
		if (value >= 0) {
			this.d = value;
			this.motor.config_kD(0, this.d);
		}
	}

	@Override
	public void setFF(double value) {
		if (value >= 0) {
			this.ff = value;
			this.motor.config_kF(0, this.ff);
		}
	}

	@Override
	public void setIZone(double value) {
		if (value >= 0) {
			this.iZone = value;
			this.motor.config_IntegralZone(0, this.iZone);
		}

	}

	@Override
	protected String getControlType() {
		return this.controlType;
	}

	@Override
	protected double getControlReference() {
		return this.controlReference;
	}
}
