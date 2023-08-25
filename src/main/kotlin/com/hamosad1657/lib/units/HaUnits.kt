package com.hamosad1657.lib.units

// This file is named "HaUnits" and not "Units" to avoid name conflicts

enum class VelocityUnit {
	RPM,
	MPS,
	RadPS,
	DegPS,
}

enum class AngleUnit {
	Rad,
	Deg,
	Rotation,
}

enum class LengthUnit {
	Meters,
	Centimeters,
	Millimeters,
	Inches,
	Feet,
}

sealed interface AngularVelocity {
	val velocity: Double

	fun toRPM(): RPM
	fun toDegPS(): DegPS
	fun toRadPS(): RadPS

	fun toMPS(wheelRadius: Double): Double
	fun toFalconTicks(gearRatio: Double): Double

	class RPM(override val velocity: Double) : AngularVelocity {
		override fun toRPM(): RPM = this
		override fun toDegPS(): DegPS = DegPS(RPMToDegPS(velocity))
		override fun toRadPS(): RadPS = RadPS(RPMToRadPS(velocity))

		override fun toMPS(wheelRadius: Double): Double = RPMToMPS(velocity, wheelRadius)
		override fun toFalconTicks(gearRatio: Double): Double = RPMToFalconTicks(velocity, gearRatio)
	}

	class DegPS(override val velocity: Double) : AngularVelocity {
		override fun toRPM(): RPM = RPM(degPSToRPM(velocity))
		override fun toDegPS(): DegPS = this
		override fun toRadPS(): RadPS = toRPM().toRadPS()

		override fun toMPS(wheelRadius: Double): Double = degPSToMPS(velocity, wheelRadius)
		override fun toFalconTicks(gearRatio: Double): Double = toRPM().toFalconTicks(gearRatio)
	}

	class RadPS(override val velocity: Double) : AngularVelocity {
		override fun toRPM(): RPM = RPM(radPSToRPM(velocity))
		override fun toDegPS(): DegPS = toRPM().toDegPS()
		override fun toRadPS(): RadPS = this

		override fun toMPS(wheelRadius: Double): Double = radPSToMPS(velocity, wheelRadius)
		override fun toFalconTicks(gearRatio: Double): Double = toRPM().toFalconTicks(gearRatio)
	}
}
