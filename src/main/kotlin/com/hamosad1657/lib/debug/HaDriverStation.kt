package com.hamosad1657.lib.debug

import edu.wpi.first.wpilibj.DriverStation

object HaDriverStation {
	/**
	 * Prints a warning to the RIOLog and Driver Station.
	 * @param object - The object to print (after calling its toString method)
	 */
	@JvmStatic
	fun print(`object`: Any) {
		DriverStation.reportWarning(`object`.toString(), false)
	}
}