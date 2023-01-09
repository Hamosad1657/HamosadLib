
package com.hamosad1657.lib.swerve;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import com.hamosad1657.lib.HaUnits;
import com.hamosad1657.lib.HaUnits.Velocity;
import com.hamosad1657.lib.motors.HaTalonFX;
import com.revrobotics.CANSparkMax.IdleMode;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

import edu.wpi.first.wpilibj.Timer;

/**
 * @author Shaked - ask me if you have questions🌠
 */
public class HaSwerveModule {
	private final double wheelRadiusM;

	private WPI_TalonFX steerTalonFX, driveTalonFX;
	private HaTalonFX steerMotor, driveMotor;
	private final CANCoder steerEncoder;

	private final Timer wheelDistTimer;
	private double wheelDistM = 0;

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
		this.wheelRadiusM = wheelRadiusM;

		this.steerEncoder = new CANCoder(steerCANCoderID);
		this.steerEncoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.steerEncoder.configFeedbackCoefficient(0.087890625, "deg", SensorTimeBase.PerSecond);
		this.steerEncoder.configMagnetOffset(steerOffsetDegrees);
		this.steerEncoder.configAbsoluteSensorRange(AbsoluteSensorRange.Unsigned_0_to_360);

		this.steerTalonFX = new WPI_TalonFX(steerMotorControllerID);
		this.steerMotor = new HaTalonFX(this.steerTalonFX, steerPidGains, this.wheelRadiusM,
				FeedbackDevice.IntegratedSensor);
		this.steerMotor.setIdleMode(IdleMode.kBrake);

		this.driveTalonFX = new WPI_TalonFX(driveMotorControllerID);
		this.driveMotor = new HaTalonFX(this.driveTalonFX, drivePidGains, this.wheelRadiusM,
				FeedbackDevice.IntegratedSensor);
		this.driveMotor.setIdleMode(IdleMode.kBrake);

		this.wheelDistTimer = new Timer();
		this.wheelDistTimer.start();

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
				HaUnits.Position.kDegrees);
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
				this.getWheelMPS(),
				this.getAbsWheelAngleRotation2d());
	}

	public SwerveModulePosition getSwerveModulePosition() {
		return new SwerveModulePosition(
			this.calcWheelDist(),
			this.getAbsWheelAngleRotation2d()
		);
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
		return this.driveMotor.get(Velocity.kMPS) * SdsModuleConfigurations.MK4_L2.getDriveReduction();
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
		this.steerMotor.set(
				angleDegrees / SdsModuleConfigurations.MK4_L2.getSteerReduction(),
				HaUnits.Position.kDegrees);
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
				HaUnits.Position.kDegrees);
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
				HaUnits.Velocity.kMPS);
	}


	// 3316 D-Bug's optimiation method

	/**
	 * Optimizing is minimizing the change in angle that is required to get to the desired
	 * heading, by potentially reversing and calculating new spin direction for the wheel.
	 * 
	 * @param desiredState
	 * @param currentAngleDegrees
	 * @return an optimized SwerveModuleState
	 */
	public static SwerveModuleState optimize(SwerveModuleState desiredState, double currentAngleDeg) {
		// desired angle diff in [-360, +360]
		double delta = (desiredState.angle.getDegrees() - currentAngleDeg) % 360;

		double targetAngle = currentAngleDeg + delta;
		double targetSpeed = desiredState.speedMetersPerSecond;

		// Q1 undershot. We expect a CW turn.
		if (delta <= -270)
			targetAngle += 360;

		// Q2 undershot. We expect a CCW turn to Q4 & reverse direction.
		// Q3. We expect a CW turn to Q1 & reverse direction.
		else if (-90 > delta && delta > -270) {
			targetAngle += 180;
			targetSpeed = -targetSpeed;
		}

		// Q2. We expect a CCW turn to Q4 & reverse direction.
		// Q3 overshot. We expect a CW turn to Q1 & reverse direction.
		else if (90 < delta && delta < 270) {
			targetAngle -= 180;
			targetSpeed = -targetSpeed;
		}

		// Q4 overshot. We expect a CCW turn.
		else if (delta >= 270)
			targetAngle -= 360;

		return new SwerveModuleState(targetSpeed, Rotation2d.fromDegrees(targetAngle));
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

	// TODO: test and delete
	public class test {
		Timer timer = new Timer();
		double desiredWheelAngle = 0;
		double desiredMotorAngle = 0;
		double desiredEncoderPosition = 0;

		/**
		 * Test the control of the wheel position.
		 * @return Wheel angle in degrees. Should increase by 45 every second.
		 */
		public double testWheelPosControl() {
			this.timer.start();
			setSteerMotor(desiredWheelAngle);

			if (this.timer.hasElapsed(1)) {
				this.desiredWheelAngle += 45;
				this.timer.reset();
			}
			return getAbsWheelAngleDeg();
		}

		/**
		 * Test the control of the motor position.
		 * @return Motor angle in degrees. Should increase by 90 every second.
		 */
		public double testSteerMotorPosControlDeg() {
			this.timer.start();
			steerMotor.set(this.desiredMotorAngle, HaUnits.Position.kDegrees);

			if (this.timer.hasElapsed(1)) {
				this.desiredMotorAngle += 90;
				this.timer.reset();
			}
			return steerMotor.get(HaUnits.Position.kDegrees);
		}

		/**
		 * Test the control of the motor position.
		 * @return Motor position in raw sensor units. Should increase by 100 every second.
		 */
		public double testSteerMotorPosControlRaw() {
			this.timer.start();
			setSteerMotor(this.desiredEncoderPosition);

			if (this.timer.hasElapsed(1)) {
				this.desiredEncoderPosition += 100;
				this.timer.reset();
			}
			return steerTalonFX.getSelectedSensorPosition();
		}
	}
	private double calcWheelDist() {
		this.wheelDistM += this.getWheelMPS() * this.wheelDistTimer.get();
		this.wheelDistTimer.reset(); // Doesn't stop timer, just set the time to 0
		return this.wheelDistM;
	}
}