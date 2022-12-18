package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.hamosad1657.lib.HaUnits;
import com.hamosad1657.lib.motors.HaTalonFX;
import com.revrobotics.CANSparkMax.IdleMode;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author Shaked - ask me if you have questions🌠
 */
public class HaSwerveModule {

	private WPI_TalonFX steerTalonFX, driveTalonFX;
	private HaTalonFX steerMotor, driveMotor;
	private final CANCoder steerEncoder;

	/**
	 * Constructs a swerve module with a CANCoder and two Falcons.
	 * <p>
	 * 
	 * @param steerMotorControllerID
	 * @param driveMotorControllerID
	 * @param steerCANCoderID
	 * @param steerOffsetDegrees
	 * @param wheelRadiusM
	 */
	public HaSwerveModule(
			int steerMotorControllerID, int driveMotorControllerID, int steerCANCoderID,
			double steerOffsetDegrees, double wheelRadiusM, HaUnits.PIDGains steerPidGains,
			HaUnits.PIDGains drivePidGains) {

		this.steerEncoder = new CANCoder(steerCANCoderID);
		this.steerEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.steerEncoder.configFeedbackCoefficient(0.087890625, "deg", SensorTimeBase.PerSecond);
		this.steerEncoder.configMagnetOffset(steerOffsetDegrees);
		this.steerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);

		this.steerTalonFX = new WPI_TalonFX(steerMotorControllerID);

		try {
			this.steerMotor = new HaTalonFX(this.steerTalonFX, steerPidGains, wheelRadiusM, FeedbackDevice.IntegratedSensor);
		} catch(Exception e) {
			DriverStation.reportError(e.toString(), true);
		}
		this.steerMotor.setIdleMode(IdleMode.kBrake);

		this.driveTalonFX = new WPI_TalonFX(driveMotorControllerID);
		try {
			this.driveMotor = new HaTalonFX(this.driveTalonFX, drivePidGains, wheelRadiusM, FeedbackDevice.IntegratedSensor);
		} catch(Exception e) {
			DriverStation.reportError(e.toString(), true);
		}

		this.syncSteerEncoder();
	}

	/**
	 * Synchronises the steer integrated encoder with the
	 * CANCoder's measurment, considering units and gear ratio.
	 */
	public void syncSteerEncoder() {
		this.steerMotor.setEncoderPosition(
				this.steerEncoder.getAbsolutePosition() 
						/ SdsModuleConfigurations.MK4_L2.getSteerReduction(),
				HaUnits.Positions.kDegrees);
	}

	/**
	 * @param pidGains
	 * @throws IllegalArgumentException
	 *             If one or more of the PID gains are
	 *             negative, or if IZone is negative.
	 */
	public void setSteerPID(HaUnits.PIDGains pidGains)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if (pidGains.p < 0 || pidGains.i < 0 || pidGains.d < 0)
			throw new IllegalArgumentException("PID gains cannot be negative.");
		// If IZone is negative
		if (pidGains.iZone < 0)
			throw new IllegalArgumentException("IZone cannot be negative.");

		this.steerMotor.configPID(pidGains);
	}

	/**
	 * @param pidGains
	 * @throws IllegalArgumentException
	 *             If one or more of the PID gains are
	 *             negative, or if IZone is negative.
	 */
	public void setDrivePID(HaUnits.PIDGains pidGains)
			throws IllegalArgumentException {
		// If one of the PID gains are negative
		if (pidGains.p < 0 || pidGains.i < 0 || pidGains.d < 0)
			throw new IllegalArgumentException("PID gains cannot be negative.");
		// If IZone is negative
		if (pidGains.iZone < 0)
			throw new IllegalArgumentException("IZone cannot be negative.");

		this.driveMotor.configPID(pidGains);
	}

	/**
	 * @return the current wheel speed and angle as a SwerveModuleState object.
	 */
	public SwerveModuleState getSwerveModuleState() {
		return new SwerveModuleState(
				this.steerMotor.get(HaUnits.Velocities.kMPS),
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
		return this.driveMotor.get(HaUnits.Velocities.kMPS);
	}

	/**
	 * Preforms velocity and position closed-loop control on the
	 * steer and drive motors, respectively. The control runs on
	 * the motor controllers.
	 * 
	 * @param moduleState
	 */
	public void setSwerveModuleState(SwerveModuleState moduleState) {
		this.driveMotor.set(moduleState.speedMetersPerSecond, HaUnits.Velocities.kMPS);
		this.steerMotor.set(moduleState.angle.getDegrees(), HaUnits.Positions.kDegrees);
	}

	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * 
	 * @param angleDegrees of the wheel
	 */
	public void setSteerMotor(double angleDegrees) {
		this.steerMotor.set(
				angleDegrees / SdsModuleConfigurations.MK4_L2.getSteerReduction(),
				HaUnits.Positions.kDegrees);
	}

	/**
	 * Preforms position closed-loop control on the
	 * steer motor. It runs on the motor controller.
	 * 
	 * @param Rotaton2d
	 *            angle of the wheel as a Rotation2d
	 */
	public void setSteerMotor(Rotation2d angle) {
		this.steerMotor.set(
				angle.getDegrees() / SdsModuleConfigurations.MK4_L2.getSteerReduction(),
				HaUnits.Positions.kDegrees);
	}

	/**
	 * Preforms velocity closed-loop control on the
	 * drive motor. It runs on the motor controller.
	 * 
	 * @param MPS of the wheel
	 */
	public void setDriveMotor(double MPS) {
		this.driveMotor.set(
				MPS / SdsModuleConfigurations.MK4_L2.getDriveReduction(),
				HaUnits.Velocities.kMPS);
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