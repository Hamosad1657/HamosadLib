package com.hamosad1657.lib;

public class HaUnitConvertor {
	// for refrence
	// https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/

	/// Meters per Second to Rotations per Minute
	static public double convertMPStoRPM(double MPS, double wheelRadius) {
		if (wheelRadius > 0) {
			return 60 / (2 * Math.PI * wheelRadius) * MPS;
		} else
			return -1;
	}

	/// Rotations per Minute to Meters per Second
	static public double convertRPMtoMPS(double RPM, double wheelRadius) {
		if (wheelRadius > 0) {
			return wheelRadius * ((2 * Math.PI) / 60) * RPM;
		} else
			return -1;
	}

	/// Meters to Inchs
	static public double metersToInchs(double meter) {
		return meter * RobotConstants.ConvertionConstants.inchsInMeter;
	}

	/// Inchs to Meters
	static public double inchsTometer(double inch) {
		return inch / RobotConstants.ConvertionConstants.inchsInMeter;
	}

}
