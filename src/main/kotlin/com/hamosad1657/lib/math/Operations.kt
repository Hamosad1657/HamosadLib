package com.hamosad1657.lib.math

import edu.wpi.first.math.MathUtil
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sign

fun deadband(value: Double, deadband: Double): Double {
	return if (abs(value) > deadband) {
		(value - deadband * sign(value)) / (1.0 - deadband)
	} else {
		0.0
	}
}

fun clamp(value: Double, min: Double, max: Double): Double {
	return if (min >= max) 0.0 else MathUtil.clamp(value, min, max)
}

/**
 * Gets a start range defined by [startMin] and [startMax] and an end range defined by [endMin] and [endMax], and a
 * value that is relative to the first range.
 *
 * @return The value relative to the end range.
 */
fun mapRange(value: Double, startMin: Double, startMax: Double, endMin: Double, endMax: Double): Double {
	return endMin + (endMax - endMin) / (startMax - startMin) * (value - startMin)
}

/**
 * Gets a start range defined by [startMin] and [startMax] and an end range defined by [endMin] and [endMax], and a
 * value that is relative to the first range.
 *
 * @return The value relative to the end range.
 */
fun mapRange(value: Int, startMin: Int, startMax: Int, endMin: Int, endMax: Int): Int {
	return endMin + (endMax - endMin) / (startMax - startMin) * (value - startMin)
}

fun median(collection: Collection<Double>): Double {
	return median(collection.toDoubleArray())
}

fun median(array: Array<Double>): Double {
	return median(array.toDoubleArray())
}

fun median(array: DoubleArray): Double {
	val sortedArray = array.sorted()
	val size = sortedArray.size
	if (size == 2) return sortedArray.average()

	return if (size % 2 == 0) {
		(sortedArray[size / 2] + sortedArray[(size / 2) - 1]) / 2.0
	} else {
		array[((size / 2) - 0.5).toInt()]
	}
}

/**
 * Modify the setpoint to always go the shorter way in position control.
 *
 * Returns a new setpoint that will produce the shortest path to [realSetpoint], using the
 * [measurement] (which isn't required to be inside of [minMeasurement] and [maxMeasurement]).
 *
 * The [minMeasurement] and [maxMeasurement] define the range where the wrapping will occur.
 *
 * ## Example
 * Say I want to control the angle of a swerve module (we'll use degrees for convenience).
 * Zero and 360 are the same position in reality, and it can cross this position with no
 * problem physically. The PID runs onboard the motor controller, not the RoboRIO.
 *
 * Now, imagine this situation: The wheel is now at 10 degrees, and the setpoint is 350.
 * Without this function, the motor will move 340 degrees all the way around, even though
 * in reality it's only 20 degrees away.
 *
 * To solve the above problem, use this function! pass 350.0 for realSetpoint, 10.0 for
 * measurement, 0.0 for minMeasurement, 360.0 for maxMeasurement, and 360 for ticksInRotation.
 * This function will return a new setpoint, which is then set to the motor controller in
 * position control mode and will make it go the shorter way.
 *
 * - Note that measurement is allowed to accumulate beyond minMeasurement and maxMeasurement,
 * but it needs to correspond to the same position in the original scope. for example, a
 * measurement of 361 must be the same module angle as measurement 1.
 *
 * * DO NOT use this function if your mechanism cannot move freely in every direction, like
 * in a turret with finite rotation. Also don't use it for controlling linear motion, like
 * a telescopic arm or an elevator.
 */
fun wrapPositionSetpoint(
	realSetpoint: Double,
	measurement: Double,
	minMeasurement: Double,
	maxMeasurement: Double,
	ticksInRotation: Int
): Double {
	require(minMeasurement < maxMeasurement)
	require(realSetpoint in minMeasurement..maxMeasurement)

	// This is done in case the measurement doesn't wrap already (e.g. is accumulated forever, and could theoretically be infinite)
	val wrappedMeasurement = MathUtil.inputModulus(measurement, minMeasurement, maxMeasurement)

	val realError = realSetpoint - wrappedMeasurement
	val maxRealError = maxMeasurement - minMeasurement

	val minModifiedError = maxRealError / -2.0
	val maxModifiedError = maxRealError / 2.0

	val modifiedError = MathUtil.inputModulus(realError, minModifiedError, maxModifiedError)

	val modifiedSetpoint = modifiedError + wrappedMeasurement // same as [error = setpoint - measurement]

	val fullRotationsFromZero = if(measurement < 0) ceil(measurement / ticksInRotation) else floor(measurement / ticksInRotation)
	return modifiedSetpoint + fullRotationsFromZero * ticksInRotation
}