package com.hamosad1657.lib.units

import com.hamosad1657.lib.robotPrintError
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation.Alliance

const val INCHES_IN_METER = 39.3700787402
val CRESCENDO_FIELD_LENGTH = Length.fromMeters(16.54)
val CRESCENDO_FIELD_WIDTH = Length.fromMeters(8.21)

/// --- Angles to Angles Conversions ---

/** Degrees to radians.  */
fun degToRad(deg: Number) = Math.toRadians(deg.toDouble())

/** Radians to degrees.  */
fun radToDeg(rad: Number) = Math.toDegrees(rad.toDouble())


/// --- Angular Velocities to Angular Velocities Conversions ---

/** Rotations per minute to rotations per second. */
fun rpmToRps(rpm: Number) = rpm.toDouble() / 60.0

/** Rotations per minute to radians per second. */
fun rpmToRadPs(rpm: Number) = rpm.toDouble() / 60.0 * (Math.PI * 2.0)

/** Rotations per minute to degrees per seconds. */
fun rpmToDegPs(rpm: Number) = rpm.toDouble() / 60.0 * 360.0

/** Rotations per second to rotations per minute. */
fun rpsToRpm(rps: Number) = rps.toDouble() * 60.0

/** Rotations per second to radians per second. */
fun rpsToRadPs(rps: Number) = rps.toDouble() * Math.PI * 2.0

/** Rotations per second to degrees per second. */
fun rpsToDegPs(rps: Number) = rps.toDouble() * 360.0

/** Radians per second to rotations per minute. */
fun radPsToRpm(radPs: Number) = radPs.toDouble() / (Math.PI * 2.0) * 60.0

/** Radians per second ro rotations per second. */
fun radPsToRps(radPs: Number) = radPs.toDouble() / (Math.PI * 2.0)

/** Radians per second to degrees per second. */
fun radPsToDegPs(radPs: Number) = Math.toDegrees(radPs.toDouble())

/** Degrees per second to rotations per minute. */
fun degPsToRpm(degPs: Number) = degPs.toDouble() / 360.0 * 60.0

/** Degrees per second to rotations per second. */
fun degPsToRps(degPs: Number) = degPs.toDouble() / 360.0

/** Degrees per second to radians per second. */
fun degPsToRadPs(degPs: Number) = Math.toRadians(degPs.toDouble())


/// --- Angular Velocities to Linear Velocity Conversions ---

/** Rotations per minute to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun rpmToMps(rpm: Number, wheelRadius: Length): Double {
	if (wheelRadius.asMeters <= 0.0) {
		robotPrintError("wheelRadius is negative or zero: $wheelRadius \nReturning zero MPS.", true)
		return 0.0
	}
	return rpm.toDouble() / 60.0 * (wheelRadius.asMeters * 2.0 * Math.PI)
}

/** Radians per second to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun radPsToMps(radPS: Number, wheelRadius: Length) = rpmToMps(radPsToRpm(radPS), wheelRadius)

/** Degrees per second to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun degPsToMps(degPs: Number, wheelRadius: Length) = rpmToMps(degPsToRpm(degPs), wheelRadius)

/** Meters per second to rotations per minute.
 *
 * Wheel radius should be greater than 0. */
fun mpsToRpm(mps: Number, wheelRadius: Length): Double {
	if (wheelRadius.asMeters <= 0.0) {
		robotPrintError("wheelRadius is negative or zero: $wheelRadius \nReturning zero RPM.", true)
		return 0.0
	}
	return mps.toDouble() * 60.0 / (wheelRadius.asMeters * 2.0 * Math.PI)
}

/** Meters per second to radians per second.
 *
 * Wheel radius should be greater than 0. */
fun mpsToRadPs(mps: Number, wheelRadius: Length) = rpmToRadPs(mpsToRpm(mps, wheelRadius))

/** Meters per second to degrees per second.
 *
 * Wheel radius should be greater than 0. */
fun mpsToDegPs(mps: Number, wheelRadius: Length) = rpmToDegPs(mpsToRpm(mps, wheelRadius))


/// --- Lengths to Lengths Conversions ---

/** Meters to inches. */
fun metersToInches(meters: Number) = meters.toDouble() * INCHES_IN_METER

/** Meters to feet.  */
fun metersToFeet(meters: Number) = inchesToFeet(metersToInches(meters))

/** Inches to meters. */
fun inchesToMeters(inches: Number) = inches.toDouble() / INCHES_IN_METER

/** Inches to feet. */
fun inchesToFeet(inches: Number) = inches.toDouble() / 12.0

/** Feet to meters.  */
fun feetToMeters(feet: Number) = inchesToMeters(feetToInches(feet))

/** Feet to inches.  */
fun feetToInches(feet: Number) = feet.toDouble() * 12.0

/**
 * @param position - MUST be blue alliance.
 * @param alliance - The current alliance.
 * @return New position relative to robot's alliance.
 */
fun matchPoseToAlliance(position: Pose2d, alliance: Alliance): Pose2d {
	return when (alliance) {
		Alliance.Blue -> position
		Alliance.Red ->
			Pose2d(
				CRESCENDO_FIELD_LENGTH.asMeters - position.x,
				position.y,
				position.rotation.rotateBy(180.degrees)
			)
	}
}