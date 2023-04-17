
package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.sensors.CANCoder;
import com.hamosad1657.lib.math.HaUnitConvertor;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.robot.subsystems.swerve.SwerveConstants;

public class SwerveModule implements Sendable {
	public int moduleNumber;
	private Rotation2d angleOffset;
	private Rotation2d desiredAngle = new Rotation2d();
	private double drivePercentOutput = 0.0;
	private double driveTicksPer100ms = 0.0;

	private TalonFX steerMotor;
	private TalonFX driveMotor;
	private CANCoder steerCANCoder;

	SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(SwerveConstants.driveKS, SwerveConstants.driveKV,
			SwerveConstants.driveKA);

	public SwerveModule(int moduleNumber, SwerveModuleConstants moduleConstants) {
		this.moduleNumber = moduleNumber;
		this.angleOffset = moduleConstants.angleOffset;

		// CANCoder configs
		this.steerCANCoder = new CANCoder(moduleConstants.cancoderID);
		this.configAngleEncoder();

		// Steer motor configs
		this.steerMotor = new TalonFX(moduleConstants.angleMotorID);
		this.configAngleMotor();

		// Drive motor configs
		this.driveMotor = new TalonFX(moduleConstants.driveMotorID);
		this.configDriveMotor();
	}

	public void setState(SwerveModuleState desiredState, boolean isOpenLoop) {
		/*
		 * This is a custom optimize function, since default WPILib optimize assumes a continuous PID controller, which
		 * the Talon onboard is not.
		 */
		desiredState = optimize(desiredState, this.getModuleState().angle);
		this.setAngle(desiredState);
		this.setSpeed(desiredState, isOpenLoop);
	}

	public Rotation2d getCanCoder() {
		return Rotation2d.fromDegrees(steerCANCoder.getAbsolutePosition());
	}

	public void resetToAbsolute() {
		double absolutePosition = HaUnitConvertor.degreesToFalconTicks(
				getCanCoder().getDegrees() - angleOffset.getDegrees(), SwerveConstants.kSteerGearRatio);
		this.steerMotor.setSelectedSensorPosition(absolutePosition);
	}

	public SwerveModuleState getModuleState() {
		return new SwerveModuleState(HaUnitConvertor.falconTicksToMPS(driveMotor.getSelectedSensorVelocity(),
				SwerveConstants.kWheelCircumferenceM, SwerveConstants.kDriveGearRatio), this.getAngle());
	}

	public SwerveModulePosition getModulePosition() {
		return new SwerveModulePosition(HaUnitConvertor.falconTicksToMeters(driveMotor.getSelectedSensorPosition(),
				SwerveConstants.kWheelCircumferenceM, SwerveConstants.kDriveGearRatio), this.getAngle());
	}

	private void setSpeed(SwerveModuleState desiredState, boolean isOpenLoop) {
		if (isOpenLoop) {
			this.drivePercentOutput = desiredState.speedMetersPerSecond / SwerveConstants.kChassisMaxSpeedMPS;
			driveMotor.set(ControlMode.PercentOutput, this.drivePercentOutput);
		} else {
			double velocity = HaUnitConvertor.MPSToFalconTicks(desiredState.speedMetersPerSecond,
					SwerveConstants.kWheelCircumferenceM, SwerveConstants.kDriveGearRatio);
			this.driveMotor.set(ControlMode.Velocity, velocity, DemandType.ArbitraryFeedForward,
					this.feedforward.calculate(desiredState.speedMetersPerSecond));
			this.driveTicksPer100ms = this.driveMotor.getClosedLoopTarget(0);
		}
	}

	public double getDrivePercentOutput() {
		return this.drivePercentOutput;
	}

	public double getVelocitySetpoint() {
		return this.driveTicksPer100ms;
	}

	private void setAngle(SwerveModuleState desiredState) {
		this.desiredAngle = desiredState.angle;
		this.steerMotor.set(ControlMode.Position,
				HaUnitConvertor.degreesToFalconTicks(this.desiredAngle.getDegrees(), SwerveConstants.kSteerGearRatio));
	}

