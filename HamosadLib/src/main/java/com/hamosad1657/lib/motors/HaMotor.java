package com.hamosad1657.lib.motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;

public class HaMotor<motorClass> {
    private motorClass motor;
    private encoderType typeOfEncoder;

    private long kp;
    private long ki;
    private long kd;
    private PIDController pidController;

    public HaMotor(motorClass motor) {
        this.motor = motor;
    }

    public HaMotor(motorClass motor, encoderType typeOfEncoder) {
        this.motor = motor;
        this.typeOfEncoder = typeOfEncoder;

    }

    public HaMotor(motorClass motor, encoderType typeOfEncoder, long kp, long ki, long kd) {
        this.motor = motor;
        this.typeOfEncoder = typeOfEncoder;
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

    public void setEncoderType(encoderType encoderType) {
        this.typeOfEncoder = encoderType;
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

    static enum encoderType {
        absolute,
        incromental
    }
}
