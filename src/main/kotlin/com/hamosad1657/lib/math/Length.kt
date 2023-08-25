package com.hamosad1657.lib.math

import com.hamosad1657.lib.units.*

class Length : Comparable<Length> {
    var meters = 0.0
        set(value) {
            require(!value.isNaN()) {" Length cannot contain NaN "}
            require(value.isFinite()) {" Length cannot contain infinity "}
            field = value
        }

    var centimeters: Double = getLength(LengthUnit.Centimeters)
        get() = getLength(LengthUnit.Centimeters)
        set(value) {
            field = value
            this.meters = value / 100.0
        }
    var millimeters: Double = getLength(LengthUnit.Millimeters)
        get() = getLength(LengthUnit.Millimeters)
        set(value) {
            field = value
            this.meters = value / 1000.0
        }
    var inches: Double  = getLength(LengthUnit.Millimeters)
        get() = getLength(LengthUnit.Inches)
        set(value) {
            field = value
            this.meters = inchesToMeters(value)
        }
    var ft: Double = getLength(LengthUnit.Millimeters)
        get() = getLength(LengthUnit.Feet)
        set(value) {
            field = value
            this.meters = ftToMeters(value)
        }

    /**
     * Constructs a new Length of zero.
     */
    constructor() {
    }

    /**
     * @param length
     * @param lengthUnit
     */
    constructor(length: Double, lengthUnit: LengthUnit) {
        this.meters = when (lengthUnit) {
            LengthUnit.Meters -> length
            LengthUnit.Centimeters -> length / 100.0
            LengthUnit.Millimeters -> length / 1000.0
            LengthUnit.Inches -> inchesToMeters(length)
            LengthUnit.Feet -> ftToMeters(length)
        }
    }

    private constructor(meters: Double) {
        this.meters = meters
    }

    companion object {
        fun fromMeters(meters: Double): Length {
            return Length(meters)
        }

        fun fromCM(CM: Double): Length {
            return Length(CM, LengthUnit.Centimeters)
        }

        fun fromMM(MM: Double): Length {
            return Length(MM, LengthUnit.Millimeters)
        }

        fun fromInches(inches: Double): Length {
            return Length(inches, LengthUnit.Inches)
        }

        fun fromFt(ft: Double): Length {
            return Length(ft, LengthUnit.Feet)
        }
    }

    fun getLength(lengthUnit: LengthUnit): Double {
        return when (lengthUnit) {
            LengthUnit.Meters -> this.meters
            LengthUnit.Centimeters -> this.meters * 100.0
            LengthUnit.Millimeters -> this.meters * 1000.0
            LengthUnit.Inches -> metersToInches(this.meters)
            LengthUnit.Feet -> metersToFt(this.meters)
        }
    }

    override fun toString(): String {
        return "Length in meters: $meters"
    }

    override fun compareTo(other: Length): Int {
        return (this.meters - other.meters).toInt()
    }
}