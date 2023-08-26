package com.hamosad1657.lib.sensors

import com.hamosad1657.lib.debug.HaDriverStation.print
import com.kauailabs.navx.frc.AHRS
import com.kauailabs.navx.frc.AHRS.SerialDataType
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.*

/**
 * A wrapper class for kauailabs.navx.frc.AHRS, which adheres to WPILib's
 * coordinate system conventions.
 */
class HaNavX : Sendable {
    private var navX: AHRS? = null
    private var yawOffsetDeg = 0.0
    private val commsTimoutTimer = Timer()

    /**
     * Starts communication between navX and RoboRIO, enables logging to the RioLog
     * & Driver Station, waits until the
     * navX is connected and calibrated, then returns an instance.
     *
     * @param port serial port (usually USB)
     * @return An instance of HaNavx, which wraps AHRS.
     */
    constructor(port: SerialPort.Port) {
        try {
            this.initialize(port)
        } catch (E: RuntimeException) {
            print("Failed to connect to navX.")
        }
    }

    /**
     * Starts communication between navX and RoboRIO, enables logging to the RioLog
     * & Driver Station, waits until the
     * navX is connected and calibrated, then returns an instance.
     *
     * @param port I2C port. Using the onboard I2C port is not recommended, for more
     * information click here:
     * [...](https://docs.wpilib.org/en/stable/docs/yearly-overview/known-issues.html#onboard-i2c-causing-system-lockups)
     */
    constructor(port: I2C.Port) {
        try {
            this.initialize(port)
        } catch (E: RuntimeException) {
            print("Failed to connect to navX.")
        }
    }

    /**
     * Starts communication between navX and RoboRIO, enables logging to the RioLog
     * & Driver Station, waits until the
     * navX is connected and calibrated, then returns an instance.
     *
     * @param port SPI port
     */
    constructor(port: SPI.Port) {
        try {
            this.initialize(port)
        } catch (E: RuntimeException) {
            print("Failed to connect to navX.")
        }
    }

    /**
     * Used to set the angle the navX is currently facing as zero.
     */
    fun zeroYaw() {
        yawOffsetDeg = 0.0
        try {
            navX!!.zeroYaw()
        } catch (E: RuntimeException) {
            print("Failed to zero navX yaw.")
        }
    }

    /**
     * Used to set the angle the navX is currently facing minus the offset as zero.
     */
    fun setYaw(offsetDeg: Double) {
        zeroYaw() // Must be first, because zeroYaw() sets the offset as 0.
        yawOffsetDeg = offsetDeg
        print("Gyro set to: " + java.lang.Double.toString(offsetDeg) + " degrees.")
    }

    /**
     * Used to set the angle the navX is currently facing minus the offset as zero.
     */
    fun setYaw(offset: Rotation2d) {
        this.setYaw(offset.degrees)
    }

