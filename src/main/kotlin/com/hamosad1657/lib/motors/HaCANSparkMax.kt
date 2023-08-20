package com.hamosad1657.lib.motors

import com.hamosad1657.lib.debug.HaDriverStation
import com.revrobotics.CANSparkMax
import edu.wpi.first.math.MathUtil
import edu.wpi.first.wpilibj.DriverStation

class HaCANSparkMax(deviceID: Int) : CANSparkMax(deviceID, MotorType.kBrushless) {
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