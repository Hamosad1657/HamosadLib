
package com.hamosad1657.lib.math;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;

public class HaUnitConvertor {
	// for refrence
	// https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/

	private static final double inchsInMeter = 39.3700787402;

	/** Degrees to radians. */
	public static double degToRad(double deg) {
		return Math.toRadians(deg);
	}

	/** Radians to degrees. */
	public static double radToDeg(double rad) {
		return Math.toDegrees(rad);
	}

	/** Degrees per second to rotations per minute. */
	public static double degPSToRPM(double degPS) {
		return (degPS / 360) / 60;
	}

	/** Rotations per minute to degrees per seconds. */
	public static double RPMToDegPS(double RPM) {
		return (RPM * 60) * 360;
	}

	/** Radians per second to rotations per minute. */
	public static double radPSToRPM(double radPS) {
		return (radPS / (Math.PI * 2)) / 60;
	}

	/** Rotations per minute to radians per second. */
	public static double RPMToRadPS(double RPM) {
		return (RPM * 60) * (Math.PI * 2);
	}

	/**
	 * Radians per second to meters per second.
	 * 
	 * @throws IllegalArgumentException If wheel radius is negative.
	 */
	public static double radPSToMPS(double radPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0)
			return RPMToMPS(radPSToRPM(radPS), wheelRadiusMeters);
		else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/**
	 * Degrees per second to meters per second.
	 * 
	 * @param degPS
	 * @param wheelRadiusMeters
	 * @return
	 * @throws IllegalArgumentException If wheel radius is negative.
	 */
	public static double degPSToMPS(double degPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0)
			return RPMToMPS(degPSToRPM(degPS), wheelRadiusMeters);
		else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/** Meters per second to degrees per second. */
	public static double MPSToDegPS(double MPS, double wheelRadiusM) {
		return RPMToDegPS(MPSToRPM(MPS, wheelRadiusM));
	}

	/**
	 * Meters per second to rotations per minute.
	 * 
	 * @throws IllegalArgumentException If wheel radius is negative.
	 */
	public static double MPSToRPM(double MPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0)
			return 60 / (2 * Math.PI * wheelRadiusMeters) * MPS;
		else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/**
	 * Rotations per minute to meters per second.
	 * 
	 * @param RPM
	 * @param wheelRadiusMeters
	 * @return
	 * @throws IllegalArgumentException If wheel radius is negative.
	 */
	public static double RPMToMPS(double RPM, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0)
			return wheelRadiusMeters * ((2 * Math.PI) / 60) * RPM;
		else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/** Meters to inches. */
	public static double metersToInches(double meters) {
		return meters * inchsInMeter;
	}

	/** Inches to meters. */
	public static double inchesToMeters(double inches) {
		return inches / inchsInMeter;
	}

	public static double degPSToCANCoderTicksPer100ms(double degPS) {
		return degreesToCANCoderTicks(degPS) / 10;
	}

	/**
	 * Convert degrees to CANCoder ticks with no gear ratio. An overload exists with a gear ratio.
	 * 
	 * @param degrees - Degrees of rotation of the mechanism.
	 * @return CANCoder position counts (4096)
	 */
	public static double degreesToCANCoderTicks(double degrees) {
		return (degrees / 360.0) * HaUnits.kCANCoderTicksPerRev;
	}

	/**
	 * @param positionCounts - CANCoder position counts (4096)
	 * @param gearRatio      - Gear Ratio between CANCoder and mechanism.
	 * @return Degrees of rotation of mechanism
	 */
	public static double CANCoderTicksToDegrees(double positionCounts, double gearRatio) {
		return positionCounts * (360.0 / (gearRatio * 4096.0));
	}

	/**
	 * @param degrees   - Degrees of rotation of mechanism
	 * @param gearRatio - Gear ratio between CANCoder and mechanism
	 * @return CANCoder position counts (4096)
	 */
	public static double degreesToCANCoderTicks(double degrees, double gearRatio) {
		return degrees / (360.0 / (gearRatio * 4096.0));
	}

