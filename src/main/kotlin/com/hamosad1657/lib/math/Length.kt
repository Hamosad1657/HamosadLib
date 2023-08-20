package com.hamosad1657.lib.math

class Length {
    var meters = 0.0

    var centimeters: Double = getLength(HaUnits.Length.kCM)
        get() = getLength(HaUnits.Length.kCM)
        set(value) {
            field = value
            this.meters = value / 100.0
        }
    var millimeters: Double = getLength(HaUnits.Length.kMM)
        get() = getLength(HaUnits.Length.kMM)
        set(value) {
            field = value
            this.meters = value / 1000.0
        }
    var inches: Double  = getLength(HaUnits.Length.kMM)
        get() = getLength(HaUnits.Length.kInches)
        set(value) {
            field = value
            this.meters = HaUnitConvertor.inchesToMeters(value)
        }
    var ft: Double = getLength(HaUnits.Length.kMM)
        get() = getLength(HaUnits.Length.kFt)
        set(value) {
            field = value
            this.meters = HaUnitConvertor.ftToMeters(value)
        }

    /**
     * Constructs a new Length of zero.
     */
    constructor() {
        this.meters = 0.0
    }

    /**
     * @param length
     * @param lengthUnit
     */
    constructor(length: Double, lengthUnit: HaUnits.Length) {
        this.meters = when (lengthUnit) {
            HaUnits.Length.kMeters -> length
            HaUnits.Length.kCM -> length / 100.0
            HaUnits.Length.kMM -> length / 1000.0
            HaUnits.Length.kInches -> HaUnitConvertor.inchesToMeters(length)
            HaUnits.Length.kFt -> HaUnitConvertor.ftToMeters(length)
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

    fun getLength(lengthUnit: HaUnits.Length): Double {
        return when (lengthUnit) {
            HaUnits.Length.kMeters -> this.meters
            HaUnits.Length.kCM -> this.meters * 100.0
            HaUnits.Length.kMM -> this.meters * 1000.0
            HaUnits.Length.kInches -> HaUnitConvertor.metersToInches(this.meters)
            HaUnits.Length.kFt -> -HaUnitConvertor.metersToFt(this.meters)
        }
    }

    override fun toString(): String {
        return "Length in meters: $meters"
    }
}