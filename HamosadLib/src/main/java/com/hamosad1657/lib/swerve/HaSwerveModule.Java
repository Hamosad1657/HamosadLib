package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * @author Shaked - ask me if you have questions🌠
 */
public class HaSwerveModule {

	private WPI_TalonFX steerTalonFX, driveTalonFX;
	private final CANCoder steerEncoder;

	private final double kWheelCircumferenceM;

	/**
	 * Constructs a swerve module with a CANCoder and two Falcons.
	 * <p>
	 * 
	 * @param steerMotorControllerType
	 * @param steerMotorControllerID
	 * @param driveMotorControllerType
	 * @param driveMotorControllerID
	 * @param steerCANCoderID
	 * @param steerOffsetDegrees
	 */
	public HaSwerveModule(
			int steerMotorControllerID, int driveMotorControllerID, int steerCANCoderID,
			double steerOffsetDegrees, double wheelDiameterCM) {

		this.kWheelCircumferenceM = wheelDiameterCM / 10 * Math.PI;

		this.steerEncoder = new CANCoder(steerCANCoderID);
		this.steerEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.steerEncoder.configFeedbackCoefficient(0.087890625, "deg", SensorTimeBase.PerSecond);
		this.steerEncoder.configMagnetOffset(steerOffsetDegrees);
		this.steerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);

		this.steerTalonFX = new WPI_TalonFX(steerMotorControllerID);
		this.steerTalonFX.setNeutralMode(NeutralMode.Brake);
		this.steerTalonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
		this.steerTalonFX.setSelectedSensorPosition(
				(this.steerEncoder.getAbsolutePosition()
						* HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDeg)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());

		this.driveTalonFX = new WPI_TalonFX(driveMotorControllerID);
		this.driveTalonFX.setNeutralMode(NeutralMode.Brake);
		this.driveTalonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

