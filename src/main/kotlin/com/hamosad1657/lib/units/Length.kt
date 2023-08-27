package com.hamosad1657.lib.units

class Length private constructor(length: Double, lengthUnit: Unit) : Comparable<Length> {
	var meters = 0.0
		set(value) {
			require(!value.isNaN()) { " Length cannot contain NaN " }
			require(value.isFinite()) { " Length cannot contain infinity " }
			field = value
		}

	init {
		this.meters = when (lengthUnit) {
			Unit.Meters -> length
			Unit.Centimeters -> length / 100.0
			Unit.Millimeters -> length / 1000.0
			Unit.Inches -> inchesToMeters(length)
			Unit.Feet -> feetToMeters(length)
		}
	}

	constructor(
		meters: Double = 0.0,
		centimeters: Double = 0.0,
		millimeters: Double = 0.0,
		inches: Double = 0.0,
		feet: Double = 0.0,
	) : this(0.0, Unit.Meters) {
		this.meters +=
			meters + centimeters * 100.0 + millimeters * 1000.0 + inchesToMeters(inches) + feetToMeters(feet)
	}

	var centimeters: Double = inUnit(Unit.Centimeters)
		get() = inUnit(Unit.Centimeters)
		set(value) {
			field = value
			this.meters = value / 100.0
		}
	var millimeters: Double = inUnit(Unit.Millimeters)
		get() = inUnit(Unit.Millimeters)
		set(value) {
			field = value
			this.meters = value / 1000.0
		}
	var inches: Double = inUnit(Unit.Millimeters)
		get() = inUnit(Unit.Inches)
		set(value) {
			field = value
			this.meters = inchesToMeters(value)
		}
	var ft: Double = inUnit(Unit.Millimeters)
		get() = inUnit(Unit.Feet)
		set(value) {
			field = value
			this.meters = feetToMeters(value)
		}

	private fun inUnit(lengthUnit: Unit): Double {
		return when (lengthUnit) {
			Unit.Meters -> meters
			Unit.Centimeters -> meters * 100.0
			Unit.Millimeters -> meters * 1000.0
			Unit.Inches -> metersToInches(meters)
			Unit.Feet -> metersToFt(meters)
		}
	}

	companion object {
		fun fromMeters(meters: Double): Length {
			return Length(meters, Unit.Meters)
		}

		fun fromCM(centimeters: Double): Length {
			return Length(centimeters, Unit.Centimeters)
		}

		fun fromMM(millimeters: Double): Length {
			return Length(millimeters, Unit.Millimeters)
		}

		fun fromInches(inches: Double): Length {
			return Length(inches, Unit.Inches)
		}

		fun fromFeet(feet: Double): Length {
			return Length(feet, Unit.Feet)
		}
	}

	override fun toString(): String {
		Length(meters = 1.0, millimeters = 13.2)
		return "Length in meters: $meters"
	}

	override fun compareTo(other: Length): Int {
		return (meters - other.meters).toInt()
	}

	enum class Unit {
		Meters,
		Centimeters,
		Millimeters,
		Inches,
		Feet,
	}
}

val Double.meters get() = Length.fromMeters(this)
val Int.meters get() = Length.fromMeters(this.toDouble())
