package com.hamosad1657.lib.swerve;

public class HaSwerveConstants {
	public static final double kTalonFXIntegratedEncoderTicksPerRev = 2048;
	public static final double kTalonFXIntegratedEncoderTicksPerDeg = 2048 / 360.0;

	// Module angles for cross-locking the wheels
	public static final double kFrontLeftCrossAngleDeg = 45;
	public static final double kFrontRightCrossAngleDeg = 135;
	public static final double kBackLeftCrossAngleDeg = 135;
	public static final double kBackRightCrossAngleDeg = 45;
}
