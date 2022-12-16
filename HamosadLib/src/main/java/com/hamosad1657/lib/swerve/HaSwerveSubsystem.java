package com.hamosad1657.lib.swerve;

import com.hamosad1657.lib.sensors.HaNavX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * A Subsystem with the logic of a swerve drivetrain,
 * and public methods to use said drivetrain.
 * Get an object of this class by calling the static
 * getInstance() method, and then call initialize()
 * on it. Probably best done in RobotContainer.
 * @author Shaked - ask me if you have questions🌠
 */
public class HaSwerveSubsystem extends SubsystemBase {

	private Timer encodersSyncTimer;

	private SwerveDriveKinematics kinematics;
	private SwerveDriveOdometry odometry;
	private ChassisSpeeds chassisSpeeds;

	private double[] previousRotations;
	private SwerveModuleState[] desiredStates;
	private SwerveModuleState[] empiricalStates;

	private HaSwerveModule[] swerveModules;
	private double maxChassisVelocityMPS;

	/**
	 * It's not singleton, so take care to not create more than one instance of this Subsystem.
	 * @param startingPose
	 * @param navX
	 * @param swerveModules front-left, front-right, back-left, back-right
	 * @param trackWidthMeters
	 * @param maxChassisVelocityMPS How fast the robot can move in a straight line
	 */
	public HaSwerveSubsystem(
			Pose2d startingPose,
			HaNavX navX,
			HaSwerveModule[] swerveModules,
			double trackWidthMeters,
			double maxChassisVelocityMPS) {

		this.swerveModules = swerveModules;
		this.maxChassisVelocityMPS = maxChassisVelocityMPS;

		this.kinematics = new SwerveDriveKinematics(
				new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
				new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
				new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
				new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0));
		this.chassisSpeeds = new ChassisSpeeds();
		this.desiredStates = this.kinematics.toSwerveModuleStates(this.chassisSpeeds);
		this.empiricalStates = new SwerveModuleState[] {
				new SwerveModuleState(),
				new SwerveModuleState(),
				new SwerveModuleState(),
				new SwerveModuleState()};

		this.odometry = new SwerveDriveOdometry(
				this.kinematics, HaNavX.getYawRotation2d(), startingPose);

