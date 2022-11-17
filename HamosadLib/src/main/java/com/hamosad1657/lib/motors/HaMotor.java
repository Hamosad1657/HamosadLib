package com.hamosad1657.lib.motors;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class HaMotor<motorClass extends MotorController> {
    private motorClass motor;

    private long kp;
    private long ki;
    private long kd;
    private PIDController pidController;

    public HaMotor(motorClass motor) {
        this.motor = motor;
    }

    public HaMotor(motorClass motor, long kp, long ki, long kd) {
        this.motor = motor;
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.pidController = new PIDController(kp, ki, kd);
    }

    public void setPidTolerance(double tolerance) {
        this.pidController.setTolerance(tolerance);
    }

    public void setSetpoint(double setpoint) {
        this.pidController.setSetpoint(setpoint);
    }

    public void setPercentOutput(double percentOutput) {
        this.motor.set(percentOutput);
    }

    public void setPID(long kp, long ki, long kd) {
        this.kd = kd;
        this.ki = ki;
        this.kp = kp;
        this.pidController = new PIDController(kp, ki, kd);
    }

    public void setDegrees(double motorPosition) {
        this.motor.set(this.pidController.calculate(motorPosition));
    }

    public void setDegrees(double motorPosition, double setpoint) {
        this.motor.set(this.pidController.calculate(motorPosition, setpoint));
    }

    public void setInverted() {
        this.motor.setInverted(!this.motor.getInverted());
    }

    public void shuffleboardInit(ShuffleboardTab tab, String title) {
        ShuffleboardContainer container = tab.getLayout(title, BuiltInLayouts.kList);
        // add more in the future
    }
}
