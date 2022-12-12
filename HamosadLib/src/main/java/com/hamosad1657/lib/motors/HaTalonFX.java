// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.hamosad1657.lib.motors;

import com.hamosad1657.lib.HaUnits;
import com.hamosad1657.lib.HaUnits.PIDGains;
import com.hamosad1657.lib.HaUnits.Velocities;

import edu.wpi.first.util.sendable.SendableBuilder;

/** Add your docs here. */
public class HaTalonFX extends HaBaseTalon {
	@Override
	public void initSendable(SendableBuilder builder) {

	}

	@Override
	public void initShuffleboard() {

	}

	@Override
	public void set(double value, Velocities type) {
		switch (type) {
			
		}		
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
