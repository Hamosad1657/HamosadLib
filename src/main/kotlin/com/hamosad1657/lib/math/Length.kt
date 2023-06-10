package com.hamosad1657.lib.math

class Length {
    private var m = 0.0

    /**
     * Constructs a new Length of zero.
     */
    constructor() {
        m = 0.0
    }

    /**
     * @param length
     * @param lengthUnit
     */
    constructor(length: Double, lengthUnit: HaUnits.Length) {
        m = when (lengthUnit) {
            HaUnits.Length.kMeters -> length
            HaUnits.Length.kCM -> length / 100.0
            HaUnits.Length.kMM -> length / 1000.0
            HaUnits.Length.kInches -> HaUnitConvertor.inchesToMeters(length)
            HaUnits.Length.kFt -> HaUnitConvertor.ftToMeters(length)
        }
    }

    fun getLength(lengthUnit: HaUnits.Length): Double {
        return when (lengthUnit) {
            HaUnits.Length.kMeters -> m
            HaUnits.Length.kCM -> m * 100.0
            HaUnits.Length.kMM -> m * 1000.0
            HaUnits.Length.kInches -> HaUnitConvertor.metersToInches(m)
            HaUnits.Length.kFt -> HaUnitConvertor.metersToFt(m)
        }
    }

    val cM: Double
        get() = getLength(HaUnits.Length.kCM)
    val mM: Double
        get() = getLength(HaUnits.Length.kMM)
    val inches: Double
        get() = getLength(HaUnits.Length.kInches)
    val ft: Double
        get() = getLength(HaUnits.Length.kFt)

    private constructor(meters: Double) {
        m = meters
    }

    override fun toString(): String {
        return "Length in meters: $m"
    }

    companion object {
        fun fromMeters(meters: Double): Length {
            return Length(meters)
        }

        fun fromCM(CM: Double): Length {
            return Length(CM, HaUnits.Length.kCM)
        }

        fun fromMM(MM: Double): Length {
            return Length(MM, HaUnits.Length.kMM)
        }

        fun fromInches(inches: Double): Length {
            return Length(inches, HaUnits.Length.kInches)
        }

        fun fromFt(ft: Double): Length {
            return Length(ft, HaUnits.Length.kFt)
        }
    }
}