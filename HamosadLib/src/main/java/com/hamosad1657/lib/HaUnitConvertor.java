package com.hamosad1657.lib;

public class HaUnitConvertor {
	// for refrence
	// https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/


	private static final double inchsInMeter = 39.3700787402;
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

	/** Radians per second to meters per second */
	public static double radPSToMPS(double radPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0) {
			return RPMToMPS(radPSToRPM(radPS), wheelRadiusMeters);
		} else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}
	
	/** Degrees per second to meters per second */
	public static double degPSToMPS(double degPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0) {
			return RPMToMPS(degPSToRPM(degPS), wheelRadiusMeters);
		} else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/// Meters per Second to Rotations per Minute
	static public double MPSToRPM(double MPS, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0) {
			return 60 / (2 * Math.PI * wheelRadiusMeters) * MPS;
		} else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/// Rotations per Minute to Meters per Second
	static public double RPMToMPS(double RPM, double wheelRadiusMeters) throws IllegalArgumentException {
		if (wheelRadiusMeters > 0) {
			return wheelRadiusMeters * ((2 * Math.PI) / 60) * RPM;
		} else
			throw new IllegalArgumentException("Wheel radius must be positive");
	}

	/// Meters to Inchs
	static public double metersToInches(double meters) {
		return meters * inchsInMeter;
	}

	/// Inchs to Meters
	static public double inchesToMeters(double inches) {
		return inches / inchsInMeter;
	}

}
