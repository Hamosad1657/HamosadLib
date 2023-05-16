
package com.hamosad1657.lib.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Encoder;

/**
 * A sendable quadrature encoder, wrapping WPILib's Encoder class.
 * For an explanation on quadratrue encoders, read here:
 * https://docs.wpilib.org/en/stable/docs/software/hardware-apis/sensors/encoders-software.html
 */
public class HaEncoder implements Sendable {
	private final Encoder encoder;
	private final double pulsesPerRev;

	private double wheelRadiusM = 0.0;

	/**
	 * Construct a new HaEncoder. It starts counting immediately.
	 * 
	 * @param channelA         - The digital input port on the RoboRIO (onboard or
	 *                         MXP) that the encoder's channel A is wired to.
	 * @param channelB         - The digital input port that the encoder's channel B
	 *                         is wired to.
	 * @param pulsesPerRev     - The encoder's pulses per revolution.
	 * @param reverseDirection - Whether to invert the speed and distance.
	 */
	public HaEncoder(int channelA, int channelB, int pulsesPerRev, boolean reverseDirection) {
		this.encoder = new Encoder(channelA, channelB, reverseDirection);
		this.pulsesPerRev = (double) pulsesPerRev;
		this.encoder.setDistancePerPulse(this.pulsesPerRev);
	}

	/**
	 * Construct a new HaEncoder. It starts counting immediately.
	 * 
	 * @param channelA     - The digital input port on the RoboRIO (onboard or
	 *                     MXP) that the encoder's channel A is wired to.
	 * @param channelB     - The digital input port that the encoder's channel B
	 *                     is wired to.
	 * @param pulsesPerRev - The encoder's pulses per revolution.
	 */
	public HaEncoder(int channelA, int channelB, int pulsesPerRev) {
		this.encoder = new Encoder(channelA, channelB);
		this.pulsesPerRev = (double) pulsesPerRev;
		this.encoder.setDistancePerPulse(this.pulsesPerRev);
	}

	/**
	 * @param gearRatio - The gear reduction ratio between the encoder shaft and the
	 *                  wheel/mechanism. For example, for a 4:1 speed reduction passs
	 *                  0.25, not 4. If not set, it defaults to 1.
	 */
	public void setGearRatio(double gearRatio) {
		this.encoder.setDistancePerPulse(this.pulsesPerRev * gearRatio);
	}

	/**
	 * @param wheelRadiusM - The radius of the wheel in meters, to use with
	 *                     getDistanceMeters and getSpeedMPS.
	 */
	public void setWheelRadius(double wheelRadiusM) {
		this.wheelRadiusM = wheelRadiusM;
	}

	/**
	 * @return The rotations travled since last reset, accounting the gear ratio as
	 *         configured in setGearRatio().
	 */
	public double getRotations() {
		return this.encoder.getDistance();
	}

	/**
	 * @return The degrees travled since last reset, accounting the gear ratio as
	 *         configured in setGearRatio().
	 */
	public double getDegrees() {
		return this.getRotations() * 360;
	}

	/**
	 * @return The distance traveled in meters since last reset, as configured in
	 *         setWheelRadius, and accounting the gear ratio as configured in
	 *         setGearRatio().
	 */
	public double getDistanceMeters() {
		return this.getRotations() * this.wheelRadiusM * 2 * Math.PI;
	}

	/**
	 * @return The speed in rotations per minute, accounting the gear ratio as
	 *         configured in setGearRatio().
	 */
	public double getSpeedRPM() {
		return this.encoder.getRate() / 60;
	}

	/**
	 * @return The speed in degrees per second, accounting the gear ratio as
	 *         configured in setGearRatio().
	 */
	public double getSpeedDegPS() {
		return this.encoder.getRate() * 360;
	}

	/**
	 * @return The wheel speed in meters per second, as configured in
	 *         setWheelRadius, and accounting the gear ratio as configured in
	 *         setGearRatio().
	 */
	public double getSpeedMPS() {
		return this.encoder.getRate() * 2 * Math.PI * this.wheelRadiusM;
	}

	/**
	 * Sets the distance/rotations count to zero.
	 */
	public void reset() {
		this.encoder.reset();
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("HaEncoder");
		builder.addDoubleProperty("Rotations", this::getRotations, null);
		builder.addDoubleProperty("RPM", this::getSpeedRPM, null);
	}
}
