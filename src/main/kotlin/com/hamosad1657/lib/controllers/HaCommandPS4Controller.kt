package com.hamosad1657.lib.controllers

import com.hamosad1657.lib.math.continuousDeadband
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
import kotlin.math.pow
import kotlin.math.sign

/**
 * Automatically flips y value of joysticks and applies deadband
 */
class HaCommandPS4Controller(private val deadband: Double, port: Int) : CommandPS4Controller(port) {
	override fun getLeftX(): Double {
		return continuousDeadband(super.getLeftX(), deadband)
	}

	override fun getLeftY(): Double {
		return -continuousDeadband(super.getLeftY(), deadband)
	}

	override fun getRightX(): Double {
		return continuousDeadband(super.getRightX(), deadband)
	}

	override fun getRightY(): Double {
		return -continuousDeadband(super.getRightY(), deadband)
	}
}

fun Double.powerProfile(power: Int): Double {
	return if (power % 2 == 0) {
		this.pow(power)
	} else this.pow(power) * this.sign
}