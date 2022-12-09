package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * @author Shaked - ask me if you have questions✨
 */
public class HaSwerveModule {

	public enum MotorControllerType {
		kSparkMax, kTalonFX;
	}

	private CANSparkMax steerSparkMax, driveSparkMax;
	private SparkMaxPIDController steerPIDController, drivePIDController;
	private BaseTalon steerTalonFX, driveTalonFX;
	private final CANCoder steerEncoder;
	private final MotorControllerType steerType, driveType;

	/**
	 * Constructs a swerve module with a CANCoder.
	 * Drive and steer motor controllers can be a
	 * Spark Max or a TalonFX.
	 * <p>
	 * @param steerMotorControllerType
	 * @param steerMotorControllerID
	 * @param driveMotorControllerType
	 * @param driveMotorControllerID
	 * @param steerCANCoderID
	 * @param steerOffsetDegrees
	 */
	public HaSwerveModule(
			MotorControllerType steerMotorControllerType, int steerMotorControllerID,
			MotorControllerType driveMotorControllerType, int driveMotorControllerID,
			int steerCANCoderID, double steerOffsetDegrees) {
		this.steerType = steerMotorControllerType;
		this.driveType = driveMotorControllerType;

		this.steerEncoder = new CANCoder(steerCANCoderID);
		this.steerEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.steerEncoder.configFeedbackCoefficient(0.087890625, "deg", SensorTimeBase.PerSecond);
		this.steerEncoder.configMagnetOffset(steerOffsetDegrees);
		this.steerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);
		// If steer motor controller is a CANSparkMax
		if (this.steerType == MotorControllerType.kSparkMax) {
			this.steerSparkMax = new CANSparkMax(steerMotorControllerID, MotorType.kBrushless);
			this.steerSparkMax.setIdleMode(IdleMode.kBrake);
			this.steerPIDController = this.steerSparkMax.getPIDController();
		}
		// If steer motor controller is a TalonFX
		else if (this.steerType == MotorControllerType.kTalonFX) {
			this.steerTalonFX = new WPI_TalonFX(steerMotorControllerID);
			this.steerTalonFX.setNeutralMode(NeutralMode.Brake);
			this.steerTalonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
			this.steerTalonFX.setSelectedSensorPosition(
					(this.steerEncoder.getAbsolutePosition() * HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDegree)
							/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}

		// If drive motor controller is a CANSparkMax
		if (this.driveType == MotorControllerType.kSparkMax) {
			this.driveSparkMax = new CANSparkMax(driveMotorControllerID, MotorType.kBrushless);
			this.driveSparkMax.setIdleMode(IdleMode.kBrake);
			this.drivePIDController = this.driveSparkMax.getPIDController();
		}
		// If drive motor controller is a TalonFX
		else if (this.driveType == MotorControllerType.kTalonFX) {
			this.driveTalonFX = new WPI_TalonFX(driveMotorControllerID);
			this.driveTalonFX.setNeutralMode(NeutralMode.Brake);
			this.driveTalonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		}

		this.syncSteerEncoder();
	}

	/**
	 * Synchronises the steer integrated encoder with the
	 * CANCoder's measurment, considering units and gear ratio.
	 */
	public void syncSteerEncoder() {
		// If steer motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			this.steerSparkMax.getEncoder().setPosition(
				(this.steerEncoder.getAbsolutePosition() / 360)
				/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}
		// If steer motor controller is a TalonFX
		else {
			this.steerTalonFX.setSelectedSensorPosition(
				(this.steerEncoder.getAbsolutePosition()
						* HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDegree)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}
	}

	/**
	 * @param Proportional
	 * @param Integral
	 * @param Derivative
	 * @param IZone if the absloute closed-loop error is above IZone, the
	 * 				Integral accumulator is cleared (making it ineffective).
	 * @param slot which PID presets are you trying to configure. There are
	 * 			   between 0 and 3.
	 * @throws IllegalArgumentException if one or more of the PID gains are
	 * 									negative, or if IZone is negative.
	 */
	public void setSteerPID(
			double Proportional, double Integral, double Derivative, double IZone, int slot)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if(Proportional < 0 || Integral < 0 || Derivative < 0)
				throw new IllegalArgumentException("PID gains cannot be negative");
		// If IZone is negative
		if(IZone < 0) throw new IllegalArgumentException("IZone cannot be negative");