	public Rotation2d getDesiredAngle() {
		return this.desiredAngle;
	}

	public Rotation2d getAngle() {
		return Rotation2d.fromDegrees(HaUnitConvertor.falconTicksToDegrees(steerMotor.getSelectedSensorPosition(),
				SwerveConstants.kSteerGearRatio));
	}

	private void configAngleEncoder() {
		this.steerCANCoder.configFactoryDefault();
		this.steerCANCoder.configAllSettings(SwerveModuleConstants.CANCoderConfig);
	}

	private void configAngleMotor() {
		this.steerMotor.configFactoryDefault();
		this.steerMotor.configAllSettings(SwerveModuleConstants.angleFXConfig);
		this.steerMotor.setInverted(SwerveConstants.kSteerMotorInvert);
		this.steerMotor.setNeutralMode(SwerveConstants.kSteerNeutralMode);
		this.resetToAbsolute();
	}

	private void configDriveMotor() {
		this.driveMotor.configFactoryDefault();
		this.driveMotor.configAllSettings(SwerveModuleConstants.driveFXConfig);
		this.driveMotor.setInverted(SwerveConstants.kDriveMotorInvert);
		this.driveMotor.setNeutralMode(SwerveConstants.kDriveNeutralMode);
		this.driveMotor.setSelectedSensorPosition(0);
	}

	/**
	 * Minimize the change in heading the desired swerve module state would require by potentially reversing the
	 * direction the wheel spins. Customized from WPILib's version to include placing in appropriate scope for CTRE
	 * onboard control.
	 *
	 * @param desiredState The desired state.
	 * @param currentAngle The current module angle.
	 */
	public static SwerveModuleState optimize(SwerveModuleState desiredState, Rotation2d currentAngle) {
		double targetAngle = placeInAppropriate0To360Scope(currentAngle.getDegrees(), desiredState.angle.getDegrees());
		double targetSpeed = desiredState.speedMetersPerSecond;
		double delta = targetAngle - currentAngle.getDegrees();
		if (Math.abs(delta) > 90) {
			targetSpeed = -targetSpeed;
			targetAngle = delta > 90 ? (targetAngle -= 180) : (targetAngle += 180);
		}
		return new SwerveModuleState(targetSpeed, Rotation2d.fromDegrees(targetAngle));
	}

	/**
	 * @param scopeReference Current Angle
	 * @param newAngle       Target Angle
	 * @return Closest angle within scope
	 */
	private static double placeInAppropriate0To360Scope(double scopeReference, double newAngle) {
		double lowerBound;
		double upperBound;
		double lowerOffset = scopeReference % 360;
		if (lowerOffset >= 0) {
			lowerBound = scopeReference - lowerOffset;
			upperBound = scopeReference + (360 - lowerOffset);
		} else {
			upperBound = scopeReference - lowerOffset;
			lowerBound = scopeReference - (360 + lowerOffset);
		}
		while (newAngle < lowerBound) {
			newAngle += 360;
		}
		while (newAngle > upperBound) {
			newAngle -= 360;
		}
		if (newAngle - scopeReference > 180) {
			newAngle -= 360;
		} else if (newAngle - scopeReference < -180) {
			newAngle += 360;
		}
		return newAngle;
	}

	public double getSteerErrorDeg() {
		return HaUnitConvertor.falconTicksToDegrees(this.steerMotor.getClosedLoopError(0),
				SwerveConstants.kSteerGearRatio);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("SwerveModule");
		builder.addDoubleProperty("Angle Deg", () -> this.getAngle().getDegrees(), null);
		builder.addDoubleProperty("Angle Error Deg", this::getSteerErrorDeg, null);
		builder.addDoubleProperty("Speed MPS", () -> this.getModuleState().speedMetersPerSecond, null);
		builder.addDoubleProperty("Drive Percent Output", this::getDrivePercentOutput, null);
	}
}