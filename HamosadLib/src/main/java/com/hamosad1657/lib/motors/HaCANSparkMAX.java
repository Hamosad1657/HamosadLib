// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.HaUnits.PIDGains;
import com.hamosad1657.lib.HaUnits.Velocities;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.util.sendable.SendableBuilder;

/** Add your docs here. */
public class HaCANSparkMAX extends HaMotorController {
	@Override
	public void initSendable(SendableBuilder builder) {

	}

	@Override
	public void initShuffleboard() {

	}

	@Override
	public void set(double value, Velocities type) {
		// TODO Auto-generated method stub

	}

	@Override
	public double get(Velocities type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void set(double value, Velocities type, PIDGains pidGains) {
		// TODO Auto-generated method stub

	}

}
