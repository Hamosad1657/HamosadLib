package com.hamosad1657.lib

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase

fun robotPrint(message: Any?, printStackTrace: Boolean = false) =
	DriverStation.reportWarning(message.toString(), printStackTrace)

fun robotPrintError(message: Any?, printStackTrace: Boolean = false) =
	DriverStation.reportError(message.toString(), printStackTrace)

fun RobotBase.print(message: Any?, printStackTrace: Boolean = false) = robotPrint(message, printStackTrace)
fun RobotBase.printError(message: Any?, printStackTrace: Boolean = false) = robotPrintError(message, printStackTrace)