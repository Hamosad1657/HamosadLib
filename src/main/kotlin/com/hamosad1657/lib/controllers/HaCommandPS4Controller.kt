package com.hamosad1657.lib.controllers

import com.hamosad1657.lib.math.continuousDeadband
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
import kotlin.math.PI
import kotlin.math.atan2
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

	private fun joyStickToAngle(x: Double, y: Double): Rotation2d {
		val theta = atan2(y, x)
		return if (theta >= 0.0) Rotation2d.fromRadians(theta) else Rotation2d.fromRadians(2 * PI + theta)
	}

	/**
	 * The angle the right joystick forms with the right side of the X axis.
	 * Counter-clockwise positive, goes up to 360 degrees.
	 */
	fun getRightAngle(): Rotation2d = joyStickToAngle(rightX, rightY)

	/**
	 * The angle the left joystick forms with the right side of the X axis.
	 * Counter-clockwise positive, goes up to 360 degrees.
	 */
	fun getLeftAngle(): Rotation2d = joyStickToAngle(leftX, leftY)
}

fun Double.powerProfile(power: Int): Double {
	return if (power % 2 == 0) {
		this.pow(power) * this.sign
	} else this.pow(power)
}