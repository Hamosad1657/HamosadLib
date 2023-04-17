
package com.hamosad1657.lib.math;

import com.pathplanner.lib.auto.PIDConstants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class HaUnits {
	public static final double kCANCoderTicksPerRev = 4096;
	public static double kChargedUpFieldLength = 16.5;

	public enum Velocity {
		kRPM, kMPS, kRadPS, kDegPS,
	}

	public enum Position {
		kRad, kDeg, kRot;
	}

	/**
	 * Represents a set of PID, feedforward and iZone values.
	 */
	public static class PIDGains {
		public double kP, kI, kD, kFF, kIZone;

		/**
		 * @param kP    - Proportional gain.
		 * @param kI    - Integral gain.
		 * @param kD    - Derivative gain.
		 * @param kFF   - Feed Forward gain.
		 * @param iZone - If the absolute error is above iZone, the integral accumulator is cleared (making it
		 *              ineffective).
		 */
		public PIDGains(double kP, double kI, double kD, double kFF, double iZone) {
			this.kP = kP;
			this.kI = kI;
			this.kD = kD;
			this.kFF = kFF;
			this.kIZone = iZone;
		}

		/**
		 * @param kP  - Proportional gain.
		 * @param kI  - Integral gain.
		 * @param kD  - Derivative gain.
		 * @param kFF - Feed Forward gain.
		 */
		public PIDGains(double kP, double kI, double kD, double kFF) {
			this(kP, kI, kD, kFF, 0.0);
		}

		/**
		 * @param kP - Proportional gain.
		 * @param kI - Integral gain.
		 * @param kD - Derivative gain.
		 */
		public PIDGains(double kP, double kI, double kD) {
			this(kP, kI, kD, 0.0, 0.0);
		}

		public PIDGains() {
			this(0.0, 0.0, 0.0);
		}

		public PIDController toPIDController() {
			return new PIDController(this.kP, this.kI, this.kD);
		}

		public ProfiledPIDController toProfiledPIDController(TrapezoidProfile.Constraints constraints) {
			return new ProfiledPIDController(this.kP, this.kI, this.kD, constraints);
		}

		public PIDConstants toPathPlannerPIDConstants() {
			return new PIDConstants(this.kP, this.kI, this.kD);
		}

	}

	public static double deadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			return (value - deadband * Math.signum(value)) / (1.0 - deadband);
		} else {
			return 0.0;
		}
	}

	/**
	 * Gets a start range defined by [startMin] and [startMax] and an end range defined by [endMin] and [endMax], and a
	 * value that is relative to the first range.
	 * 
	 * @return The value relative to the end range.
	 */
	public static double mapRange(double value, double startMin, double startMax, double endMin, double endMax) {
		return endMin + ((endMax - endMin) / (startMax - startMin)) * (value - startMin);
	}
}
