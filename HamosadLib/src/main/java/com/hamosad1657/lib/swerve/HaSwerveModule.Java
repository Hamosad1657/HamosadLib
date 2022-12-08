package com.hamosad1657.lib.swerve;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.revrobotics.RelativeEncoder;

public class HaSwerveModule {

	public enum MotorControllerType {
		kCANSparkMax, kTalonSRX, kTalonFX;
	}

	private CANSparkMax steerMotorSM, driveMotorSM;
	private BaseTalon steerMotorTalon, driveMotorTalon;
	private CANCoder steerEncoder;

	/**
	 * Constructs a swerve module with a CANCoder.
	 * <p>
	 * @param steerMotorControllerType can be CANSparkMax or TalonFX
	 * @param steerMotorControllerID
	 * @param driveMotorControllerType can be CANSparkMax, TalonFX or TalonSRX
	 * @param driveMotorControllerID
	 * @param steerCANCoderID
	 * @param steerOffsetDegrees
	 * @throws IllegalArgumentException if steer motor is a TalonSRX
	 */
	public HaSwerveModule(
			MotorControllerType steerMotorControllerType, int steerMotorControllerID,
			MotorControllerType driveMotorControllerType, int driveMotorControllerID,
			int steerCANCoderID, double steerOffsetDegrees) throws IllegalArgumentException {
		this.steerEncoder = new CANCoder(steerCANCoderID);
		this.steerEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.steerEncoder.configFeedbackCoefficient(0.087890625, "deg", SensorTimeBase.PerSecond);
		this.steerEncoder.configMagnetOffset(steerOffsetDegrees);
		this.steerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);
		// If steer motor is a CANSparkMax
		if (steerMotorControllerType == MotorControllerType.kCANSparkMax) {
			this.steerMotorSM = new CANSparkMax(steerMotorControllerID, MotorType.kBrushless);
			this.steerMotorSM.setIdleMode(IdleMode.kBrake);
		}
		// If steer motor is a TalonFX
		else if (steerMotorControllerType == MotorControllerType.kTalonFX) {
			this.steerMotorTalon = new WPI_TalonFX(steerMotorControllerID);
			this.steerMotorTalon.setNeutralMode(NeutralMode.Brake);
			this.steerMotorTalon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
			this.steerMotorTalon.setSelectedSensorPosition(
					(this.steerEncoder.getAbsolutePosition() * HaSwerveConstants.kTalonFXIntegratedSensorTicksPerDegree)
							/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
		}
		// If steer motor is a TalonSRX
		else {
			throw new IllegalArgumentException("Steer motor controller cannot be a TalonSRX");
		}

		// If drive motor is a CANSparkMax
		if (driveMotorControllerType == MotorControllerType.kCANSparkMax) {
			this.driveMotorSM = new CANSparkMax(driveMotorControllerID, MotorType.kBrushless);
			this.driveMotorSM.setIdleMode(IdleMode.kBrake);
		}
		// If drive motor is a TalonFX
		else if (driveMotorControllerType == MotorControllerType.kTalonFX) {
			this.driveMotorTalon = new WPI_TalonFX(driveMotorControllerID);
			this.driveMotorTalon.setNeutralMode(NeutralMode.Brake);
			this.driveMotorTalon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		}
		// If drive motor is a TalonSRX
		else {
			this.driveMotorTalon = new WPI_TalonSRX(driveMotorControllerID);
			this.driveMotorTalon.setNeutralMode(NeutralMode.Brake);
			this.driveMotorTalon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		}
	}

	/*
	 * public SwerveModuleState getSwerveModuleState() {}
	 */

	public double getAbsWheelAngleDeg() {
		return this.steerEncoder.getAbsolutePosition();
	}

	/*
	 * public double getWheelMPS() {}
	 */

	/*
	 * public void setSwerveModuleState(SwerveModuleState moduleState)
	 */

	/*
	 * public void setSteerMotor(double angle) {}
	 */

	/*
	 * public void setDriveMotor(double MPS) {}
	 */

	/*
	 * @Override
	 * public String toString() {}
	 */
}