package com.hamosad1657.lib.swerve;

import com.hamosad1657.lib.sensors.HaNavX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HaSwerveSubsystem extends SubsystemBase {
    private HaSwerveSubsystem() {}

    private static HaSwerveSubsystem instance;
    public static HaSwerveSubsystem getInstance() {
        if(instance == null) instance = new HaSwerveSubsystem();
        return instance; 
    }
    private SwerveDriveKinematics kinematics;
    private SwerveDriveOdometry odometry;
    private ChassisSpeeds chassisSpeeds;

    private SwerveModuleState[] desiredStates;
    private SwerveModuleState[] empiricalStates;

    private HaNavX navX;
    private HaSwerveModule[] swerveModules;

    /**
     * Initialize the swerve drivetrain.
     * @param startingPoint
     * @param navX
     * @param swerveModules front-left, front-right, back-left, back-right
     * @param trackWidthMeters
     */
    public void initialize(
        Pose2d startingPose, HaNavX navX, HaSwerveModule[] swerveModules, double trackWidthMeters) {
        this.navX = navX;
        this.swerveModules = swerveModules;

        this.kinematics = new SwerveDriveKinematics(
                new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
                new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
                new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0),
                new Translation2d(trackWidthMeters / 2.0, trackWidthMeters / 2.0));
        this.chassisSpeeds = new ChassisSpeeds();
        this.desiredStates = this.kinematics.toSwerveModuleStates(this.chassisSpeeds);

        this.odometry = new SwerveDriveOdometry(
                this.kinematics, this.navX.getYawRotation2d(), startingPose);
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
        this.odometry.resetPosition(newPosition, this.navX.getYawRotation2d());
    }

    /**
     * Discards the odometry measurments and sets the pose to 0,0,0.
     */
    public void resetPosition() {
        this.odometry.resetPosition(new Pose2d(), this.navX.getYawRotation2d());
    }

    /**
     * Sets the angle to zero, and informs the odometry of the change.
     */
    public void zeroAngle() {
        this.navX.zeroYaw();
        this.odometry.resetPosition(this.getCurrentPosition(), new Rotation2d());
    }

    /*
    public void drive(ChassisSpeeds robotRelativeSpeeds) {}
    */

    @Override
    public void periodic() {
        this.empiricalStates[0] = this.swerveModules[0].getSwerveModuleState();
        this.empiricalStates[1] = this.swerveModules[1].getSwerveModuleState();
        this.empiricalStates[2] = this.swerveModules[2].getSwerveModuleState();
        this.empiricalStates[3] = this.swerveModules[3].getSwerveModuleState();

        this.odometry.update(this.navX.getYawRotation2d(), this.empiricalStates);
    }
}
