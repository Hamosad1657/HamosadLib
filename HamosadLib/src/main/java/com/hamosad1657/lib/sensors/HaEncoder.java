package com.hamosad1657.lib.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

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
        if (wheelRadius > 0) {
            return (60 * MPS) / (2 * Math.PI * wheelRadius);
        } else
            return -1;
    }

    public void shuffleboardInit(ShuffleboardTab tab, String title) {
        ShuffleboardContainer container = tab.getLayout(title, BuiltInLayouts.kList);
        container.addNumber("Wheel Raius", this::getWheelRadius);
        container.add(this.encoder);

        // add more atfter the new functions are added
    }
}