    private val yawAngleDeg: Double
        /**
         * @return The angle of the navX on the Z axis (perpendicular to earth, "yaw")
         * in degrees. Rotating
         * counter-clockwise makes the angle increase, and rotating
         * clockwise makes the angle decrease,
         * according to WPILib's coordinate system conventions.
         */
        get() = try {
            -navX!!.yaw - yawOffsetDeg
        } catch (e: RuntimeException) {
            0.0
        }
    private val yawAngleRad: Double
        /**
         * @return The angle of the navX on the Z axis (perpendicular to earth, "yaw")
         * in radians. Rotating
         * counter-clockwise makes the angle increase, and rotating clockwise
         * makes the angle decrease, according to
         * WPILib's coordinate system conventions.
         */
        get() = Math.toRadians(yawAngleDeg)
    val yawRotation2d: Rotation2d
        /**
         * @return The angle of the navX on the Z axis (perpendicular to earth, "yaw")
         * as a Rotation2d. Rotating
         * counter-clockwise makes the angle increase, and rotating clockwise
         * makes the angle decrease, according to
         * WPILib's coordinate system conventions.
         */
        get() = Rotation2d.fromDegrees(yawAngleDeg)
    private val pitchAngleDeg: Double
        /**
         *
         * @return The angle of the navX on the X axis (forward-backward tilt) in
         * degrees. Tilting backwards makes the angle
         * increase, and tilting forwards makes the angle decrease. If the angle
         * returned is incorrect, verify that
         * the navX axises are matching to the robot axises, or use the
         * omnimount feature (as specified in
         * Kauailabs's website).
         */
        get() = try {
            navX!!.pitch.toDouble()
        } catch (E: RuntimeException) {
            0.0
        }
    val pitchAngleRad: Double
        /**
         * @return The angle of the navX on the X axis (forward-backward tilt) in
         * radians. Tilting backwards makes the angle
         * increase, and tilting forwards makes the angle decrease. If the angle
         * returned is incorrect, verify that
         * the navX axises are matching to the robot axises, or use the
         * omnimount feature (as specified in
         * kauailabs's website).
         */
        get() = Math.toRadians(pitchAngleDeg)
    val pitchRotation2d: Rotation2d
        /**
         * @return The angle of the navX on the X axis (forward-backward tilt) as a
         * Rotation2d. Tilting backwards makes the
         * angle increase, and tilting forwards makes the angle decrease. If the
         * angle returned is incorrect, verify
         * that the navX axises are matching to the robot axises, or use the
         * omnimount feature (as specified in
         * kauailabs's website).
         */
        get() = Rotation2d.fromDegrees(pitchAngleDeg)
    private val rollAngleDeg: Double
        /**
         * @return The angle of the navX on the Y axis (left-right tilt) in degrees.
         * Tilting left makes the angle increase,
         * and tilting right makes the angle decrease. If the angle returned is
         * incorrect, verify that the navX
         * axises are matching to the robot axises, or use the omnimount feature
         * (as specified in kauailabs's
         * website).
         */
        get() = try {
            navX!!.roll.toDouble()
        } catch (E: RuntimeException) {
            0.0
        }
    val rollAngleRad: Double
        /**
         * @return The angle of the navX on the Y axis (left-right tilt) in radians.
         * Tilting left makes the angle increase,
         * and tilting right makes the angle decrease.If the angle returned is
         * incorrect, verify that the navX
         * axises are matching to the robot axises, or use the omnimount feature
         * (as specified in kauailabs's
         * website).
         */
        get() = Math.toRadians(rollAngleDeg)
    val rollRotation2d: Rotation2d
        /**
         * @return The angle of the navX on the Y axis (left-right tilt) as a
         * Rotation2d. Tilting left makes the angle
         * increase, and tilting right makes the angle decrease. If the angle
         * returned is incorrect, verify that the
         * navX axises are matching to the robot axises, or use the omnimount
         * feature (as specified in kauailabs's
         * website).
         */
        get() = Rotation2d.fromDegrees(rollAngleDeg)
    private val angularVelocityDegPS: Double
        /**
         * @return the rate of change in the angle on the Z axis (perpendicular to
         * earth, "yaw") in degrees. Rotating
         * counter-clockwise returns a positive value, and clockwise returns a
         * negative value.
         */
        get() = try {
            -navX!!.rate
        } catch (E: RuntimeException) {
            0.0
        }
    val angularVelocityRadPS: Double
        /**
         * @return the rate of change in the angle on the Z axis (perpendicular to
         * earth, "yaw") in radians. Rotating
         * counter-clockwise returns a positive value, and clockwise returns a
         * negative value.
         */
        get() = Math.toRadians(angularVelocityDegPS)

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("HaNavx")
        builder.addDoubleProperty("YawAngleDeg", { yawAngleDeg }, null)
        builder.addDoubleProperty("YawAngleRad", { yawAngleRad }, null)
        builder.addDoubleProperty("PitchAngleDeg", { pitchAngleDeg }, null)
        builder.addDoubleProperty("RollAngleDeg", { rollAngleDeg }, null)
        builder.addDoubleProperty("Offset", { yawOffsetDeg }, null)
    }

    /*
	 * Waits until the navX is connected and calibrated, or 5 seconds have passed
	 * since startup. If the former, print
	 * that the navX is done calibrating and continue; If the latter, print that
	 * communication has failed or navX did
	 * not calibrate and continue.
	 */
    private fun initialize(port: SerialPort.Port) {
        commsTimoutTimer.start()
        navX = AHRS(port, SerialDataType.kProcessedData, 60.toByte())
        navX!!.enableLogging(true)
        initReport()
    }

    /*
	 * Waits until the navX is connected and calibrated, or 5 seconds have passed
	 * since startup. If the former, print
	 * that the navX is done calibrating and continue; If the latter, print that
	 * communication has failed or navX did
	 * not calibrate and continue.
	 */
    private fun initialize(port: I2C.Port) {
        commsTimoutTimer.start()
        navX = AHRS(port)
        navX!!.enableLogging(true)
        initReport()
    }

    /*
	 * Waits until the navX is connected and calibrated, or 5 seconds have passed
	 * since startup. If the former, print
	 * that the navX is done calibrating and continue; If the latter, print that
	 * communication has failed or navX did
	 * not calibrate and continue.
	 */
    private fun initialize(port: SPI.Port) {
        commsTimoutTimer.start()
        navX = AHRS(port)
        navX!!.enableLogging(true)
        initReport()
    }

    private fun initReport() {
        // Wait until navX is connected or 5 seconds have passed
        while (!navX!!.isConnected) {
            if (commsTimoutTimer.hasElapsed(kTimeoutSec)) {
                break
            }
        }
        // Wait until navX is calibrated or 5 seconds have passed
        while (navX!!.isCalibrating) {
            if (commsTimoutTimer.hasElapsed(kTimeoutSec)) {
                break
            }
        }

        // If 5 seconds have passed
        if (commsTimoutTimer.hasElapsed(kTimeoutSec)) {
            DriverStation.reportError(
                "Failed to connect to navX, or navX didn't calibrate, within "
                        + kTimeoutSec.toString() + " seconds from startup.", true
            )
        } else {
            print(
                "navX done calibrating in " + commsTimoutTimer.get().toString()
                        + " seconds from startup."
            )
        }
        commsTimoutTimer.stop()
    }

    companion object {
        private const val kTimeoutSec = 5.0
    }
}
