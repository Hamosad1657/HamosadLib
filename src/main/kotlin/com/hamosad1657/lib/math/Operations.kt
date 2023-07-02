package com.hamosad1657.lib.math

import kotlin.math.abs
import kotlin.math.sign

fun deadband(value: Double, deadband: Double): Double {
    return if (abs(value) > deadband) {
        (value - deadband * sign(value)) / (1.0 - deadband)
    } else {
        0.0
    }
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

fun median(collection: Collection<Double>): Double {
    return median(collection.toDoubleArray())
}

fun median(array: Array<Double>): Double {
    return median(array.toDoubleArray())
}

fun median(array: DoubleArray): Double {
    val sortedArray = array.sorted()
    val size = sortedArray.size
    if(size == 2) return sortedArray.average()

    return if(size % 2 == 0) {
        (sortedArray[size / 2] + sortedArray[(size / 2) - 1]) / 2.0
    } else {
        array[((size / 2) - 0.5).toInt()]
    }
}