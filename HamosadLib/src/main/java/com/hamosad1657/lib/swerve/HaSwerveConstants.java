package com.hamosad1657.lib.swerve;

public class HaSwerveConstants {
	public static final double kTalonFXIntegratedEncoderTicksPerRev = 2048;
	public static final double kTalonFXIntegratedEncoderTicksPerDegree = 2048/360.0;

	// Module angles for cross-locking the wheels
	public static final double kFrontLeftCrossAngleDegrees = 45;
	public static final double kFrontRightCrossAngleDegrees = 135;
	public static final double kBackLeftCrossAngleDegrees = 135;
	public static final double kBackRightCrossAngleDegrees = 45;
}
