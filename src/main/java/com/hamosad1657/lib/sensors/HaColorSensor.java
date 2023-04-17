
package com.hamosad1657.lib.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorSensorV3.RawColor;

public class HaColorSensor implements Sendable {
	private final ColorSensorV3 colorSensor;

	private final int minCm = 1;
	private final int maxCm = 10;
	private final int minProx = 0;
	private final int maxProx = 2047;

	/**
	 * @param port - The I2C port that the sensor is connected to.
	 */
	public HaColorSensor(I2C.Port port) {
		this.colorSensor = new ColorSensorV3(port);
	}

	/**
	 * @param minColor - The minimum acceptable color values (inclusive).
	 * @param maxColor - The maximum acceptable color values (inclusive).
	 * 
	 * @return Whether the detected color is in the specified range (inclusive).
	 */
	public boolean isColorInRange(RawColor minColor, RawColor maxColor) {
		RawColor color = colorSensor.getRawColor();
		return (color.red >= minColor.red && color.red <= maxColor.red && color.blue >= minColor.blue
				&& color.blue <= maxColor.blue && color.green >= minColor.green && color.green <= maxColor.green);
	}

	/**
	 * @param minProximity - The minimum acceptable proximity (inclusive).
	 * @param maxProximity - The maximum acceptable proximity (inclusive).
	 * 
	 * @return Whether the proximity is in the specified range (inclusive).
	 */
	public boolean isObjectInProximityRange(double minProximity, double maxProximity) {
		int proximity = this.getProximity();
		return proximity >= minProximity && proximity <= maxProximity;
	}

	/**
	 * @return The raw color detected by the sensor, as a RawColor object.
	 */
	public RawColor getColor() {
		return this.colorSensor.getRawColor();
	}

	/**
	 * 
	 * @return The red value of the detected color.
	 */
	public double getRed() {
		return this.colorSensor.getRed();
	}

	/**
	 * 
	 * @return The green value of the detected color.
	 */
	public double getGreen() {
		return this.colorSensor.getGreen();
	}

	/**
	 * 
	 * @return The blue value of the detected color.
	 */
	public double getBlue() {
		return this.colorSensor.getBlue();
	}

	/**
	 * 
	 * @return The IR color value detected by the sensor (in CIE 1931 XYZ colorspace).
	 */
	public int getIR() {
		return this.colorSensor.getIR();
	}

	/**
	 * 
	 * @return The proximity value of the sensor ranging from 0 (object is close) to 2047 (object is far away).
	 */
	public int getProximity() {
		return this.maxProx - this.colorSensor.getProximity(); // Flip the range from [2047, 0] to [0, 2047]
	}

	/**
	 * 
	 * @return The proximity value of the sensor in cm
	 */
	public int getCmProximity() {
		return this.minCm
				+ ((this.getProximity() - this.minProx) * (this.maxCm - this.minCm)) / (this.maxProx - this.minProx);
	}

	/**
	 * @param minColor - The minimum acceptable color values (inclusive).
	 * @param maxColor - The maximum acceptable color values (inclusive).
	 * 
	 * @return Whether the detected color is in the specified range (inclusive).
	 */
	public boolean isColorInRangePercent(Color minColor, Color maxColor) {
		Color color = this.colorSensor.getColor();
		return (color.red >= minColor.red && color.red <= maxColor.red && color.blue >= minColor.blue
				&& color.blue <= maxColor.blue && color.green >= minColor.green && color.green <= maxColor.green);
	}

	/**
	 * @return The raw color detected by the sensor, as a RawColor object.
	 */
	public Color getColorPercent() {
		return this.colorSensor.getColor();
	}

	/**
	 * 
	 * @return The red value of the detected color.
	 */
	public double getRedPercent() {
		return this.colorSensor.getColor().red;
	}

	/**
	 * 
	 * @return The green value of the detected color.
	 */
	public double getGreenPercent() {
		return this.colorSensor.getColor().green;
	}

	/**
	 * 
	 * @return The blue value of the detected color.
	 */
	public double getBluePercent() {
		return this.colorSensor.getColor().blue;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("HaColorSensor");

		builder.addDoubleProperty("Red", this::getRed, null);
		builder.addDoubleProperty("Green", this::getGreen, null);
		builder.addDoubleProperty("Blue", this::getBlue, null);

		builder.addDoubleProperty("Red %", this::getRedPercent, null);
		builder.addDoubleProperty("Green %", this::getGreenPercent, null);
		builder.addDoubleProperty("Blue %", this::getBluePercent, null);

		builder.addDoubleProperty("Proximity (CM)", this::getCmProximity, null);

		builder.addDoubleProperty("Proximity (0-2047)", this::getProximity, null);
		builder.addDoubleProperty("IR", this::getIR, null);
	}
}
