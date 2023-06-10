package com.hamosad1657.lib.math

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj.DriverStation

object HaUnitConvertor {
    // for reference
    // https://lucidar.me/en/unit-converter/revolutions-per-minute-to-meters-per-second/
    private const val inchesInMeter = 39.3700787402

    /** Degrees to radians.  */
    fun degToRad(deg: Double): Double {
        return Math.toRadians(deg)
    }

    /** Radians to degrees.  */
    fun radToDeg(rad: Double): Double {
        return Math.toDegrees(rad)
    }

    /** Degrees per second to rotations per minute.  */
    fun degPSToRPM(degPS: Double): Double {
        return degPS / 360 / 60
    }

    /** Rotations per minute to degrees per seconds.  */
    fun RPMToDegPS(RPM: Double): Double {
        return RPM * 60 * 360
    }

    /** Radians per second to rotations per minute.  */
    fun radPSToRPM(radPS: Double): Double {
        return radPS / (Math.PI * 2) / 60
    }

    /** Rotations per minute to radians per second.  */
    fun RPMToRadPS(RPM: Double): Double {
        return RPM * 60 * (Math.PI * 2)
    }

    /**
     * Radians per second to meters per second.
     *
     * @throws IllegalArgumentException If wheel radius is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun radPSToMPS(radPS: Double, wheelRadiusMeters: Double): Double {
        return if (wheelRadiusMeters > 0) RPMToMPS(
            radPSToRPM(radPS),
            wheelRadiusMeters
        ) else throw IllegalArgumentException("Wheel radius must be positive")
    }

    /**
     * Degrees per second to meters per second.
     *
     * @param degPS
     * @param wheelRadiusMeters
     * @return
     * @throws IllegalArgumentException If wheel radius is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun degPSToMPS(degPS: Double, wheelRadiusMeters: Double): Double {
        return if (wheelRadiusMeters > 0) RPMToMPS(
            degPSToRPM(degPS),
            wheelRadiusMeters
        ) else throw IllegalArgumentException("Wheel radius must be positive")
    }

    /** Meters per second to degrees per second.  */
    fun MPSToDegPS(MPS: Double, wheelRadiusM: Double): Double {
        return RPMToDegPS(MPSToRPM(MPS, wheelRadiusM))
    }

    /**
     * Meters per second to rotations per minute.
     *
     * @throws IllegalArgumentException If wheel radius is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun MPSToRPM(MPS: Double, wheelRadiusMeters: Double): Double {
        return if (wheelRadiusMeters > 0) 60 / (2 * Math.PI * wheelRadiusMeters) * MPS else throw IllegalArgumentException(
            "Wheel radius must be positive"
        )
    }

    /**
     * Rotations per minute to meters per second.
     *
     * @param RPM
     * @param wheelRadiusMeters
     * @return
     * @throws IllegalArgumentException If wheel radius is negative.
     */
    @Throws(IllegalArgumentException::class)
    fun RPMToMPS(RPM: Double, wheelRadiusMeters: Double): Double {
        return if (wheelRadiusMeters > 0) wheelRadiusMeters * (2 * Math.PI / 60) * RPM else throw IllegalArgumentException(
            "Wheel radius must be positive"
        )
    }

    /** Meters to inches.  */
    fun metersToInches(meters: Double): Double {
        return meters * inchesInMeter
    }

    /** Inches to meters.  */
    fun inchesToMeters(inches: Double): Double {
        return inches / inchesInMeter
    }

    /** Meters to ft.  */
    fun metersToFt(meters: Double): Double {
        return metersToInches(meters) / 12.0
    }

    /** Ft to meters.  */
    fun ftToMeters(ft: Double): Double {
        return inchesToMeters(ft * 12.0)
    }

    /** Degrees per second to CANCoder ticks per 100ms.  */
    fun degPSToCANCoderTicksPer100ms(degPS: Double): Double {
        return degreesToCANCoderTicks(degPS) / 10
    }

    /**
     * Convert degrees to CANCoder ticks with no gear ratio. An overload exists with a gear ratio.
     *
     * @param degrees - Degrees of rotation of the mechanism.
     * @return CANCoder position counts (4096)
     */
    fun degreesToCANCoderTicks(degrees: Double): Double {
        return degrees / 360.0 * HaUnits.kCANCoderTicksPerRev
    }

    /**
     * @param positionCounts - CANCoder position counts (4096)
     * @param gearRatio      - Gear Ratio between CANCoder and mechanism.
     * @return Degrees of rotation of mechanism
     */
    fun CANCoderTicksToDegrees(positionCounts: Double, gearRatio: Double): Double {
        return positionCounts * (360.0 / (gearRatio * 4096.0))
    }

