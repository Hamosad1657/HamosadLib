package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.hamosad1657.lib.debug.HaDriverStation
import edu.wpi.first.math.MathUtil

class HaTalonFX(deviceNumber: Int) : WPI_TalonFX(deviceNumber) {
    init {
        isSafetyEnabled = true
    }

    var forwardLimit: () -> Boolean = { false }
    var reverseLimit: () -> Boolean = { false }

    var minPercentOutput = -1.0
    var maxPercentOutput = 1.0

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