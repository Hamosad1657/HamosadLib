
package com.hamosad1657.lib.sensors;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import com.hamosad1657.lib.math.HaUnitConvertor;
import com.ctre.phoenix.sensors.CANCoderSimCollection;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class HaCANCoder implements Sendable {
	public final WPI_CANCoder cancoder;
	private boolean isReversed = false;
	private CANCoderSimCollection simCANCoder;
	private double simAngleDeg = 0, simVelocityDegPS = 0;

	/**
	 * @param CanId
	 * @param offsetDeg - To find the offset, set the mechanism in the desired zero
	 *                  position and set the measured angle
	 *                  as the offset.
	 */
	public HaCANCoder(int CanId, double offsetDeg) {
		this.cancoder = new WPI_CANCoder(CanId);
		this.cancoder.configMagnetOffset(-offsetDeg);
		this.cancoder.configSensorInitializationStrategy(SensorInitializationStrategy.BootToAbsolutePosition);
		this.cancoder.configAbsoluteSensorRange(AbsoluteSensorRange.Signed_PlusMinus180);
	}

	public HaCANCoder(int id) {
		this(id, 0.0);
	}

	public HaCANCoder(WPI_CANCoder cancoder) {
		this.cancoder = cancoder;
	}

	/**
	 * @param offsetDeg - Adjusts the zero point for the absolute position. To find
	 *                  the offset, set the mechanism in the
	 *                  desired zero position and set the measured angle as the
	 *                  offset.
	 */
	public void setOffset(double offsetDeg) {
		this.cancoder.configMagnetOffset(-offsetDeg);
	}

	/**
	 * @param newPosition
	 */
	public void setPosition(double newPosition) {
		this.cancoder.setPosition(newPosition);
	}

	public void initializeSim() {
		simCANCoder = this.cancoder.getSimCollection();
	}

	public double getAbsAngleDeg() {
		if (this.isReversed) {
			return 360.0 - this.cancoder.getAbsolutePosition();
		}

		return this.cancoder.getAbsolutePosition();
	}

	/**
	 * Reverses the relative accumulated angle.
	 */
	public void setReversed(boolean reversed) {
		this.isReversed = reversed;
	}

	/**
	 * @param measurmentRange 0-360 or cartisan
	 */
	public void setMeasurmentRange(AbsoluteSensorRange measurmentRange) {
		this.cancoder.configAbsoluteSensorRange(measurmentRange);
	}

	public double getAbsAngleRad() {
		return HaUnitConvertor.degToRad(this.getAbsAngleDeg());
	}

	public Rotation2d getAbsAngleRotation2d() {
		return Rotation2d.fromDegrees(this.getAbsAngleDeg());
	}

	public double getPositionDeg() {
		return this.cancoder.getPosition();
	}

	public double getPositionRad() {
		return HaUnitConvertor.degToRad(this.getPositionDeg());
	}

	public double getVelocityDegPS() {
		return this.cancoder.getVelocity();
	}

	public double getVelocityRadPS() {
		return HaUnitConvertor.degToRad(this.cancoder.getVelocity());
	}

	public double getVelocityMPS(double wheelRadiusM) {
		return HaUnitConvertor.degPSToMPS(this.cancoder.getVelocity(), wheelRadiusM);
	}

	public double getVelocityRPM() {
		return HaUnitConvertor.degPSToRPM(this.cancoder.getVelocity());
	}

	// Sim CANCoder methods

	public double getSimAngleDeg() {
		return this.simAngleDeg;
	}

	public double getSimAngleRad() {
		return HaUnitConvertor.degToRad(this.simAngleDeg);
	}

	public Rotation2d getSimAngleRotation2d() {
		return Rotation2d.fromDegrees(this.simAngleDeg);
	}

	public double getSimVelocityDegPS() {
		return this.simVelocityDegPS;
	}

	public double getSimVelocityRadPS() {
		return HaUnitConvertor.degToRad(this.simVelocityDegPS);
	}

	public double getSimVelocityMPS(double wheelRadiusM) {
		return HaUnitConvertor.degPSToMPS(this.simVelocityDegPS, wheelRadiusM);
	}

	public double getSimVelocityRPM() {
		return HaUnitConvertor.degPSToRPM(this.simVelocityDegPS);
	}

	public void setSimAngleDeg(double angleDeg) {
		this.simAngleDeg = angleDeg;
		this.simCANCoder.setRawPosition((int) HaUnitConvertor.degreesToCANCoderTicks(simAngleDeg));
	}

	public void setSimAngleRad(double angleRad) {
		this.simAngleDeg = HaUnitConvertor.radToDeg(angleRad);
		this.simCANCoder.setRawPosition((int) HaUnitConvertor.degreesToCANCoderTicks(simAngleDeg));
	}

	public void setSimAngleRotation2d(Rotation2d angleRotation2d) {
		this.simAngleDeg = angleRotation2d.getDegrees();
		this.simCANCoder.setRawPosition((int) HaUnitConvertor.degreesToCANCoderTicks(simAngleDeg));
	}

	public void setSimVelocityDegPS(double degPS) {
		this.simVelocityDegPS = degPS;
		this.simCANCoder.setVelocity((int) HaUnitConvertor.degPSToCANCoderTicksPer100ms(this.simVelocityDegPS));
	}

	public void setSimVelocityRadPS(double radPS) {
		this.simVelocityDegPS = HaUnitConvertor.radToDeg(radPS);
		this.simCANCoder.setVelocity((int) HaUnitConvertor.degPSToCANCoderTicksPer100ms(this.simVelocityDegPS));
	}

	public void setSimVelocityMPS(double MPS, double wheelRadiusM) {
		this.simVelocityDegPS = HaUnitConvertor.MPSToDegPS(MPS, wheelRadiusM);
		this.simCANCoder.setVelocity((int) HaUnitConvertor.degPSToCANCoderTicksPer100ms(this.simVelocityDegPS));
	}

	public void setSimVelocityRPM(double RPM) {
		this.simVelocityDegPS = HaUnitConvertor.RPMToDegPS(RPM);
		this.simCANCoder.setVelocity((int) HaUnitConvertor.degPSToCANCoderTicksPer100ms(this.simVelocityDegPS));
	}

	// Private unit convertors
	// angle & velocity

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("HaCANCoder");

		builder.addDoubleProperty("Abs Angle (Deg)", this::getAbsAngleDeg, null);
		builder.addDoubleProperty("Accumulated angle (Deg)", this::getPositionDeg, null);
		// builder.addDoubleProperty("Velocity RPM", this::getVelocityRPM, null);
		// builder.addDoubleProperty("Velocity DegPS", this::getVelocityDegPS, null);
		// builder.addDoubleProperty("Velocity RadPS", this::getVelocityRadPS, null);
	}
}
