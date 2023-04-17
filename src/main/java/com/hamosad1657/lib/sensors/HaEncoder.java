
package com.hamosad1657.lib.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Encoder;

public class HaEncoder implements Sendable {
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

	/**
	 * @return The distance traveled, according to the configured wheel radius.
	 */
	public double getDistance() {
		return this.encoder.getDistance();
	}

	/**
	 * Distance units the same as configured in setWheelRadius().
	 * 
	 * @param minDistance
	 * @param maxDistance
	 * @return Whether the distance traveled is within the range.
	 */
	public boolean inRange(double minDistance, double maxDistance) {
		return this.getDistance() > minDistance && this.getDistance() < maxDistance;
	}

	/**
	 * Sets the wheel radius, to use in getDistance().
	 * 
	 * @param radius - The radius of the wheel in whatever units you choose. Please choose meters.
	 */
	public void setWheelRadius(double radius) {
		this.wheelRadius = radius;
		this.encoder.setDistancePerPulse(radius * 2 * Math.PI);
	}

	/**
	 * @return The configured wheel radius.
	 */
	public double getWheelRadius() {
		return this.wheelRadius;
	}

	/**
	 * @return The encoder speed in distance units per second (same units as configured in setWheelRadius()).
	 */
	public double getSpeed() {
		return this.encoder.getRate();
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("HaEncoder");

		builder.addDoubleProperty("Distance", this::getDistance, null);
		builder.addDoubleProperty("Wheel Radius", this::getWheelRadius, null);
	}
}
