package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.hamosad1657.lib.math.clamp

class HaTalonSRX(deviceID: Int) : WPI_TalonSRX(deviceID) {
    init {
        isSafetyEnabled = true
    }

    var forwardLimit: () -> Boolean = { false }
    var reverseLimit: () -> Boolean = { false }

    var minPercentOutput = -1.0
        set(value) {
            field = if(value <= -1.0) -1.0 else value
        }
    var maxPercentOutput = 1.0
        set(value) {
            field = if(value >= 1.0) 1.0 else value
        }

    /**
     * percentOutput is clamped between properties minPercentOutput and maxPercentOutput.
     */
    override fun set(percentOutput: Double) {
        require(maxPercentOutput >= minPercentOutput)
        super.set(clamp(percentOutput, minPercentOutput, maxPercentOutput))
    }

    /**
     * percentOutput is clamped between properties minPercentOutput and maxPercentOutput.
     */
    fun setWithLimits(percentOutput: Double) {
        if ((forwardLimit() && percentOutput > 0.0) || (reverseLimit() && percentOutput < 0.0)) {
            this.set(0.0)
        } else {
            this.set(percentOutput)
        }
    }
}