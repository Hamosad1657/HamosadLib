package com.hamosad1657.lib.motors

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.hamosad1657.lib.math.clamp
import com.hamosad1657.lib.math.wrapPositionSetpoint
import com.hamosad1657.lib.units.FALCON_TICKS_PER_ROTATION

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

	/**
	 * Software forward limit.
	 *
	 * May be jittery in control modes that aren't percent-output, so if using  other control
	 * modes, it is recommended to implement limits through the logic of your own code as well.
	 *
	 * - If possible, use hardware limits by wiring switches to the JST connector.
	 */
	var forwardLimit: () -> Boolean = { false }
	/**
	 * Software reverse limit.
	 *
	 * May be jittery in control modes that aren't percent-output, so if using  other control
	 * modes, it is recommended to implement limits through the logic of your own code as well.
	 *
	 * - If possible, use hardware limits by wiring switches to the JST connector.
	 */
	var reverseLimit: () -> Boolean = { false }

	var minPercentOutput = -1.0
		set(value) {
			field = value.coerceAtLeast(-1.0)
		}
	var maxPercentOutput = 1.0
		set(value) {
			field = value.coerceAtMost(1.0)
		}

	var isTempSafe = true
		get() = temperature < FalconSafeTempC
		private set

	private var minPossibleMeasurement: Double = 0.0
	private var maxPossibleMeasurement: Double = 0.0
	private var isPositionWrapEnabled = false
	private var ticksPerRotation = FALCON_TICKS_PER_ROTATION.toInt()


	/**
	 * In PercentOutput control mode, value is clamped between properties minPercentOutput and maxPercentOutput
	 */
	override fun set(mode: ControlMode, value: Double) {
		if (mode == ControlMode.PercentOutput) {
			this.set(value)
		}
		else if(isAtLimit_ForOtherControlModes()) {
			super.stopMotor()
		} else if (isPositionWrapEnabled && mode == ControlMode.Position) {
			val newValue =
				wrapPositionSetpoint(value, selectedSensorPosition, minPossibleMeasurement, maxPossibleMeasurement, ticksPerRotation)
			super.set(ControlMode.Position, newValue)
		} else {
			super.set(mode, value)
		}
	}

	/**
	 * percentOutput is clamped between properties minPercentOutput and maxPercentOutput.
	 */
	override fun set(percentOutput: Double) {
		require(maxPercentOutput >= minPercentOutput)
		if ((forwardLimit() && percentOutput > 0.0) || (reverseLimit() && percentOutput < 0.0)) {
			super.set(0.0)
		} else {
			super.set(clamp(percentOutput, minPercentOutput, maxPercentOutput))
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
	fun enablePositionWrap(minPossibleMeasurement: Double, maxPossibleMeasurement: Double, ticksPerRotation: Int) {
		require(minPossibleMeasurement < maxPossibleMeasurement)
		this.minPossibleMeasurement = minPossibleMeasurement
		this.maxPossibleMeasurement = maxPossibleMeasurement
		this.ticksPerRotation = ticksPerRotation
		isPositionWrapEnabled = true
	}

	fun disablePositionWrap() {
		isPositionWrapEnabled = false
	}

	private fun isAtLimit_ForOtherControlModes() = (forwardLimit() && super.get() > 0.0) || (reverseLimit() && super.get() < 0.0)
}