package com.hamosad1657.lib.units

import edu.wpi.first.math.geometry.Rotation2d

/// --- Length ---

val Double.meters get() = Length.fromMeters(this)
val Int.meters get() = Length.fromMeters(this.toDouble())

val Double.centimeters get() = Length.fromCentimeters(this)
val Int.centimeters get() = Length.fromCentimeters(this.toDouble())

val Double.millimeters get() = Length.fromMillimeters(this)
val Int.millimeters get() = Length.fromMillimeters(this.toDouble())

val Double.feet get() = Length.fromFeet(this)
val Int.feet get() = Length.fromFeet(this.toDouble())

val Double.inches get() = Length.fromInches(this)
val Int.inches get() = Length.fromInches(this.toDouble())


/// --- Angular Velocity ---

val Double.rpm get() = AngularVelocity.fromRpm(this)
val Int.rpm get() = AngularVelocity.fromRpm(this.toDouble())

val Double.radPs get() = AngularVelocity.fromRadPs(this)
val Int.radPs get() = AngularVelocity.fromRadPs(this.toDouble())

val Double.degPs get() = AngularVelocity.fromDegPs(this)
val Int.degPs get() = AngularVelocity.fromDegPs(this.toDouble())


/// -- Rotation2d ---

val Double.degrees: Rotation2d get() = Rotation2d.fromDegrees(this)
val Int.degrees: Rotation2d get() = Rotation2d.fromDegrees(this.toDouble())

val Double.radians: Rotation2d get() = Rotation2d.fromRadians(this)
val Int.radians: Rotation2d get() = Rotation2d.fromRadians(this.toDouble())

val Double.rotations: Rotation2d get() = Rotation2d.fromRotations(this)
val Int.rotations: Rotation2d get() = Rotation2d.fromRotations(this.toDouble())