    /**
     * @param degrees   - Degrees of rotation of mechanism
     * @param gearRatio - Gear ratio between CANCoder and mechanism
     * @return CANCoder position counts (4096)
     */
    fun degreesToCANCoderTicks(degrees: Double, gearRatio: Double): Double {
        return degrees / (360.0 / (gearRatio * 4096.0))
    }

    /**
     * @param positionCounts - Falcon position counts in integrated encoder ticks (2048)
     * @param gearRatio      - Gear ratio between Falcon and mechanism
     * @return Degrees of rotation of mechanism
     */
    fun falconTicksToDegrees(positionCounts: Double, gearRatio: Double): Double {
        return positionCounts * (360.0 / (gearRatio * 2048.0))
    }

    /**
     * @param degrees   - Degrees of rotation of mechanism
     * @param gearRatio - Gear ratio between Falcon and mechanism
     * @return Falcon position counts in integrated encoder ticks (2048)
     */
    fun degreesToFalconTicks(degrees: Double, gearRatio: Double): Double {
        return degrees / (360.0 / (gearRatio * 2048.0))
    }

    /**
     * @param velocityCounts - Falcon integrated encoder ticks per 100ms
     * @param gearRatio      - Gear ratio between Falcon and mechanism (set to 1 for Falcon RPM)
     * @return RPM of mechanism
     */
    fun falconTicksToRPM(velocityCounts: Double, gearRatio: Double): Double {
        val motorRPM = velocityCounts * (600.0 / 2048.0)
        return motorRPM / gearRatio
    }

    /**
     * @param RPM       - RPM of mechanism
     * @param gearRatio - Gear ratio between Falcon and mechanism (set to 1 for Falcon RPM)
     * @return RPM of Mechanism
     */
    fun RPMToFalconTicks(RPM: Double, gearRatio: Double): Double {
        val motorRPM = RPM * gearRatio
        return motorRPM * (2048.0 / 600.0)
    }

    /**
     * @param velocityCounts      - Falcon integrated encoder ticks per 100ms
     * @param wheelCircumferenceM - Circumference of wheel in meters
     * @param gearRatio           - Gear Ratio between Falcon and mechanism (set to 1 for Falcon MPS)
     * @return Falcon Velocity Counts
     */
    fun falconTicksToMPS(
        velocityCounts: Double,
        wheelCircumferenceM: Double,
        gearRatio: Double
    ): Double {
        val wheelRPM = falconTicksToRPM(velocityCounts, gearRatio)
        return wheelRPM * wheelCircumferenceM / 60
    }

    /**
     * @param MPS                 - Velocity in meters per second
     * @param wheelCircumferenceM - Circumference of wheel in meters
     * @param gearRatio           - Gear Ratio between Falcon and Mechanism (set to 1 for Falcon MPS)
     * @return Falcon Velocity Counts
     */
    fun MPSToFalconTicks(
        MPS: Double,
        wheelCircumferenceM: Double,
        gearRatio: Double
    ): Double {
        val wheelRPM = MPS * 60 / wheelCircumferenceM
        return RPMToFalconTicks(wheelRPM, gearRatio)
    }

    /**
     * @param positionCounts      - Falcon position counts in integrated encoder ticks (2048)
     * @param wheelCircumferenceM - Circumference of wheel in meters
     * @param gearRatio           - Gear Ratio between Falcon and Wheel
     * @return Meters
     */
    fun falconTicksToMeters(positionCounts: Double, wheelCircumferenceM: Double, gearRatio: Double): Double {
        return positionCounts * (wheelCircumferenceM / (gearRatio * 2048.0))
    }

    /**
     * @param meters              - Meters
     * @param wheelCircumferenceM - Circumference of wheel
     * @param gearRatio           - Gear ratio between Falcon and wheel
     * @return Falcon position counts in integrated encoder ticks (2048)
     */
    fun metersToFalconTicks(meters: Double, wheelCircumferenceM: Double, gearRatio: Double): Double {
        return meters / (wheelCircumferenceM / (gearRatio * 2048.0))
    }

    /**
     *
     * @param position - MUST be blue alliance
     * @return New position relative to robot's alliance.
     */
    fun matchPoseToAlliance(position: Pose2d): Pose2d {
        return if (DriverStation.getAlliance() == DriverStation.Alliance.Blue) position else Pose2d(
            HaUnits.kChargedUpFieldLength - position.x, position.y,
            position.rotation.rotateBy(Rotation2d.fromDegrees(180.0))
        )
    }
}
