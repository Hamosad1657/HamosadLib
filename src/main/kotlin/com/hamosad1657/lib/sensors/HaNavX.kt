package com.hamosad1657.lib.sensors

import com.hamosad1657.lib.robotPrint
import com.hamosad1657.lib.robotPrintError
import com.hamosad1657.lib.units.AngularVelocity
import com.hamosad1657.lib.units.degToRad
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.*

/**
 * A wrapper class for kauailabs.navx.frc.AHRS, which adheres to WPILib's
 * coordinate system conventions.
 * @see <a href="https://en.wikipedia.org/wiki/Aircraft_principal_axes">Aircraft principal axes</a>
 * for an explanation on the different axes.
 */
class HaNavX : AHRS {
	private var yawOffsetDeg = 0.0

	/** The angle of the navX in range [[-180, 180]] on the Vertical-axis (perpendicular to earth, left / right rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val yawDeg: Double
		get() = -getYaw().toDouble() - yawOffsetDeg

	/** The angle of the navX in range [[-PI, PI]] on the Vertical-axis (perpendicular to earth, left / right rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val yawRad: Double
		get() = degToRad(yawDeg)

	/** The angle of the navX in range [[-180, 180]] on the Vertical-axis (perpendicular to earth, left / right rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val yaw: Rotation2d
		get() = Rotation2d.fromDegrees(yawDeg)

	/** The angle of the navX in range [[-180, 180]] on the Transverse-axis (down / up rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val pitchDeg: Double
		get() = getPitch().toDouble()

	/** The angle of the navX in range [[-PI, PI]] on the Transverse-axis (down / up rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val pitchRad: Double
		get() = degToRad(pitchDeg)

	/** The angle of the navX in range [[-180, 180]] on the Transverse-axis (down / up rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val pitch: Rotation2d
		get() = Rotation2d.fromDegrees(pitchDeg)

	/** The angle of the navX in range [[-180, 180]] on the Longitudinal-axis (lean right / lean left rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val rollDeg: Double
		get() = getRoll().toDouble()

	/** The angle of the navX in range [[-PI, PI]] on the Longitudinal-axis (lean right / lean left rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val rollRad: Double
		get() = degToRad(rollDeg)

	/** The angle of the navX in range [[-180, 180]] on the Longitudinal-axis (lean right / lean left rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	val roll: Rotation2d
		get() = Rotation2d.fromDegrees(rollDeg)

	/** The rate of angle change (angular velocity) of the navX in range [[-180, 180]]
	 * on the Vertical-axis (perpendicular to earth, left / right rotation).
	 * Larger value is counter-clockwise, according to WPILib's coordinate system. */
	private val yawAngularVelocity: AngularVelocity
		get() = AngularVelocity.fromDegPs(rate)

	/** Enables logging to the RioLog & Driver Station, then waits until
	 * the navX is connected and calibrated, or 5 seconds have passed. */
	init {
		this.enableLogging(true)

		val connectionTimeoutTimer = Timer()
		connectionTimeoutTimer.start()
		while ((!isConnected || isCalibrating) && !connectionTimeoutTimer.hasElapsed(TIMEOUT_SECONDS)) {
			// Wait until navX is connected and calibrated or 5 seconds have passed
		}
		connectionTimeoutTimer.stop()

		if (connectionTimeoutTimer.hasElapsed(TIMEOUT_SECONDS)) {
			robotPrintError(
				"Failed to connect to navX, or navX didn't calibrate, within $TIMEOUT_SECONDS seconds from startup.",
				printStackTrace = true
			)
		} else {
			robotPrint("NavX done calibrating in ${connectionTimeoutTimer.get()} seconds from startup.")
		}
	}

	constructor(port: SerialPort.Port) : super(port)
	constructor(port: SPI.Port) : super(port)

	/** Using the onboard I2C port is [not recommended](
		https://docs.wpilib.org/en/stable/docs/yearly-overview/known-issues.html#onboard-i2c-causing-system-lockups) */
	constructor(port: I2C.Port) : super(port)

	/** Sets the currently facing yaw angle as zero.*/
	override fun zeroYaw() {
		yawOffsetDeg = 0.0
		try {
			super.zeroYaw()
			robotPrint("NavX zeroed.")
		} catch (e: RuntimeException) {
			robotPrintError("Failed to zero navX yaw.", printStackTrace = true)
		}
	}

	/** Sets the currently facing yaw angle as the zero minus the offset. */
	fun setYaw(offsetDeg: Double) {
		try {
			super.zeroYaw()
			yawOffsetDeg = offsetDeg
			robotPrint("NavX offset set to $offsetDeg.")
		} catch (e: RuntimeException) {
			robotPrintError("Failed to set navX yaw offset.", printStackTrace = true)
		}
	}

	/** Sets the currently facing yaw angle as the zero minus the offset. */
	fun setYaw(offset: Rotation2d) = this.setYaw(offset.degrees)

	override fun initSendable(builder: SendableBuilder) {
		builder.setSmartDashboardType("HaNavX")
		builder.addDoubleProperty("Yaw Angle Degrees", { yawDeg }, null)
		builder.addDoubleProperty("Yaw Angle Radians", { yawRad }, null)
		builder.addDoubleProperty("Pitch Angle Degrees", { pitchDeg }, null)
		builder.addDoubleProperty("Pitch Angle Radians", { pitchRad }, null)
		builder.addDoubleProperty("Roll Angle Degrees", { rollDeg }, null)
		builder.addDoubleProperty("Roll Angle Radians", { rollRad }, null)
		builder.addDoubleProperty("Yaw Angular Velocity DegPs", { yawAngularVelocity.degPs }, null)
		builder.addDoubleProperty("Yaw Offset Degrees", { yawOffsetDeg }, null)
	}

	companion object {
		private const val TIMEOUT_SECONDS = 5.0
	}
}
