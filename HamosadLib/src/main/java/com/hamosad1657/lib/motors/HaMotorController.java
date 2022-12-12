package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.HaUnits;

import edu.wpi.first.util.sendable.Sendable;

abstract public class HaMotorController implements Sendable {
	abstract public void initShuffleboard();

	abstract public void set(double value, HaUnits.Velocities type);

	abstract public void set(double value, HaUnits.Positions type);

	abstract public void set(double value, HaUnits.Velocities type, HaUnits.PIDGains pidGains);

	abstract public double get(HaUnits.Velocities type);

	abstract public double get(HaUnits.Positions type);

}