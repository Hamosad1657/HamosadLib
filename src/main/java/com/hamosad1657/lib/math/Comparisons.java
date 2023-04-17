
package com.hamosad1657.lib.math;

public class Comparisons {
	/**
	 * @param currentAngleDeg
	 * @param desiredAngleDeg
	 * @param toleranceDeg
	 * @param cartisan        - Is the angle between 0 and 360, or between 180 and -180. True if the latter.
	 * @return Whether current angle is within the tolerance for the desired angle.
	 */
	boolean isAngleInTolerance(double currentAngleDeg, double desiredAngleDeg, double toleranceDeg, boolean cartisan) {
		double errorDeg;
		if (cartisan) {
			errorDeg = (180 - currentAngleDeg) - desiredAngleDeg;
		} else {
			errorDeg = (360 - currentAngleDeg) - desiredAngleDeg;
		}
		return Math.abs(errorDeg) < toleranceDeg;
	}

	/**
	 * @param currentAngleDeg - Between 0 to 360
	 * @param desiredAngleDeg - Between 0 to 360
	 * @param toleranceDeg
	 * @return Whether current angle is within the tolerance for the desired angle.
	 */
	boolean isAngleInTolerance(double currentAngleDeg, double desiredAngleDeg, double toleranceDeg) {
		double errorDeg = (360 - currentAngleDeg) - desiredAngleDeg;
		return Math.abs(errorDeg) < toleranceDeg;
	}
}
