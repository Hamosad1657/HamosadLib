package com.hamosad1657.lib.swerve;

import com.hamosad1657.lib.sensors.HaNavX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * A Subsystem with the logic of a swerve drivetrain,
 * and public methods to use said drivetrain.
 * 
 * @author Shaked - ask me if you have questions🌠
 */
public class HaSwerveSubsystem extends SubsystemBase {

	/**
	 * This timer counts how long the chassis didn't move, so we know when
	 * we can sync the TalonFX integrated encoders with the CANCoders.
	 */
	private Timer encodersSyncTimer;

	private SwerveDriveKinematics kinematics;
	private SwerveDriveOdometry odometry;
	private ChassisSpeeds chassisSpeeds;

	private double[] previousRotations;
	private SwerveModuleState[] desiredModuleStates;
	private SwerveModulePosition[] empiricalModulePositions;

	private HaNavX navX;
	private HaSwerveModule[] swerveModules;
	private double maxChassisVelocityMPS;

	/**
	 * It's not singleton, so take care to not create more than one instance of this
	 * Subsystem.
	 * 
	 * @param startingPose
	 * @param navX
	 * @param swerveModules
	 *                              front-left, front-right, back-left, back-right
	 * @param trackWidthM           Distance between two adjacent wheels in meters.
	 * @param maxChassisVelocityMPS
	 *                              How fast the robot can move in a straight line.
	 */
	public HaSwerveSubsystem(
			Pose2d startingPose,
			HaNavX navX,
			HaSwerveModule[] swerveModules,
			double trackWidthM,
			double maxChassisVelocityMPS) {

		this.navX = navX;
		this.swerveModules = swerveModules;
		this.maxChassisVelocityMPS = maxChassisVelocityMPS;

		this.kinematics = new SwerveDriveKinematics(
				new Translation2d(trackWidthM / 2.0, trackWidthM / 2.0),
				new Translation2d(trackWidthM / 2.0, trackWidthM / 2.0),
				new Translation2d(trackWidthM / 2.0, trackWidthM / 2.0),
				new Translation2d(trackWidthM / 2.0, trackWidthM / 2.0));

		// The odometry begins from the starting pose.
		this.odometry = new SwerveDriveOdometry(
				this.kinematics, this.navX.getYawRotation2d(), this.empiricalModulePositions, startingPose);

		// Construct a new ChassisSpeeds with 0,0,0 because the robot starts the match
		// not moving.
		this.chassisSpeeds = new ChassisSpeeds();
		this.desiredModuleStates = this.kinematics.toSwerveModuleStates(this.chassisSpeeds);

		this.empiricalModulePositions = new SwerveModulePosition[4];
		this.previousRotations = new double[] { 0, 0, 0, 0 };

		this.encodersSyncTimer = new Timer();
		this.encodersSyncTimer.start();
	}

	/**
	 * @return The position of the robot according to odometry. Units in meters and
	 *         Rotation2d.
	 */
	public Pose2d getCurrentPosition() {
		return this.odometry.getPoseMeters();
	}

	/**
	 * Discards the odometry measurments and sets the pose to newPosition.
	 * 
	 * @param newPosition
	 *                    a Pose2d object. Units in meters and Rotation2d.
	 */
	public void setPosition(Pose2d newPosition) {
		this.odometry.resetPosition(this.navX.getYawRotation2d(), this.empiricalModulePositions, newPosition);
	}

	/**
	 * Discards the odometry measurments and sets the position to 0,0,0.
	 */
	public void resetPosition() {
		this.odometry.resetPosition(this.navX.getYawRotation2d(), this.empiricalModulePositions, new Pose2d());
	}

	/**
	 * Sets the angle to zero, and informs the odometry of the change.
	 */
	public void zeroAngle() {
		this.navX.zeroYaw();
		this.odometry.resetPosition( new Rotation2d(), this.empiricalModulePositions, this.getCurrentPosition());
	}

	/**
	 * Drive the swerve drivetrain. Whether it behaves field or
	 * robot-relative depends on the passed ChassisSpeeds object.
	 * Convert field-relative to robot-relative ChassisSpeeds using
	 * the static ChassisSpeeds.fromFieldRelativeSpeeds() method.
	 * 
	 * @param robotRelativeSpeeds
	 */
	public void drive(ChassisSpeeds robotRelativeSpeeds) {
		this.chassisSpeeds = robotRelativeSpeeds;
		this.desiredModuleStates = this.kinematics.toSwerveModuleStates(this.chassisSpeeds);

		this.desiredModuleStates[0] = HaSwerveModule.optimize(
				this.desiredModuleStates[0], this.swerveModules[0].getAbsWheelAngleDeg());
		this.desiredModuleStates[1] = HaSwerveModule.optimize(
				this.desiredModuleStates[1], this.swerveModules[1].getAbsWheelAngleDeg());
		this.desiredModuleStates[2] = HaSwerveModule.optimize(
				this.desiredModuleStates[2], this.swerveModules[2].getAbsWheelAngleDeg());
		this.desiredModuleStates[3] = HaSwerveModule.optimize(
				this.desiredModuleStates[3], this.swerveModules[3].getAbsWheelAngleDeg());

		// If any of the wheel speeds are over the max velocity, lower them all in the
		// same ratio.
		SwerveDriveKinematics.desaturateWheelSpeeds(
				this.desiredModuleStates, this.maxChassisVelocityMPS);

		// If chassis doesn't need to move, set the modules to 0 MPS and previous
		// rotation.
		if (!this.robotNeedsToMove()) {
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
			this.swerveModules[0].setSwerveModuleState(this.desiredModuleStates[0]);
			this.previousRotations[0] = this.swerveModules[0].getAbsWheelAngleDeg();
			// Front right
			this.swerveModules[1].setSwerveModuleState(this.desiredModuleStates[1]);
			this.previousRotations[1] = this.swerveModules[1].getAbsWheelAngleDeg();
			// Back left
			this.swerveModules[2].setSwerveModuleState(this.desiredModuleStates[2]);
			this.previousRotations[2] = this.swerveModules[2].getAbsWheelAngleDeg();
			// Back right
			this.swerveModules[3].setSwerveModuleState(this.desiredModuleStates[3]);
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
		this.empiricalModulePositions[0] = this.swerveModules[0].getSwerveModulePosition();
		this.empiricalModulePositions[1] = this.swerveModules[1].getSwerveModulePosition();
		this.empiricalModulePositions[2] = this.swerveModules[2].getSwerveModulePosition();
		this.empiricalModulePositions[3] = this.swerveModules[3].getSwerveModulePosition();

		// Update the odometry according to the empirical states.
		this.odometry.update(this.navX.getYawRotation2d(), this.empiricalModulePositions);

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
		return !(this.chassisSpeeds.vxMetersPerSecond == 0 &&
				this.chassisSpeeds.vyMetersPerSecond == 0 &&
				this.chassisSpeeds.omegaRadiansPerSecond == 0);
	}
}
