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
 * Modify the setpoint to wrap position.
 *
 * Returns a new setpoint that will produce the shortest path to [realSetpoint], using the
 * [measurement] (which isn't required to be inside of [minMeasurement] and [maxMeasurement]).
 *
 * The [minMeasurement] and [maxMeasurement] define the range where the wrapping will occur.
 *
 * ## Example
 * TODO: Write an example.
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