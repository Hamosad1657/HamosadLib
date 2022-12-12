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

	/**
	 * Represents a set of Proportional, Integral, Derivative and IZone values.
	 */
	public static class PIDGains {
		/**
		 * @param p Proportional
		 * @param i Integral
		 * @param d Derivative
		 * @param iZone if the absloute error is above iZone, the integral
		 * 				accumulator is cleared (making it ineffective).
		 * @throws IllegalArgumentException If any of the values are negative.
		 */
		public PIDGains(double p, double i, double d, double iZone) throws IllegalArgumentException {
			if (p < 0 || i < 0 || d < 0) {
				throw new IllegalArgumentException("PID gains cannot be negative");
			}
			else if (iZone < 0) {
				throw new IllegalArgumentException("IZone cannot be negative");
			}
			else {
				this.p = p;
				this.i = i;
				this.d = d;
				this.iZone = iZone;
			}
		}

		public double p;
		public double i;
		public double d;
		public double iZone;
	}
}
