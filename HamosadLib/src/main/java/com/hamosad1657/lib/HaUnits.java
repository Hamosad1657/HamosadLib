// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib;

/** Add your docs here. */
public class HaUnits {
	public enum Velocities {
		kRPM, kMPS, kRadPS, kDegPS,

	}

	public enum Positions {
		kRad, kDegrees, kRotations;
	}

	public static class PIDGains {
		public PIDGains(double d, double i, double p, double iZone) {
			this.p = p;
			this.i = i;
			this.d = d;
			this.iZone = iZone;
		}

		public double p;
		public double i;
		public double d;
		public double iZone;
	}
}
