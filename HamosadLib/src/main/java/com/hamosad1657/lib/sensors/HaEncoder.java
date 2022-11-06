package com.hamosad1657.lib.sensors;

import edu.wpi.first.wpilibj.Encoder;

public class HaEncoder {
    // https://first.wpi.edu/wpilib/allwpilib/docs/release/java/edu/wpi/first/wpilibj/Encoder.html
    // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/sensors/encoders-software.html
    // ^docus
    private Encoder encoder;
    private double wheelRadius = -1;

    public HaEncoder(int channelA, int channelB) {
        this.encoder = new Encoder(channelA, channelB);
    }

    public HaEncoder(int channelA, int channelB, boolean reverseDirection) {
        this.encoder = new Encoder(channelA, channelB, reverseDirection);
    }

    public double getDistance() {
        return this.encoder.getDistance();
    }

    public boolean inRange(double minDistance, double maxDistance) {
        double currentDistance = getDistance();
        if (currentDistance > minDistance
                && currentDistance < maxDistance) {
            return true;
        } else {
            return false;
        }
    }

    public void setWheelRadius(double radius) {
        this.wheelRadius = radius;
    }

    public double getWheelRadius() {
        return this.wheelRadius;
    }

    /// Meters per Second to Rotations per Minute
    public double convertMPStoRPM(double MPS) {
        if (wheelRadius > 0){
            return (60 * MPS) / (2 * Math.PI * wheelRadius);
        } else return -1;
    }
}
