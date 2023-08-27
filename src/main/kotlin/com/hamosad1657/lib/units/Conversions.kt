package com.hamosad1657.lib.units

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.DriverStation

const val ChargedUpFieldLengthM = 16.7 // TODO: Update to current field's length.
const val INCHES_IN_METER = 39.3700787402
const val CANCODER_TICKS_PER_REVOLUTION = 4096.0
const val FALCON_TICKS_PER_REVOLUTION = 2048.0


/// --- Angles to Angles Conversions ---

/** Degrees to radians.  */
fun degToRad(deg: Double) = Math.toRadians(deg)

/** Radians to degrees.  */
fun radToDeg(rad: Double) = Math.toDegrees(rad)


/// --- Angular Velocities to Angular Velocities Conversions ---

/** Rotations per minute to radians per second. */
fun rpmToRadPs(rpm: Double) = rpm * 60.0 * (Math.PI * 2.0)

/** Rotations per minute to degrees per seconds. */
fun rpmToDegPs(rpm: Double) = rpm * 60.0 * 360.0

/** Radians per second to rotations per minute. */
fun radPsToRpm(radPs: Double) = radPs / (Math.PI * 2.0) / 60.0

/** Radians per second to degrees per second. */
fun radPsToDegPs(radPs: Double) = radPs * (180.0 / Math.PI)

/** Degrees per second to rotations per minute. */
fun degPsToRpm(degPs: Double) = degPs / 360.0 / 60.0

/** Degrees per second to radians per second. */
fun degPsToRadPs(degPs: Double) = degPs * (Math.PI / 180.0)


/// --- Angular Velocities to Linear Velocity Conversions ---

/** Rotations per minute to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun rpmToMps(rpm: Double, wheelRadius: Length) =
	require(wheelRadius.meters > 0.0).run { wheelRadius.meters * (2.0 * Math.PI / 60.0) * rpm }

/** Radians per second to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun radPsToMps(radPS: Double, wheelRadius: Length): Double = rpmToMps(radPsToRpm(radPS), wheelRadius)

/** Degrees per second to meters per second.
 *
 * Wheel radius should be greater than 0. */
fun degPsToMps(degPs: Double, wheelRadius: Length) = rpmToMps(degPsToRpm(degPs), wheelRadius)

/** Meters per second to rotations per minute.
 *
 * Wheel radius should be greater than 0. */
fun mpsToRpm(mps: Double, wheelRadius: Length) =
	require(wheelRadius.meters > 0.0).run { 60.0 / (2.0 * Math.PI * wheelRadius.meters) * mps }

/** Meters per second to radians per second.
 *
 * Wheel radius should be greater than 0. */
fun mpsToRadPs(mps: Double, wheelRadius: Length) = rpmToRadPs(mpsToRpm(mps, wheelRadius))

/** Meters per second to degrees per second.
 *
 * Wheel radius should be greater than 0. */
fun mpsToDegPs(mps: Double, wheelRadius: Length) = rpmToDegPs(mpsToRpm(mps, wheelRadius))


/// --- Lengths to Lengths Conversions ---

/** Meters to inches. */
fun metersToInches(meters: Double) = meters * INCHES_IN_METER

/** Meters to feet.  */
fun metersToFeet(meters: Double) = inchesToFeet(metersToInches(meters))

/** Inches to meters. */
fun inchesToMeters(inches: Double) = inches / INCHES_IN_METER

/** Inches to feet. */
fun inchesToFeet(inches: Double) = inches / 12.0

/** Feet to meters.  */
fun feetToMeters(feet: Double) = inchesToMeters(feetToInches(feet))

/** Feet to inches.  */
fun feetToInches(feet: Double) = feet * 12.0


/// --- CANCoder Ticks to Degrees Conversions ---

/**
 * Convert degrees to CANCoder ticks.
 * @param gearRatio Gear ratio between Falcon and mechanism
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun degToCANCoderTicks(deg: Double, gearRatio: Double = 1.0) =
	deg / 360.0 * gearRatio * CANCODER_TICKS_PER_REVOLUTION

/**
 * CANCoder ticks to degrees.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun CANCoderTicksToDeg(ticks: Double, gearRatio: Double = 1.0) =
	ticks / gearRatio / CANCODER_TICKS_PER_REVOLUTION * 360.0

/**
 * Degrees per second to CANCoder ticks per 100ms.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun degPsToCANCoderTicksPer100ms(degPs: Double, gearRatio: Double = 1.0) =
	degToCANCoderTicks(degPs, gearRatio) / 10.0


/// --- Falcon Ticks to Angles and Angular Velocities Conversions ---

/**
 * Falcon's integrated encoder ticks (2048 per revolution) to degrees.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun degToFalconTicks(deg: Double, gearRatio: Double = 1.0) =
	deg / 360.0 * gearRatio * FALCON_TICKS_PER_REVOLUTION

/**
 * Falcon's integrated encoder ticks (2048 per revolution) to degrees.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun falconTicksToDeg(ticks: Double, gearRatio: Double = 1.0) =
	ticks / gearRatio / FALCON_TICKS_PER_REVOLUTION * 360.0

/**
 * Revolutions per minute to Falcon's integrated encoder ticks (2048 per revolution) per 100ms.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun rpmToFalconTicksPer100ms(rpm: Double, gearRatio: Double = 1.0) =
	rpm / 600.0 * gearRatio * FALCON_TICKS_PER_REVOLUTION

/**
 * Falcon's integrated encoder ticks (2048 per revolution) per 100ms tp revolutions per minute.
 * @param gearRatio Gear ratio between Falcon and mechanism.
 * (e.g. 3.0 means that for each 3 rotations the Falcon makes, the mechanism makes 1).
 */
fun falconTicksPer100msToRpm(ticksPer100ms: Double, gearRatio: Double = 1.0) =
	ticksPer100ms / gearRatio / FALCON_TICKS_PER_REVOLUTION * 600.0

/**
 *
 * @param position - MUST be Blue Alliance.
 * @return New position relative to robot's alliance.
 */
fun matchPoseToAlliance(position: Pose2d): Pose2d =
	when (DriverStation.getAlliance()) {
		DriverStation.Alliance.Blue -> position
		DriverStation.Alliance.Red ->
			Pose2d(
				ChargedUpFieldLengthM - position.x,
				position.y,
				position.rotation.rotateBy(180.degrees)
			)

		else -> throw IllegalStateException("Alliance invalid or can't fetch alliance from DriverStation")
	}