		this.previousRotations = new double[] { 0, 0, 0, 0 };
		this.encodersSyncTimer = new Timer();
		this.encodersSyncTimer.start();
	}

	/**
	 * @return The position of the robot according to odometry.
	 *         Units in meters and Rotation2d.
	 */
	public Pose2d getCurrentPosition() {
		return this.odometry.getPoseMeters();
	}

	/**
	 * Discards the odometry measurments and sets the pose to newPosition.
	 * @param newPosition a Pose2d object. Units in meters and Rotation2d.
	 */
	public void setPosition(Pose2d newPosition) {
		this.odometry.resetPosition(newPosition, HaNavX.getYawRotation2d());
	}

	/**
	 * Discards the odometry measurments and sets the position to 0,0,0.
	 */
	public void resetPosition() {
		this.odometry.resetPosition(new Pose2d(), HaNavX.getYawRotation2d());
	}

	/**
	 * Sets the angle to zero, and informs the odometry of the change.
	 */
	public void zeroAngle() {
		HaNavX.resetYaw();
		this.odometry.resetPosition(this.getCurrentPosition(), new Rotation2d());
	}

	/**
	 * Drive the swerve drivetrain. Whether it behaves field or
	 * robot-relative depends on the passed ChassisSpeeds object.
	 * Convert field-relative to robot-relative ChassisSpeeds using
	 * the static ChassisSpeeds.fromFieldRelativeSpeeds() method.
	 * @param robotRelativeSpeeds
	 */
	public void drive(ChassisSpeeds robotRelativeSpeeds) {
		this.chassisSpeeds = robotRelativeSpeeds;
		this.desiredStates = this.kinematics.toSwerveModuleStates(this.chassisSpeeds);

		this.desiredStates[0] = HaSwerveModule.optimizeWithWPI(
				this.desiredStates[0], this.swerveModules[0].getAbsWheelAngleDeg());
		this.desiredStates[1] = HaSwerveModule.optimizeWithWPI(
				this.desiredStates[1], this.swerveModules[1].getAbsWheelAngleDeg());
		this.desiredStates[2] = HaSwerveModule.optimizeWithWPI(
				this.desiredStates[2], this.swerveModules[2].getAbsWheelAngleDeg());
		this.desiredStates[3] = HaSwerveModule.optimizeWithWPI(
				this.desiredStates[3], this.swerveModules[3].getAbsWheelAngleDeg());

		// If any of the wheel speeds are over the max velocity, lower them all in the same ratio.
		SwerveDriveKinematics.desaturateWheelSpeeds(
				this.desiredStates, this.maxChassisVelocityMPS);

		// If chassis doesn't need to move, set the modules to 0 MPS and previous rotation.
		if (this.robotNeedsToMove()) {
			// Front left
			this.swerveModules[0].setDriveMotor(0);
			this.swerveModules[0].setSteerMotor(this.previousRotations[0]);
			// Front right
			this.swerveModules[1].setDriveMotor(0);
			this.swerveModules[1].setSteerMotor(this.previousRotations[1]);
			// Back left
			this.swerveModules[2].setDriveMotor(0);
			this.swerveModules[2].setSteerMotor(this.previousRotations[2]);
			// Back right
			this.swerveModules[3].setDriveMotor(0);
			this.swerveModules[3].setSteerMotor(this.previousRotations[3]);
		}
		// If chassis does need to move, set the modules to the desired
		// SwerveModuleStates, and reset the sync encoders timer.
		else {
			this.encodersSyncTimer.reset();
			// Front left
			this.swerveModules[0].setSwerveModuleState(this.desiredStates[0]);
			this.previousRotations[0] = this.swerveModules[0].getAbsWheelAngleDeg();
			// Front right
			this.swerveModules[1].setSwerveModuleState(this.desiredStates[1]);
			this.previousRotations[1] = this.swerveModules[1].getAbsWheelAngleDeg();
			// Back left
			this.swerveModules[2].setSwerveModuleState(this.desiredStates[2]);
			this.previousRotations[2] = this.swerveModules[2].getAbsWheelAngleDeg();
			// Back right
			this.swerveModules[3].setSwerveModuleState(this.desiredStates[3]);
			this.previousRotations[3] = this.swerveModules[3].getAbsWheelAngleDeg();
		}
	}

	public void crossLockWheels() {
		// Front Left
		this.swerveModules[0].setDriveMotor(0);
		this.swerveModules[0].setSteerMotor(
				HaSwerveConstants.kFrontLeftCrossAngleDeg);
		this.previousRotations[0] = HaSwerveConstants.kFrontLeftCrossAngleDeg;
		// Front right
		this.swerveModules[1].setDriveMotor(0);
		this.swerveModules[1].setSteerMotor(
				HaSwerveConstants.kFrontRightCrossAngleDeg);
		this.previousRotations[1] = HaSwerveConstants.kFrontRightCrossAngleDeg;
		// Back left
		this.swerveModules[2].setDriveMotor(0);
		this.swerveModules[2].setSteerMotor(
				HaSwerveConstants.kBackLeftCrossAngleDeg);
		this.previousRotations[2] = HaSwerveConstants.kBackLeftCrossAngleDeg;
		// Back right
		this.swerveModules[3].setDriveMotor(0);
		this.swerveModules[3].setSteerMotor(
				HaSwerveConstants.kBackRightCrossAngleDeg);
		this.previousRotations[3] = HaSwerveConstants.kBackRightCrossAngleDeg;
	}

	@Override
	public void periodic() {
		// Update the empirical SwerveModuleStates using real measurments.
		this.empiricalStates[0] = this.swerveModules[0].getSwerveModuleState();
		this.empiricalStates[1] = this.swerveModules[1].getSwerveModuleState();
		this.empiricalStates[2] = this.swerveModules[2].getSwerveModuleState();
		this.empiricalStates[3] = this.swerveModules[3].getSwerveModuleState();
		// Update the odometry according to the empirical states.
		this.odometry.update(HaNavX.getYawRotation2d(), this.empiricalStates);

		// If the robot hasn't been moving for more than a second (5 iterations),
		// then sync the encoders.
		if (this.encodersSyncTimer.hasElapsed(1)) {
			this.swerveModules[0].syncSteerEncoder();
			this.swerveModules[1].syncSteerEncoder();
			this.swerveModules[2].syncSteerEncoder();
			this.swerveModules[3].syncSteerEncoder();
			this.encodersSyncTimer.reset();
		}
	}

	private boolean robotNeedsToMove() {
		return (this.chassisSpeeds.vxMetersPerSecond == 0 &&
				this.chassisSpeeds.vyMetersPerSecond == 0 &&
				this.chassisSpeeds.omegaRadiansPerSecond == 0);
	}
}
