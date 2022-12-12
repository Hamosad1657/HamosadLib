package com.hamosad1657.lib;

public class HaUnitConvertor {
	// for refrence
	// https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/

	// Degrees to radians
	public static double degToRad(double degrees) {
		return Math.toRadians(degrees);
	}

	// Radians to degrees
	public static double radToDeg(double radians) {
		return Math.toDegrees(radians);
	}

	// radps to degps
	public static double radPSToDegPS(double radiansPerSecond) {
		return Math.toDegrees(radiansPerSecond);
	}

	// degps to radps
	public static double degPSToRadPS(double degreesPerSecond) {
		return Math.toRadians(degreesPerSecond);
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