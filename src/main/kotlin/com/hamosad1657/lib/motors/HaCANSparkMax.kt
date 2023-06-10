package com.hamosad1657.lib.motors

import com.revrobotics.CANSparkMax

class HaCANSparkMax(deviceID: Int) : CANSparkMax(deviceID, MotorType.kBrushless) {
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