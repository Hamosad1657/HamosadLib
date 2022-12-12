// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib;

/** Add your docs here. */
public class HaUnits {
	public enum Velocities {
		RPM, MPS, RadPS, DegPS, Raw,

	}

	public enum Positions {
		Rad, Degrees
	}

	public static class PIDGains {
		public PIDGains(int d, int i, int p) {
			this.p = p;
			this.i = i;
			this.d = d;
		}

		public int p;
		public int i;
		public int d;
	}
}
