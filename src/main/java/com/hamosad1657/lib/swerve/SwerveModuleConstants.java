
package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.subsystems.swerve.SwerveConstants;

public class SwerveModuleConstants {
	public final int driveMotorID;
	public final int angleMotorID;
	public final int cancoderID;
	public final Rotation2d angleOffset;

	/**
	 * Constants to be use when creating a swerve module.
	 * 
	 * @param driveMotorID
	 * @param angleMotorID
	 * @param canCoderID
	 * @param angleOffset
	 */
	public SwerveModuleConstants(int driveMotorID, int angleMotorID, int canCoderID, Rotation2d angleOffset) {
		this.driveMotorID = driveMotorID;
		this.angleMotorID = angleMotorID;
		this.cancoderID = canCoderID;
		this.angleOffset = angleOffset;
	}

	// Constants for every module.
	public static TalonFXConfiguration angleFXConfig;
	public static TalonFXConfiguration driveFXConfig;
	public static CANCoderConfiguration CANCoderConfig;

	static {
		angleFXConfig = new TalonFXConfiguration();
		driveFXConfig = new TalonFXConfiguration();
		CANCoderConfig = new CANCoderConfiguration();

		/* Swerve Angle Motor Configurations */
		SupplyCurrentLimitConfiguration angleSupplyLimit = new SupplyCurrentLimitConfiguration(true,
				SwerveConstants.kSteerContinuousCurrentLimit, SwerveConstants.kSteerPeakCurrentLimit,
				SwerveConstants.kSteerPeakCurrentTimeSec);

		angleFXConfig.slot0.kP = SwerveConstants.kSteerP;
		angleFXConfig.slot0.kI = SwerveConstants.kSteerI;
		angleFXConfig.slot0.kD = SwerveConstants.kSteerD;
		angleFXConfig.slot0.kF = SwerveConstants.kSteerFF;
		angleFXConfig.supplyCurrLimit = angleSupplyLimit;

		/* Swerve Drive Motor Configuration */
		SupplyCurrentLimitConfiguration driveSupplyLimit = new SupplyCurrentLimitConfiguration(true,
				SwerveConstants.kDriveContinuousCurrentLimit, SwerveConstants.kDrivePeakCurrentLimit,
				SwerveConstants.kDrivePeakCurrentTimeSec);

		driveFXConfig.slot0.kP = SwerveConstants.kDriveP;
		driveFXConfig.slot0.kI = SwerveConstants.kDriveI;
		driveFXConfig.slot0.kD = SwerveConstants.kDriveD;
		driveFXConfig.slot0.kF = SwerveConstants.kDriveFF;
		driveFXConfig.supplyCurrLimit = driveSupplyLimit;
		driveFXConfig.openloopRamp = SwerveConstants.kOpenLoopRampRate;
		driveFXConfig.closedloopRamp = SwerveConstants.kClosedLoopRampRate;

		/* Swerve CANCoder Configuration */
		CANCoderConfig.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
		CANCoderConfig.sensorDirection = SwerveConstants.kCANCoderInvert;
		CANCoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
		CANCoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
	}
}