		// If drive motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			this.steerPIDController.setP(Proportional, slot);
			this.steerPIDController.setI(Integral, slot);
			this.steerPIDController.setD(Derivative, slot);
			this.steerPIDController.setIZone(IZone, slot);
		}
		// If drive motor controller is a TalonFX
		else {
			this.steerTalonFX.config_kP(slot, Proportional);
			this.steerTalonFX.config_kI(slot, Integral);
			this.steerTalonFX.config_kD(slot, Derivative);
			this.steerTalonFX.config_IntegralZone(slot, IZone);
		}
	}

	/**
	 * @param Proportional
	 * @param Integral
	 * @param Derivative
	 * @param IZone if the absloute closed-loop error is above IZone, the
	 * 				Integral accumulator is cleared (making it ineffective).
	 * @throws IllegalArgumentException if one or more of the PID gains are
	 * 									negative, or if IZone is negative.
	 */
	public void setDrivePID(
			double Proportional, double Integral, double Derivative, double IZone)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if(Proportional < 0 || Integral < 0 || Derivative < 0)
				throw new IllegalArgumentException("PID gains cannot be negative");
		// If IZone is negative
		if(IZone < 0) throw new IllegalArgumentException("IZone cannot be negative");

		// If drive motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			this.drivePIDController.setP(Proportional);
			this.drivePIDController.setI(Integral);
			this.drivePIDController.setD(Derivative);
			this.drivePIDController.setIZone(IZone);
		}
		// If drive motor controller is a TalonFX
		else {
			this.driveTalonFX.config_kP(0, Proportional);
			this.driveTalonFX.config_kI(0, Integral);
			this.driveTalonFX.config_kD(0, Derivative);
			this.driveTalonFX.config_IntegralZone(0, IZone);
		}
	}

	/**
	 * @return the current wheel speed and angle
	 * 		   as a SwerveModuleState object.
	 */
	public SwerveModuleState getSwerveModuleState() {
		return new SwerveModuleState(
				this.getWheelMPS(),
				Rotation2d.fromDegrees(this.getAbsWheelAngleDeg()));
	}

	/**
	 * @return the absloute angle of the wheel in degrees.
	 */
	public double getAbsWheelAngleDeg() {
		return this.steerEncoder.getAbsolutePosition();
	}

	/**
	 * @return the absloute angle of the wheel as a Rotation2d object.
	 */
	public Rotation2d getAbsWheelAngleRotation2d() {
		return Rotation2d.fromDegrees(this.steerEncoder.getAbsolutePosition());
	}

	/**
	 * @return the speed of the wheel in meters per second.
	 */
	public double getWheelMPS() {
		// If drive motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			double motorRPM = this.driveSparkMax.getEncoder().getVelocity();
			double motorRotationsPerSec = motorRPM / 60;
			double wheelRotationsPerSec = motorRotationsPerSec * SdsModuleConfigurations.MK4_L2.getDriveReduction();
			double metersPerSecond = wheelRotationsPerSec * HaSwerveConstants.kWheelCircumferenceMeters;
			return metersPerSecond;
		}
		// If drive motor controller is a TalonFX
		else {
			double integratedEncoderCountsPer100MS = this.driveTalonFX.getSelectedSensorVelocity();
			double integratedEncoderCountsPerSec = integratedEncoderCountsPer100MS * 10;
			double motorRotationsPerSec = integratedEncoderCountsPerSec / HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerRev;
			double wheelRotationsPerSec = motorRotationsPerSec * SdsModuleConfigurations.MK4_L2.getDriveReduction();
			double metersPerSecond = wheelRotationsPerSec * HaSwerveConstants.kWheelCircumferenceMeters;
			return metersPerSecond;
		}
	}

	/**
	 * Preforms velocity and position closed-loop control on the
	 * steer and drive motors, respectively. The control runs on
	 * the motor controllers.
	 * @param moduleState
	 */
	public void setSwerveModuleState(SwerveModuleState moduleState) {
		this.setDriveMotor(moduleState.speedMetersPerSecond);
		this.setSteerMotor(moduleState.angle);
	}


	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * @param angleDegrees of the wheel
	 */
	public void setSteerMotor(double angleDegrees) {
		// If drive motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			double desiredRotations = (angleDegrees / 360) / SdsModuleConfigurations.MK4_L2.getSteerReduction();
			this.steerPIDController.setReference(desiredRotations, ControlType.kPosition);
		}
		// If drive motor controller is a TalonFX
		else {
			this.steerTalonFX.set(
				ControlMode.Position,
				(angleDegrees * HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDegree)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}
	}

	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * @param Rotaton2d angle of the wheel as a Rotation2d
	 */
	public void setSteerMotor(Rotation2d angle) {
		// If drive motor controller is a Spark Max
		if(this.steerType == MotorControllerType.kSparkMax) {
			double desiredRotations = (angle.getDegrees() / 360) / SdsModuleConfigurations.MK4_L2.getSteerReduction();
			this.steerPIDController.setReference(desiredRotations, ControlType.kPosition);
		}
		// If drive motor controller is a TalonFX
		else {
			this.steerTalonFX.set(
				ControlMode.Position,
				(angle.getDegrees() * HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDegree)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}
	}

	/**
	 * Preforms velocity closed-loop control on the
	 * drive motor. It runs on the motor controller.
	 * @param MPS of the wheel
	 */
	public void setDriveMotor(double MPS) {
		// If drive motor controller is a Spark Max
		if(this.driveType == MotorControllerType.kSparkMax) {
			double desiredRPM = (((MPS * 60) / HaSwerveConstants.kWheelCircumferenceMeters)
					/ SdsModuleConfigurations.MK4_L2.getDriveReduction());
			this.drivePIDController.setReference(desiredRPM, ControlType.kVelocity);
		}
		// If drive motor controller is a TalonFX
		else {
			this.driveTalonFX.set(
					ControlMode.Velocity,
					this.MPSToIntegratedEncoderTicksPer100MS(MPS)
							/ SdsModuleConfigurations.MK4_L2.getDriveReduction());
		}
	}

	/*
	public SwerveModuleState Optimize(SwerveModuleState desiredState) {

	}
	*/

	@Override
	public String toString() {
	return "\n MPS: " + String.valueOf(this.getWheelMPS()) +
		   "\n Angle: " + String.valueOf(this.steerEncoder.getAbsolutePosition());
	}

	private double MPSToIntegratedEncoderTicksPer100MS(double metersPerSecond) {
		double wheelRotationsPerSec = metersPerSecond / (HaSwerveConstants.kWheelCircumferenceMeters);
		double motorRotationsPerSec = wheelRotationsPerSec / SdsModuleConfigurations.MK4_L2.getDriveReduction();
		double integratedEncoderCountsPerSec = motorRotationsPerSec *
				(HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerRev);
		double encoderCountsPer100MS = integratedEncoderCountsPerSec / 10;
		return encoderCountsPer100MS;
	}
}