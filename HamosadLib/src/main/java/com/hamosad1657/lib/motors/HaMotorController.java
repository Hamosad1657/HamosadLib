package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.HaUnits;
import com.hamosad1657.lib.HaUnits.PIDGains;
import edu.wpi.first.util.sendable.Sendable;

abstract public class HaMotorController implements Sendable {
	abstract public void initShuffleboard();

	/**
	 * Configure PID gains for the motor controller.
	 * @param PIDGains
	 */
	abstract public void configPID(PIDGains PIDGains);

	/**
	 * Set motor output in 1 to -1.
	 * @param value percent output 1 to -1
	 */
	abstract public void set(double value);

	/**
	 * Control the motor by current.
	 * @param value amperes
	 */
	abstract public void setCurrent(double value);

	/**
	 * Get the motor output in 1 to -1.
	 * @return percent output 1 to -1
	 */
	abstract public double get();

	/**
	 * Control the motor by velocity.
	 * @param value
	 * @param type velocity unit
	 */
	abstract public void set(double value, HaUnits.Velocities type);

	/**
	 * Control the motor by position.
	 * @param value
	 * @param type position unit
	 */
	abstract public void set(double value, HaUnits.Positions type);

	/**
	 * @param type velocity unit
	 * @return Motor velocity
	 */
	abstract public double get(HaUnits.Velocities type);

	/**
	 * @param type position unit
	 * @return Motor position
	 */
	abstract public double get(HaUnits.Positions type);

	/**
	 * @return Motor output current
	 */
	abstract public double getCurrent();

}