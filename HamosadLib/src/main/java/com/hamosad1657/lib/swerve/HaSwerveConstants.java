package com.hamosad1657.lib.swerve;

public class HaSwerveConstants {
	public static final double kTalonFXIntegratedEncoderTicksPerRev = 2048;
	public static final double kTalonFXIntegratedEncoderTicksPerDegree = 2048/360.0;
	public static final double kWheelDiameterCM = 10.16;
	public static final double kWheelCircumferenceCM = kWheelDiameterCM * Math.PI;
	public static final double kWheelCircumferenceMeters = kWheelCircumferenceCM / 10;
}
