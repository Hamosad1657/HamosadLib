package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.hamosad1657.lib.debug.HaDriverStation
import edu.wpi.first.math.MathUtil

/**
 * Max safe temperature for the time span of a match.
 * This number is an educated assumption based on things I found on the internet.
 * https://www.chiefdelphi.com/uploads/short-url/eVYO5tVOYZecwq6Tl2kURlFZFgq.pdf
 * https://www.revrobotics.com/neo-brushless-motor-locked-rotor-testing/
 *
 */
const val FalconSafeTempC = 90

class HaTalonFX(deviceNumber: Int) : WPI_TalonFX(deviceNumber) {
    init {
        isSafetyEnabled = true
    }

    var forwardLimit: () -> Boolean = { false }
    var reverseLimit: () -> Boolean = { false }

    var minPercentOutput = -1.0
    var maxPercentOutput = 1.0

    var isTempSafe = true
        get() = temperature < FalconSafeTempC
        private set

    override fun set(output: Double) {
        require(maxPercentOutput >= minPercentOutput)
        super.set(MathUtil.clamp(output, minPercentOutput, maxPercentOutput))
    }

    fun setWithLimits(output: Double) {
        if ((forwardLimit() && output > 0.0) || (reverseLimit() && output < 0.0)) {
            set(0.0)
        } else {
            set(output)
        }
    }
}