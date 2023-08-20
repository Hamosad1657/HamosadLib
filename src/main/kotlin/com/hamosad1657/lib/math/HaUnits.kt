package com.hamosad1657.lib.math

import com.pathplanner.lib.auto.PIDConstants
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile

object HaUnits {
    const val kCANCoderTicksPerRev = 4096.0
    @JvmField
    var kChargedUpFieldLength = 16.5

    enum class Velocity {
        kRPM,
        kMPS,
        kRadPS,
        kDegPS
    }

    enum class Position {
        kRad,
        kDeg,
        kRot
    }

    enum class Length {
        kMeters,
        kCM,
        kMM,
        kInches,
        kFt
    }

    /**
     * Represents a set of PID, feedforward and iZone values.
     */
    class PIDGains
    /**
     * @param kP  - Proportional gain.
     * @param kI  - Integral gain.
     * @param kD  - Derivative gain.
     * @param kFF - Feed Forward gain.
     */ @JvmOverloads constructor(
        var kP: Double = 0.0,
        var kI: Double = 0.0,
        var kD: Double = 0.0,
        var kFF: Double = 0.0,
        var kIZone: Double = 0.0
    ) {
        /**
         * @param kP    - Proportional gain.
         * @param kI    - Integral gain.
         * @param kD    - Derivative gain.
         * @param kFF   - Feed Forward gain.
         * @param kIZone - If the absolute error is above iZone, the integral accumulator is cleared (making it
         * ineffective). Motor controllers have this feature, but WPILib don't.
         */
        /**
         * @param kP - Proportional gain.
         * @param kI - Integral gain.
         * @param kD - Derivative gain.
         */
        /**
         * @return A new PIDController with P, I and D gains.
         */
        fun toPIDController(): PIDController {
            return PIDController(kP, kI, kD)
        }

        /**
         * @return A new ProfiledPIDController with P, I and D gains, and the given velocity and acceleration constrains.
         */
        fun toProfiledPIDController(constraints: TrapezoidProfile.Constraints?): ProfiledPIDController {
            return ProfiledPIDController(kP, kI, kD, constraints)
        }

        /**
         * @return A new PIDConstants object with P, I and D gains.
         */
        fun toPathPlannerPIDConstants(): PIDConstants {
            return PIDConstants(kP, kI, kD)
        }
    }
}
