package com.hamosad1657.lib.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * A wrapper class for kauailabs.navx.frc.AHRS,
 * which adheres to WPILib's coordinate system
 * conventions.
 * <p>
 * Get an instance of this class by using one
 * of the public getInstance() methods. They do
 * not return a value until the navX is connected
 * and calibrated.
 * @author Shaked - ask me if you have questions🌠
 */
public class HaNavX extends AHRS {
    private HaNavX() {}

    /**
     * Starts communtication between navX and RoboRIO,
     * enables logging to the RioLog & Driver Station,
     * waits until the navX is connected and calibrated,
     * then returns an instance.
     * @param port serial port (usually USB)
     * @return an instance of HaNavx, which extends AHRS.
     */
    public static HaNavX getInstance(SerialPort.Port port) {
        AHRS navx = new AHRS(
                port, SerialDataType.kProcessedData, (byte)60);
        navx.enableLogging(true);
        while(!navx.isConnected()) {}
        while(navx.isCalibrating()) {}
        DriverStation.reportError("navX done calibrating", false);
        return (HaNavX)navx;
    }

    /**
     * Starts communtication between navX and RoboRIO,
     * enables logging to the RioLog & Driver Station,
     * waits until the navX is connected and calibrated,
     * then returns an instance.
     * @param port I2C port. Using the onboard I2C port
     * is not reccomended, for more information click here:
     * https://docs.wpilib.org/en/stable/docs/yearly-overview/known-issues.html#onboard-i2c-causing-system-lockups
     * @return an instance of HaNavx, which extends AHRS.
     */
    public static HaNavX getInstance(I2C.Port port) {
        AHRS navx = new AHRS(port);
        navx.enableLogging(true);
        while(!navx.isConnected()) {}
        while(navx.isCalibrating()) {}
        DriverStation.reportError("navX done calibrating", false);
        return (HaNavX)navx;
    }

    /**
     * Starts communtication between navX and RoboRIO,
     * enables logging to the RioLog & Driver Station,
     * waits until the navX is connected and calibrated,
     * then returns an instance.
     * @param port SPI port
     * @return an instance of HaNavx, which extends AHRS.
     */
    public static HaNavX getInstance(SPI.Port port) {
        AHRS navx = new AHRS(port);
        navx.enableLogging(true);
        while(!navx.isConnected()) {}
        while(navx.isCalibrating()) {}
        DriverStation.reportError("navX done calibrating", false);
        return (HaNavX)navx;
    }

    /**
     * @return The angle of the navX on the Z axis (perpendicular
     * to earth, "yaw") in degrees. Rotating clockwise makes the
     * the angle increase, and rotating counter-clockwise makes
     * the angle decrease, according to WPILib's coordinate system
     * conventions.
     */
    public double getYawAngleDegrees() {
        return 360.0 - this.getYaw();
    }

    /**
     * @return The angle of the navX on the Z axis (perpendicular
     * to earth, "yaw") in radians. Rotating clockwise makes the
     * angle increase, and rotating counter-clockwise makes the
     * angle decrease, according to WPILib's coordinate system
     * conventions.
     */
    public double getYawAngleRadians() {
        return Math.toRadians(360.0 - this.getYaw());
    }

    /**
     * @return The angle of the navX on the Z axis (perpendicular
     * to earth, "yaw") as a Rotation2d. Rotating clockwise makes
     * the angle increase, and rotating counter-clockwise makes
     * the angle decrease, according to WPILib's coordinate system
     * conventions.
     */
    public Rotation2d getYawRotation2d() {
        return Rotation2d.fromDegrees(360.0 - this.getYaw());
    }

    /**
     * @return the rate of change in the angle on the Z
     *  axis (perpendicular to earth, "yaw") in degrees.
     * Rotating clockwise returns a positive value, and
     * counter-clockwise returns a negative value.
     */
    public double angularVelocityDegPerSec() {
        return -this.getRate();
    }

    /**
     * @return the rate of change in the angle on the Z
     * axis (perpendicular to earth, "yaw") in radians.
     * Rotating clockwise returns a positive value, and
     * counter-clockwise returns a negative value.
     */
    public double angularVelocityRadPerSec() {
        return Math.toRadians(-this.getRate());
    }
}
