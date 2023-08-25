package com.hamosad1657.lib.motors

import com.hamosad1657.lib.math.clamp
import com.revrobotics.CANSparkMax

/**
 * Max safe temperature for the time span of a match.
 * This number is an educated assumption based on things I found on the internet.
 * https://www.chiefdelphi.com/uploads/short-url/eVYO5tVOYZecwq6Tl2kURlFZFgq.pdf
 * https://www.revrobotics.com/neo-brushless-motor-locked-rotor-testing/
 *
 */
const val NEOSafeTempC = 90

class HaCANSparkMax(deviceID: Int) : CANSparkMax(deviceID, MotorType.kBrushless) {
	var forwardLimit: () -> Boolean = { false }
	var reverseLimit: () -> Boolean = { false }

	var minPercentOutput = -1.0
		set(value) {
			field = if (value <= -1.0) -1.0 else value
		}
	var maxPercentOutput = 1.0
		set(value) {
			field = if (value >= 1.0) 1.0 else value
		}

	/** The NEO motor has a temperature sensor inside it.*/
	var isMotorTempSafe = true
		get() = motorTemperature < NEOSafeTempC
		private set

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