
package com.hamosad1657.lib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXSimCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.hamosad1657.lib.math.HaUnitConvertor;
import com.hamosad1657.lib.math.HaUnits;
import com.hamosad1657.lib.math.HaUnits.PIDGains;
import com.hamosad1657.lib.math.HaUnits.Position;
import com.hamosad1657.lib.math.HaUnits.Velocity;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.SendableBuilder;

/** Add your docs here. */
public class HaTalonFX extends HaBaseTalon {
	public static final double kMaxRPM = 6380.0;
	private static final double kWheelRadNone = -1;
	private static final int kMotorConfigsCANTimeoutMS = 10;
	public static final SupplyCurrentLimitConfiguration kDefaultCurrentConfiguration = new SupplyCurrentLimitConfiguration(
			true, 40.0, 0.0, 0.0);

	private final double kCANCoderTicksPerRev = 4096.0;
	private final double kIntegratedEncoderTicksPerRev = 2048.0;

	public WPI_TalonFX motor;

	private double wheelRadiusM;
	private double selectedEncoderTicksPerRev;

	private TalonFXSimCollection simMotor;
	private double simVelocity = 0;
	private double simPosition = 0;
	private double p, i, d, ff, iZone;
	private String controlType, idleMode;
	private double controlRefrence;
	private final double maxAmpere;
	private double inputDeadband;;

