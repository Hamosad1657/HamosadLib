
package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.math.HaUnits;
import com.hamosad1657.lib.math.HaUnits.PIDGains;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.util.sendable.Sendable;

abstract public class HaMotorController implements Sendable {

	/**
	 * Configure PID gains for the closed loop running on the motor controller.
	 * 
	 * @param PIDGains - A HaUnits.PIDGains object.
	 */
	abstract public void configPID(PIDGains PIDGains);

	/**
	 * @param value - Percent output [-1.0, 1.0].
	 */
	abstract public void set(double value);

	/**
	 * Control the motor by current.
	 * 
	 * @param value - Amperes.
	 */
	abstract public void setCurrent(double value);

	/**
	 * @return Percent output [-1.0, 1.0].
	 */
	abstract public double get();

	/**
	 * Control the motor by velocity.
	 * 
	 * @param value - The new velocity.
	 * @param type  - Velocity unit: rotations per minute (RPM), meters per second (MPS, Wheel radius needed for this
	 *              one), Radians per second (RadPS), or Degrees per second (DegPS)).
	 */
	abstract public void set(double value, HaUnits.Velocity type);

	/**
	 * Control the motor by position.
	 * 
	 * @param value - The new position.
	 * @param type  - Position unit: radians (Rad), degrees (Deg), or rotations(Rot)).
	 */
	abstract public void set(double value, HaUnits.Position type);

	/**
	 * @param type - Velocity unit
	 * @return Motor velocity as measured by the selected feedback sensor.
	 */
	abstract public double get(HaUnits.Velocity type);

	/**
	 * @param type - Position unit.
	 * @return Motor position as measured by the selected feedback sensor.
	 */
	abstract public double get(HaUnits.Position type);

	/**
	 * @return The motor's supply current.
	 */
	abstract public double getCurrent();

	/**
	 * 
	 * @param value
	 * @param type  - Position unit.
	 */
	abstract public void setEncoderPosition(double value, HaUnits.Position type);

	/**
	 * @return ControlType as a String.
	 */
	abstract protected String getControlType();

	/**
	 * Sets the "idle mode" (whether the motor brakes or coasts when it doesn't need to move).
	 * 
	 * @param idleMode - The new mode.
	 */
	abstract public void setIdleMode(IdleMode idleMode);

	/**
	 * @return The "idle mode" (whether the motor brakes or coasts when it doesn't need to move), as a String.
	 */
	abstract protected String getIdleMode();

	/**
	 * @return The value of the control refrence as a double.
	 */
	abstract protected double getControlReference();

}