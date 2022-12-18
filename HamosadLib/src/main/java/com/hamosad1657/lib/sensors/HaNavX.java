package com.hamosad1657.lib.sensors;

import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * A wrapper class for kauailabs.navx.frc.AHRS,
 * which adheres to WPILib's coordinate system
 * conventions.
 * @author Shaked - ask me if you have questions🌠
 */
public class HaNavX {
	private HaNavX() {}
	private static double yawOffsetDeg = 0;

	public static AHRS navx;

	static {
		navx = new AHRS(
				SerialPort.Port.kUSB1, SerialDataType.kProcessedData, (byte) 60);
		navx.enableLogging(true);
		while (!navx.isConnected()) {}
		DriverStation.reportError("navX connected on port USB 1 \n", false);
		while (navx.isCalibrating()) {}
		DriverStation.reportError("navX done calibrating \n", false);
	}

	/**
	 * @return The angle of the navX on the Z axis (perpendicular
	 *         to earth, "yaw") in degrees. Rotating clockwise makes the
	 *         the angle increase, and rotating counter-clockwise makes
	 *         the angle decrease, according to WPILib's coordinate system
	 *         conventions.
	 */
	public static double getYawAngleDeg() {
		return 360.0 - navx.getYaw() + yawOffsetDeg;
	}

	/**
	 * @return The angle of the navX on the Z axis (perpendicular
	 *         to earth, "yaw") in radians. Rotating clockwise makes the
	 *         angle increase, and rotating counter-clockwise makes the
	 *         angle decrease, according to WPILib's coordinate system
	 *         conventions.
	 */
	public static double getYawAngleRad() {
		return Math.toRadians(360.0 - navx.getYaw() + yawOffsetDeg);
	}

	/**
	 * @return The angle of the navX on the Z axis (perpendicular
	 *         to earth, "yaw") as a Rotation2d. Rotating clockwise makes
	 *         the angle increase, and rotating counter-clockwise makes
	 *         the angle decrease, according to WPILib's coordinate system
	 *         conventions.
	 */
	public static Rotation2d getYawRotation2d() {
		return Rotation2d.fromDegrees(360.0 - navx.getYaw() + yawOffsetDeg);
	}

	/**
	 * @return the rate of change in the angle on the Z
	 *         axis (perpendicular to earth, "yaw") in degrees.
	 *         Rotating clockwise returns a positive value, and
	 *         counter-clockwise returns a negative value.
	 */
	public static double getAngularVelocityDegPS() {
		return -navx.getRate();
	}

	/**
	 * @return the rate of change in the angle on the Z
	 *         axis (perpendicular to earth, "yaw") in radians.
	 *         Rotating clockwise returns a positive value, and
	 *         counter-clockwise returns a negative value.
	 */
	public static double getAngularVelocityRadPS() {
		return Math.toRadians(-navx.getRate());
	}

	/**
	 * Sets the current angle on the Z axis (perpendicular to earth, "yaw") as
	 * zero. Can be used to set the angle the robot is currently facing as the
	 * front. The offset that was set with setYawOffsetDeg/Rad/Rotation2d is
	 * cleared.
	 */
	public static void resetYaw() {
		navx.zeroYaw();
		yawOffsetDeg = 0;
	}

	public static void setYawOffsetDeg(double offsetDeg) {
		yawOffsetDeg = offsetDeg;
	}

	public static void setYawOffsetRad(double offsetRad) {
		yawOffsetDeg = Math.toDegrees(offsetRad);
	}

	public static void setYawOffsetRotation2d(Rotation2d offsetRotation2d) {
		yawOffsetDeg = offsetRotation2d.getDegrees();
	}
}
