package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX

class HaTalonFX(deviceNumber: Int) : WPI_TalonFX(deviceNumber) {
    init {
        isSafetyEnabled = true
    }

    var forwardLimit: () -> Boolean = { false }
    var reverseLimit: () -> Boolean = { false }

    fun setWithLimits(output: Double) {
        if ((forwardLimit() && output > 0.0) || (reverseLimit() && output < 0.0)) {
            set(0.0)
        } else {
            set(output)
        }
    }
}