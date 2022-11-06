package com.hamosad1657.lib.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorSensorV3.RawColor;

public class HaColorSensor implements Sendable {
    private ColorSensorV3 colorSensor;

    public HaColorSensor(I2C.Port port) {
        this.colorSensor = new ColorSensorV3(port);
    }

    public HaColorSensor(I2C.Port port, ShuffleboardTab tab) {
        this.colorSensor = new ColorSensorV3(port);
    }

    /// Checks if the detected color is between [minColor] and [maxColor] using >=
    /// and <=;
    public boolean isColorInRange(RawColor minColor, RawColor maxColor) {
        RawColor color = colorSensor.getRawColor();
        if (color.red >= minColor.red && color.red <= maxColor.red && color.blue >= minColor.blue
                && color.blue <= maxColor.blue && color.green >= minColor.green && color.green <= maxColor.green)
            return true;
        return false;
    }

    public RawColor getColor() {
        return this.colorSensor.getRawColor();
    }

    public double getRed() {
        return this.colorSensor.getRed();
    }

    public double getGreen() {
        return this.colorSensor.getGreen();
    }

    public double getBlue() {
        return this.colorSensor.getBlue();
    }

    public int getIR() {
        return this.colorSensor.getIR();
    }

    public int getProximity() {
        return this.colorSensor.getProximity();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Red", this::getRed, null);
        builder.addDoubleProperty("Blue", this::getBlue, null);
        builder.addDoubleProperty("Green", this::getGreen, null);
        // also add poximity after you learn how to use it
    }

}
