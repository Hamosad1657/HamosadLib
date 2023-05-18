package com.hamosad1657.lib.debug;

import edu.wpi.first.wpilibj.DriverStation;

public class HaDriverStation {
	/**
	 * Prints a warning to the RIOLog and Driver Station.
	 * @param object - The object to print (after calling it's toString method)
	 */
	public static void print(Object object) {
		DriverStation.reportWarning(object.toString(), false);
	}
}