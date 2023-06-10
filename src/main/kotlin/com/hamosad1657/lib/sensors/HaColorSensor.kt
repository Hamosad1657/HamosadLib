package com.hamosad1657.lib.sensors

import com.revrobotics.ColorSensorV3
import com.revrobotics.ColorSensorV3.RawColor
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.I2C
import edu.wpi.first.wpilibj.util.Color

class HaColorSensor(port: I2C.Port?) : Sendable {
    private val colorSensor: ColorSensorV3
    private val maxProx = 2047

    init {
        colorSensor = ColorSensorV3(port)
    }

    /**
     * @param minColor - The minimum acceptable color values (inclusive).
     * @param maxColor - The maximum acceptable color values (inclusive).
     *
     * @return Whether the detected color is in the specified range (inclusive).
     */
    fun isColorInRange(minColor: RawColor, maxColor: RawColor): Boolean {
        val color = colorSensor.rawColor
        return color.red >= minColor.red && color.red <= maxColor.red && color.blue >= minColor.blue && color.blue <= maxColor.blue && color.green >= minColor.green && color.green <= maxColor.green
    }

    /**
     * @param minProximity - The minimum acceptable proximity (inclusive).
     * @param maxProximity - The maximum acceptable proximity (inclusive).
     *
     * @return Whether the proximity is in the specified range (inclusive).
     */
    fun isObjectInProximityRange(minProximity: Double, maxProximity: Double): Boolean {
        val proximity = proximity
        return proximity >= minProximity && proximity <= maxProximity
    }

    val color: RawColor
        /**
         * @return The raw color detected by the sensor, as a RawColor object.
         */
        get() = colorSensor.rawColor
    private val red: Double
        /**
         *
         * @return The red value of the detected color.
         */
        get() = colorSensor.red.toDouble()
    private val green: Double
        /**
         *
         * @return The green value of the detected color.
         */
        get() = colorSensor.green.toDouble()
    private val blue: Double
        /**
         *
         * @return The blue value of the detected color.
         */
        get() = colorSensor.blue.toDouble()
    private val IR: Int
        /**
         *
         * @return The IR color value detected by the sensor (in CIE 1931 XYZ colorspace).
         */
        get() = colorSensor.ir
    private val proximity: Int
        /**
         *
         * @return The proximity value of the sensor ranging from 0 (object is close) to 2047 (object is far away).
         */
        get() = maxProx - colorSensor.proximity // Flip the range from [2047, 0] to [0, 2047]
    private val cmProximity: Int
        /**
         *
         * @return The proximity value of the sensor in cm
         */
        get() {
            val minCm = 1
            val maxCm = 10
            val minProx = 0
            return (minCm
                    + (proximity - minProx) * (maxCm - minCm) / (maxProx - minProx))
        }

    /**
     * @param minColor - The minimum acceptable color values (inclusive).
     * @param maxColor - The maximum acceptable color values (inclusive).
     *
     * @return Whether the detected color is in the specified range (inclusive).
     */
    fun isColorInRangePercent(minColor: Color, maxColor: Color): Boolean {
        val color = colorSensor.color
        return color.red >= minColor.red && color.red <= maxColor.red && color.blue >= minColor.blue && color.blue <= maxColor.blue && color.green >= minColor.green && color.green <= maxColor.green
    }

    val colorPercent: Color
        /**
         * @return The raw color detected by the sensor, as a RawColor object.
         */
        get() = colorSensor.color
    private val redPercent: Double
        /**
         *
         * @return The red value of the detected color.
         */
        get() = colorSensor.color.red
    private val greenPercent: Double
        /**
         *
         * @return The green value of the detected color.
         */
        get() = colorSensor.color.green
    private val bluePercent: Double
        /**
         *
         * @return The blue value of the detected color.
         */
        get() = colorSensor.color.blue

    override fun initSendable(builder: SendableBuilder) {
        builder.setSmartDashboardType("HaColorSensor")
        builder.addDoubleProperty("Red", { red }, null)
        builder.addDoubleProperty("Green", { green }, null)
        builder.addDoubleProperty("Blue", { blue }, null)
        builder.addDoubleProperty("Red %", { redPercent }, null)
        builder.addDoubleProperty("Green %", { greenPercent }, null)
        builder.addDoubleProperty("Blue %", { bluePercent }, null)
        builder.addDoubleProperty("Proximity (CM)", { cmProximity.toDouble() }, null)
        builder.addDoubleProperty("Proximity (0-2047)", { proximity.toDouble() }, null)
        builder.addDoubleProperty("IR", { IR.toDouble() }, null)
    }
}
