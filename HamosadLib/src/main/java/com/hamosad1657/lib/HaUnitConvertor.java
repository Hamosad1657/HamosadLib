package com.hamosad1657.lib;

public class HaUnitConvertor {
	// for refrence
	// https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/

	/** Degrees to radians */
	public static double degToRad(double deg) {
		return Math.toRadians(deg);
	}

	/** Radians to degrees */
	public static double radToDeg(double rad) {
		return Math.toDegrees(rad);
	}

	/** Radians per second to degrees per second */
	public static double radPSToDegPS(double radPS) {
		return Math.toDegrees(radPS);
	}

	/** Degrees per second to radians per second */
	public static double degPSToRadPS(double degPS) {
		return Math.toRadians(degPS);
	}

	/** Degrees per second to rotations per minute */
	public static double degPSToRPM(double degPS) {
		return (degPS / 360) / 60;
	}

	/** Rotations per minute to degrees per secons */
	public static double RPMToDegPS(double RPM) {
		return (RPM * 60) * 360;
	}

	/** Radians per second to rotations per minute */
	public static double radPSToRPM(double radPS) {
		return (radPS / (Math.PI * 2)) / 60;
	}

	/** Rotations per minute to radians per second */
	public static double RPMToRadPS(double RPM) {
		return (RPM * 60) * (Math.PI * 2);
	}

	/// Meters per Second to Rotations per Minute
	static public double MPStoRPM(double MPS, double wheelRadius) {
		if (wheelRadius > 0) {
			return 60 / (2 * Math.PI * wheelRadius) * MPS;
		} else
			return -1;
	}

	/// Rotations per Minute to Meters per Second
	static public double RPMtoMPS(double RPM, double wheelRadius) {
		if (wheelRadius > 0) {
			return wheelRadius * ((2 * Math.PI) / 60) * RPM;
		} else
			return -1;
	}

	/// Meters to Inchs
	static public double metersToInches(double meters) {
		return meters * RobotConstants.ConvertionConstants.inchsInMeter;
	}

	/// Inchs to Meters
	static public double inchesToMeters(double inches) {
		return inches / RobotConstants.ConvertionConstants.inchsInMeter;
	}

}
/*
 * class
 * Make all kind of converstions
 * for all the types of rotational velocities ( not including raw)
 */