	/**
	 * This class only supports the integrated encoder or a CANCoder / another CTRE magnetic encoder as feedback
	 * devices.
	 */
	public HaTalonFX(WPI_TalonFX motor, HaUnits.PIDGains PIDGains, double wheelRadiusMeters,
			FeedbackDevice feedbackDevice, SupplyCurrentLimitConfiguration currentLimit) {
		this.motor = motor;
		this.motor.configSelectedFeedbackSensor(feedbackDevice, 0, kMotorConfigsCANTimeoutMS);
		this.motor.configSupplyCurrentLimit(currentLimit);
		this.simMotor = this.motor.getSimCollection();
		this.controlRefrence = 0;
		this.controlType = "None";
		this.idleMode = "Unknown (no get function implemented)";
		this.maxAmpere = currentLimit.currentLimit;
		this.wheelRadiusM = wheelRadiusMeters;
		this.configPID(PIDGains);
		this.inputDeadband = 0.0;
		switch (feedbackDevice) {
		case CTRE_MagEncoder_Absolute:
			this.selectedEncoderTicksPerRev = this.kCANCoderTicksPerRev;
			break;
		case IntegratedSensor:
			this.selectedEncoderTicksPerRev = this.kIntegratedEncoderTicksPerRev;
			break;
		default:
			break;
		}
	}

	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains, double wheelRadiusM, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains, wheelRadiusM, feedbackDevice, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains, double wheelRadiusM) {
		this(motor, PIDGains, wheelRadiusM, FeedbackDevice.None, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains, FeedbackDevice feedbackDevice) {
		this(motor, PIDGains, kWheelRadNone, feedbackDevice, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, PIDGains PIDGains) {
		this(motor, PIDGains, kWheelRadNone, FeedbackDevice.None, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, double wheelRadiusM, FeedbackDevice feedbackDevice) {
		this(motor, new PIDGains(), wheelRadiusM, feedbackDevice, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, double wheelRadiusM) {
		this(motor, new PIDGains(), wheelRadiusM, FeedbackDevice.None, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor, FeedbackDevice feedbackDevice) {
		this(motor, new PIDGains(), kWheelRadNone, feedbackDevice, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(WPI_TalonFX motor) {
		this(motor, new PIDGains(), kWheelRadNone, FeedbackDevice.None, kDefaultCurrentConfiguration);
	}

	public HaTalonFX(int motorID) {
		this(new WPI_TalonFX(motorID), new PIDGains(), kWheelRadNone, FeedbackDevice.None,
				kDefaultCurrentConfiguration);
	}

	// TalonFX takes encoder ticks per 100 ms as velocity setpoint.
	@Override
	public void set(double value, Velocity type) {
		this.controlRefrence = value;
		switch (type) {
		case kMPS:
			value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusM) * this.selectedEncoderTicksPerRev) / 600;
			this.controlType = "Meters per second (velocity)";
			break;
		case kRPM:
			value = (value * this.selectedEncoderTicksPerRev) / 600;
			this.controlType = "Rotations per minute (velocity)";
			break;
		case kDegPS:
			value = (HaUnitConvertor.degPSToRPM(value) * this.selectedEncoderTicksPerRev) / 600;
			this.controlType = "Degrees per second (velocity)";
			break;
		case kRadPS:
			value = (HaUnitConvertor.radPSToRPM(value) * this.selectedEncoderTicksPerRev) / 600;
			this.controlType = "Radians per second (velocity)";
			break;
		}
		this.motor.set(ControlMode.Velocity, value);
	}

	@Override
	public double get(Velocity type) {
		switch (type) {
		case kMPS:
			return HaUnitConvertor.RPMToMPS(
					this.motor.getSelectedSensorVelocity() * 600 / this.selectedEncoderTicksPerRev, this.wheelRadiusM);
		case kRPM:
			return this.motor.getSelectedSensorVelocity() * 600 / this.selectedEncoderTicksPerRev;
		case kDegPS:
			return HaUnitConvertor
					.RPMToDegPS(this.motor.getSelectedSensorVelocity() * 600 / this.selectedEncoderTicksPerRev);
		case kRadPS:
			return HaUnitConvertor
					.RPMToRadPS(this.motor.getSelectedSensorVelocity() * 600 / this.selectedEncoderTicksPerRev);
		}
		return 0;
	}

	// TalonFX takes encoder ticks as position setpoint.
	@Override
	public void set(double value, Position type) {
		this.controlRefrence = value;
		switch (type) {
		case kDeg:
			value = (value / 360) * this.selectedEncoderTicksPerRev;
			this.controlType = "Degrees (Position)";
			break;
		case kRad:
			value = (value / (Math.PI * 2)) * this.selectedEncoderTicksPerRev;
			this.controlType = "Radians (Position)";
			break;
		case kRot:
			value = value * this.selectedEncoderTicksPerRev;
			this.controlType = "Rotations (Position)";
			break;
		}
		this.motor.set(ControlMode.Position, value);
	}

	@Override
	public double get(Position type) {
		switch (type) {
		case kDeg:
			return (this.motor.getSelectedSensorPosition() / this.selectedEncoderTicksPerRev) * 360;
		case kRad:
			return (this.motor.getSelectedSensorPosition() / this.selectedEncoderTicksPerRev) * (Math.PI * 2);
		case kRot:
			return this.motor.getSelectedSensorPosition() / this.selectedEncoderTicksPerRev;
		default:
			return 0;
		}
	}

	@Override
	public void set(double value) {
		this.controlRefrence = value;
		this.controlType = "Percent (Base)";
		if (value > 1.0) {
			value = 1.0;
		} else if (value < -1.0) {
			value = -1.0;
		} else {
			if (Math.abs(value) < this.inputDeadband) {
				value = 0;
			}
		}
		this.motor.set(ControlMode.PercentOutput, value);
	}

	@Override
	public double get() {
		return this.motor.get();
	}

	@Override
	public void setCurrent(double value) {
		if (value >= 0 && value <= this.maxAmpere) {
			this.motor.set(ControlMode.Current, value);
		}
	}

	@Override
	public double getCurrent() {
		return this.motor.getSupplyCurrent();
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("RobotPreferences");

		// builder.addStringProperty("Speed Type", this::getControlType, null);
		// builder.addDoubleProperty("Motor ID", this.motor::getDeviceID, null);
		builder.addDoubleProperty("Speed (Percent)", this::get, this::set);
		// builder.addDoubleProperty("Current", this::getCurrent, this::setCurrent);
		// builder.addStringProperty("IdleMode", this::getIdleMode, null);

		builder.addDoubleProperty("P", this::getP, this::setP);
		builder.addDoubleProperty("I", this::getI, this::setI);
		builder.addDoubleProperty("D", this::getD, this::setD);
		builder.addDoubleProperty("FF", this::getFF, this::setFF);
		builder.addDoubleProperty("IZone", this::getIZone, this::setIZone);
	}

	@Override
	public void setEncoderPosition(double value, Position type) {
		switch (type) {
		case kDeg:
			value = (value / 360) * selectedEncoderTicksPerRev;
			break;
		case kRad:
			value = (value / (Math.PI * 2)) * selectedEncoderTicksPerRev;
			break;
		case kRot:
			value = value * selectedEncoderTicksPerRev;
			break;
		}
		this.motor.setSelectedSensorPosition(value);
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
		this.idleMode = idleMode.name();
	}

	@Override
	protected String getIdleMode() {
		return this.idleMode;
	}

	public void setInputDeadband(double deadband) {
		this.inputDeadband = deadband;
	}

	public void setSim(double value, Velocity type) {
		switch (type) {
		case kMPS:
			value = (HaUnitConvertor.MPSToRPM(value, this.wheelRadiusM) * this.selectedEncoderTicksPerRev) / 600;
			break;
		case kRPM:
			value = (value * this.selectedEncoderTicksPerRev) / 600;
			break;
		case kDegPS:
			value = (HaUnitConvertor.degPSToRPM(value) * this.selectedEncoderTicksPerRev) / 600;
			break;
		case kRadPS:
			value = (HaUnitConvertor.radPSToRPM(value) * this.selectedEncoderTicksPerRev) / 600;
			break;
		}
		this.simMotor.setIntegratedSensorVelocity((int) value);
		this.simVelocity = value;
	}

	public void setSim(double value, Position type) {
		switch (type) {
		case kDeg:
			value = (value / 360) * this.selectedEncoderTicksPerRev;
			break;
		case kRad:
			value = (value / (Math.PI * 2)) * this.selectedEncoderTicksPerRev;
			break;
		case kRot:
			value = value * this.selectedEncoderTicksPerRev;
			break;
		}
		this.simMotor.setIntegratedSensorRawPosition((int) value);
		this.simPosition = value;
	}

	// simVelocity * 600 / ticks is to convert from ticks per 100ms to RPM.
	public double getSim(Velocity type) {
		switch (type) {
		case kMPS:
			return HaUnitConvertor.RPMToMPS(this.simVelocity * 600 / this.selectedEncoderTicksPerRev,
					this.wheelRadiusM);
		case kRPM:
			return this.simVelocity * 600 / this.selectedEncoderTicksPerRev;
		case kDegPS:
			return HaUnitConvertor.RPMToDegPS(this.simVelocity * 600 / this.selectedEncoderTicksPerRev);
		case kRadPS:
			return HaUnitConvertor.RPMToRadPS(this.simVelocity * 600 / this.selectedEncoderTicksPerRev);
		default:
			return 0;
		}
	}

	public double getSim(Position type) {
		switch (type) {
		case kDeg:
			return (this.simPosition / this.selectedEncoderTicksPerRev) * 360;
		case kRad:
			return (this.simPosition / this.selectedEncoderTicksPerRev) * (Math.PI * 2);
		case kRot:
			return this.simPosition / this.selectedEncoderTicksPerRev;
		default:
			return 0;
		}
	}

	@Override
	public void configPID(PIDGains pidGains) {
		this.p = pidGains.kP;
		this.motor.config_kP(0, this.p, kMotorConfigsCANTimeoutMS);
		this.i = pidGains.kI;
		this.motor.config_kI(0, this.i, kMotorConfigsCANTimeoutMS);
		this.d = pidGains.kD;
		this.motor.config_kD(0, this.d, kMotorConfigsCANTimeoutMS);
		this.ff = pidGains.kFF;
		this.motor.config_kF(0, this.ff, kMotorConfigsCANTimeoutMS);
		this.iZone = pidGains.kIZone;
		this.motor.config_IntegralZone(0, this.iZone, kMotorConfigsCANTimeoutMS);
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
			this.motor.config_kP(0, this.p, kMotorConfigsCANTimeoutMS);
		}
	}

	@Override
	public void setI(double value) {
		if (value >= 0) {
			this.i = value;
			this.motor.config_kI(0, this.i, kMotorConfigsCANTimeoutMS);
		}
	}

	@Override
	public void setD(double value) {
		if (value >= 0) {
			this.d = value;
			this.motor.config_kD(0, this.d, kMotorConfigsCANTimeoutMS);
		}
	}

	@Override
	public void setFF(double value) {
		if (value >= 0) {
			this.ff = value;
			this.motor.config_kF(0, this.ff, kMotorConfigsCANTimeoutMS);
		}
	}

	@Override
	public void setIZone(double value) {
		if (value >= 0) {
			this.iZone = value;
			this.motor.config_IntegralZone(0, this.iZone, kMotorConfigsCANTimeoutMS);
		}
	}

	@Override
	protected String getControlType() {
		return this.controlType;
	}

	@Override
	protected double getControlReference() {
		return this.controlRefrence;
	}

}