		this.syncSteerEncoder();
	}

	/**
	 * Synchronises the steer integrated encoder with the
	 * CANCoder's measurment, considering units and gear ratio.
	 */
	public void syncSteerEncoder() {
		this.steerTalonFX.setSelectedSensorPosition(
				(this.steerEncoder.getAbsolutePosition()
						* HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDeg)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
	}

	/**
	 * @param Proportional
	 * @param Integral
	 * @param Derivative
	 * @param IZone if the absloute closed-loop error is above IZone, the
	 *            	Integral accumulator is cleared (making it ineffective).
	 * @throws IllegalArgumentException
	 *             If one or more of the PID gains are
	 *             negative, or if IZone is negative.
	 */
	public void setSteerPID(
			double Proportional, double Integral, double Derivative, double IZone)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if (Proportional < 0 || Integral < 0 || Derivative < 0)
			throw new IllegalArgumentException("PID gains cannot be negative.");
		// If IZone is negative
		if (IZone < 0)
			throw new IllegalArgumentException("IZone cannot be negative.");

		this.configPID(
				this.steerTalonFX,
				Proportional,
				Integral,
				Derivative,
				IZone);
	}

	/**
	 * @param Proportional
	 * @param Integral
	 * @param Derivative
	 * @param IZone if the absloute closed-loop error is above IZone, the
	 *            	Integral accumulator is cleared (making it ineffective).
	 * @throws IllegalArgumentException
	 *             if one or more of the PID gains are
	 *             negative, or if IZone is negative.
	 */
	public void setDrivePID(
			double Proportional, double Integral, double Derivative, double IZone)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if (Proportional < 0 || Integral < 0 || Derivative < 0)
			throw new IllegalArgumentException("PID gains cannot be negative");
		// If IZone is negative
		if (IZone < 0)
			throw new IllegalArgumentException("IZone cannot be negative");

		this.configPID(
				this.driveTalonFX,
				Proportional,
				Integral,
				Derivative,
				IZone);
	}

	/**
	 * @return the current wheel speed and angle as a SwerveModuleState object.
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
		double integratedEncoderTicksPer100MS = this.driveTalonFX.getSelectedSensorVelocity();
		double integratedEncoderTicksPS = integratedEncoderTicksPer100MS * 10;
		double motorRevPS = integratedEncoderTicksPS /
				HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerRev;
		double wheelRevPS = motorRevPS * SdsModuleConfigurations.MK4_L2.getDriveReduction();
		double MPS = wheelRevPS * this.kWheelCircumferenceM;
		return MPS;
	}

	/**
	 * Preforms velocity and position closed-loop control on the
	 * steer and drive motors, respectively. The control runs on
	 * the motor controllers.
	 * 
	 * @param moduleState
	 */
	public void setSwerveModuleState(SwerveModuleState moduleState) {
		this.setDriveMotor(moduleState.speedMetersPerSecond);
		this.setSteerMotor(moduleState.angle);
	}

	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * 
	 * @param angleDegrees of the wheel
	 */
	public void setSteerMotor(double angleDegrees) {
		this.steerTalonFX.set(
				ControlMode.Position,
				(angleDegrees * HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDeg)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
	}

	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * 
	 * @param Rotaton2d
	 *            angle of the wheel as a Rotation2d
	 */
	public void setSteerMotor(Rotation2d angle) {
		this.steerTalonFX.set(
				ControlMode.Position,
				(angle.getDegrees() * HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerDeg)
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction());
	}

	/**
	 * Preforms velocity closed-loop control on the
	 * drive motor. It runs on the motor controller.
	 * 
	 * @param MPS
	 *            of the wheel
	 */
	public void setDriveMotor(double MPS) {
		this.driveTalonFX.set(
				ControlMode.Velocity,
				this.MPSToIntegratedEncoderTicksPer100MS(MPS)
						/ SdsModuleConfigurations.MK4_L2.getDriveReduction());
	}

	/**
	 * Our optimization method! Please do question it's
	 * correctness if the swerve doesn't behave as intended.
	 * A replacement for this method is is optimizeWithWPI(),
	 * which is WPILib's SwerveModuleState.optimize() but wrapped
	 * in this class. you can also use WPILib's method directly.
	 * <p>
	 * Optimizing means to minimize the change in heading the
	 * desired swerve module state would require, by potentially
	 * reversing the direction the wheel spins. If this is used
	 * with a PID controller that has continuous input for
	 * position control, then the most the wheel will rotate is
	 * 90 degrees.
	 * 
	 * @param desiredState
	 * @param currentAngleDegrees
	 * @return an optimized SwerveModuleState
	 */
	public static SwerveModuleState optimize(SwerveModuleState desiredState, double currentAngleDegrees) {
		// Make the target angle an equivalent of it between 0 and 360
		double targetAngle = placeInZeroTo360Scope(currentAngleDegrees, desiredState.angle.getDegrees());
		double targetMPS = desiredState.speedMetersPerSecond;
		double delta = targetAngle - currentAngleDegrees;
		if (Math.abs(delta) > 90) { // If you need to turn more than 90 degrees to either direction
			targetMPS = -targetMPS; // Invert the wheel speed
			if (delta > 90) // If you need to turn > positive 90 degrees
				targetAngle -= 180;
			else // If you need to turn > negative 90 degrees,
				targetAngle += 180;
		}
		return new SwerveModuleState(targetMPS, Rotation2d.fromDegrees(targetAngle));
	}

	/**
	 * WPILib's SwerveModuleState.optimize(), wrapped.
	 * <p>
	 * Optimizing means to minimize the change in heading the
	 * desired swerve module state would require, by potentially
	 * reversing the direction the wheel spins. If this is used
	 * with a PID controller that has continuous input for
	 * position control, then the most the wheel will rotate is
	 * 90 degrees.
	 * 
	 * @param desiredState
	 * @param currentAngleDegrees
	 * @return an optimized SwerveModuleState
	 */
	public static SwerveModuleState optimizeWithWPI(SwerveModuleState desiredState, double currentAngleDegrees) {
		return SwerveModuleState.optimize(desiredState, Rotation2d.fromDegrees(currentAngleDegrees));
	}

	/**
	 * WPILib's SwerveModuleState.optimize(), wrapped.
	 * <p>
	 * Optimizing means to minimize the change in heading the
	 * desired swerve module state would require, by potentially
	 * reversing the direction the wheel spins. If this is used
	 * with a PID controller that has continuous input for
	 * position control, then the most the wheel will rotate is
	 * 90 degrees.
	 * 
	 * @param desiredState
	 * @param currentAngle
	 * @return an optimized SwerveModuleState
	 */
	public static SwerveModuleState optimizeWithWPI(
			SwerveModuleState desiredState, Rotation2d currentRotation) {
		return SwerveModuleState.optimize(desiredState, currentRotation);
	}

	@Override
	public String toString() {
		return "\n MPS: " + String.valueOf(this.getWheelMPS()) +
				"\n Angle: " + String.valueOf(this.steerEncoder.getAbsolutePosition());
	}

	private void configPID(WPI_TalonFX talonFX, double P, double I, double D, double IZone) {
		talonFX.config_kP(0, P);
		talonFX.config_kI(0, I);
		talonFX.config_kD(0, D);
		talonFX.config_IntegralZone(0, IZone);
	}

	/**
	 * Converts meters per second to the units the TalonFX uses for position
	 * feedback, which are integrated encoder ticks per 100 miliseconds.
	 * 
	 * @param MPS
	 * @return integrated encoder ticks per 100 ms
	 */
	private double MPSToIntegratedEncoderTicksPer100MS(double MPS) {
		double wheelRevPS = MPS / this.kWheelCircumferenceM;
		double motorRevPS = wheelRevPS
				/ SdsModuleConfigurations.MK4_L2.getDriveReduction();
		double integratedEncoderTicksPS = motorRevPS *
				(HaSwerveConstants.kTalonFXIntegratedEncoderTicksPerRev);
		double encoderTicksPer100MS = integratedEncoderTicksPS / 10;
		return encoderTicksPer100MS;
	}

	/**
	 * @param currentAngleDegrees
	 * @param targetAngleDegrees
	 * @return equivalent angle between 0 to 360
	 */
	private static double placeInZeroTo360Scope(
			double currentAngleDegrees, double targetAngleDegrees) {
		double lowerBound;
		double upperBound;
		double lowerOffset = currentAngleDegrees % 360;
		if (lowerOffset >= 0) {
			lowerBound = currentAngleDegrees - lowerOffset;
			upperBound = currentAngleDegrees + (360 - lowerOffset);
		} else {
			upperBound = currentAngleDegrees - lowerOffset;
			lowerBound = currentAngleDegrees - (360 + lowerOffset);
		}
		while (targetAngleDegrees < lowerBound) {
			// Increase the angle by 360 until it's something between 0 and 360
			targetAngleDegrees += 360;
		}
		while (targetAngleDegrees > upperBound) {
			// Decrease the angle by 360 until it's something between 0 and 360
			targetAngleDegrees -= 360;
		}
		// If the difference between the target and current angle
		// is more than 180, decrease the the target angle by 360
		if (targetAngleDegrees - currentAngleDegrees > 180) {
			targetAngleDegrees -= 360;
		}
		// If the difference between the target and current angle
		// is less than -180, increase the target angle by 360
		else if (targetAngleDegrees - currentAngleDegrees < -180) {
			targetAngleDegrees += 360;
		}
		return targetAngleDegrees;
	}
}