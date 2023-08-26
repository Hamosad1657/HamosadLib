package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.hamosad1657.lib.math.clamp
import com.hamosad1657.lib.math.modifyPositionSetpoint

class HaTalonSRX(deviceID: Int) : WPI_TalonSRX(deviceID) {
	init {
		isSafetyEnabled = true
	}

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

	private var minPossibleMeasurement: Double = 0.0
	private var maxPossibleMeasurement: Double = 0.0
	private var isPositionWrapEnabled = false

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

	/**
	 * In PercentOutput control mode, value is clamped between properties minPercentOutput and maxPercentOutput
	 */
	override fun set(mode: ControlMode, value: Double) {
		if (mode == ControlMode.PercentOutput) {
			this.set(value)
		} else if (isPositionWrapEnabled && mode == ControlMode.Position) {
			val newValue =
				modifyPositionSetpoint(value, selectedSensorPosition, minPossibleMeasurement, maxPossibleMeasurement)
			super.set(ControlMode.Position, newValue)
		} else {
			super.set(mode, value)
		}
	}

	/**
	 * "Position wrap" means always going the shorter way. For example, if the current
	 * position is 359 degrees and the setpoint is 2 degrees, then with position wrap
	 * it would just move three degrees to the setpoint (while without position wrap it
	 * would go all the way around).
	 *
	 * @param minPossibleMeasurement The smallest possible measurement.
	 * @param maxPossibleMeasurement The largest possible measurement.
	 */
	fun enablePositionWrap(minPossibleMeasurement: Double, maxPossibleMeasurement: Double) {
		require(minPossibleMeasurement < maxPossibleMeasurement)
		this.minPossibleMeasurement = minPossibleMeasurement
		this.maxPossibleMeasurement = maxPossibleMeasurement
		isPositionWrapEnabled = true
	}

	fun disablePositionWrap() {
		isPositionWrapEnabled = false
	}
}