	/**
	 * @param positionCounts - Falcon position counts in integrated encoder ticks (2048)
	 * @param gearRatio      - Gear ratio between Falcon and mechanism
	 * @return Degrees of rotation of mechanism
	 */
	public static double falconTicksToDegrees(double positionCounts, double gearRatio) {
		return positionCounts * (360.0 / (gearRatio * 2048.0));
	}

	/**
	 * @param degrees   - Degrees of rotation of mechanism
	 * @param gearRatio - Gear ratio between Falcon and mechanism
	 * @return Falcon position counts in integrated encoder ticks (2048)
	 */
	public static double degreesToFalconTicks(double degrees, double gearRatio) {
		return degrees / (360.0 / (gearRatio * 2048.0));
	}

	/**
	 * @param velocityCounts - Falcon integrated encoder ticks per 100ms
	 * @param gearRatio      - Gear ratio between Falcon and mechanism (set to 1 for Falcon RPM)
	 * @return RPM of mechanism
	 */
	public static double falconTicksToRPM(double velocityCounts, double gearRatio) {
		double motorRPM = velocityCounts * (600.0 / 2048.0);
		double mechRPM = motorRPM / gearRatio;
		return mechRPM;
	}

	/**
	 * @param RPM       - RPM of mechanism
	 * @param gearRatio - Gear ratio between Falcon and mechanism (set to 1 for Falcon RPM)
	 * @return RPM of Mechanism
	 */
	public static double RPMToFalconTicks(double RPM, double gearRatio) {
		double motorRPM = RPM * gearRatio;
		double sensorCounts = motorRPM * (2048.0 / 600.0);
		return sensorCounts;
	}

	/**
	 * @param velocityCounts      - Falcon integrated encoder ticks per 100ms
	 * @param wheelCircumferenceM - Circumference of wheel in meters
	 * @param gearRatio           - Gear Ratio between Falcon and mechanism (set to 1 for Falcon MPS)
	 * @return Falcon Velocity Counts
	 */
	public static double falconTicksToMPS(double velocityCounts, double wheelCircumferenceM, double gearRatio) {
		double wheelRPM = falconTicksToRPM(velocityCounts, gearRatio);
		double wheelMPS = (wheelRPM * wheelCircumferenceM) / 60;
		return wheelMPS;
	}

	/**
	 * @param MPS                 - Velocity in meters per second
	 * @param wheelCircumferenceM - Circumference of wheel in meters
	 * @param gearRatio           - Gear Ratio between Falcon and Mechanism (set to 1 for Falcon MPS)
	 * @return Falcon Velocity Counts
	 */
	public static double MPSToFalconTicks(double MPS, double wheelCircumferenceM, double gearRatio) {
		double wheelRPM = ((MPS * 60) / wheelCircumferenceM);
		double wheelVelocity = RPMToFalconTicks(wheelRPM, gearRatio);
		return wheelVelocity;
	}

	/**
	 * @param positionCounts      - Falcon position counts in integrated encoder ticks (2048)
	 * @param wheelCircumferenceM - Circumference of wheel in meters
	 * @param gearRatio           - Gear Ratio between Falcon and Wheel
	 * @return Meters
	 */
	public static double falconTicksToMeters(double positionCounts, double wheelCircumferenceM, double gearRatio) {
		return positionCounts * (wheelCircumferenceM / (gearRatio * 2048.0));
	}

	/**
	 * @param meters              - Meters
	 * @param wheelCircumferenceM - Circumference of wheel
	 * @param gearRatio           - Gear ratio between Falcon and wheel
	 * @return Falcon position counts in integrated encoder ticks (2048)
	 */
	public static double metersToFalconTicks(double meters, double wheelCircumferenceM, double gearRatio) {
		return meters / (wheelCircumferenceM / (gearRatio * 2048.0));
	}

	/**
	 * 
	 * @param position - MUST be blue alliance
	 * @return New position relative to robot's alliance.
	 */
	public static Pose2d matchPoseToAlliance(Pose2d position) {
		return DriverStation.getAlliance() == DriverStation.Alliance.Blue ? position
				: new Pose2d(HaUnits.kChargedUpFieldLength - position.getX(), position.getY(),
						position.getRotation().rotateBy(Rotation2d.fromDegrees(180.0)));
	}

}
