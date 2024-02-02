package com.hamosad1657.lib.lights

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer

/**
 * A wrapper class for WPILib's [AddressableLED], for a LED
 * strip connected to one of the RoboRIO's PWM channels.
 */
class HaLEDStrip(val PWMChannel: Int, val length: Int) {
	private val ledsBuffer = AddressableLEDBuffer(length)
	private val leds = AddressableLED(PWMChannel).apply {
		setLength(length)
		setData(ledsBuffer)
		start()
	}

	// These three functions do not use each other because
	// 1) leds.setData has to be called for the changes to take effect, and
	// 2) calling it is an expensive operation that should be done as little as possible.

	fun setAllStripRGB(color: RGBColor) {
		for (i in 0 until length) {
			ledsBuffer.setRGB(i, color.red, color.green, color.blue)
		}
		leds.setData(ledsBuffer)
	}

	fun setPixelRangeRGB(color: RGBColor, startIndex: Int, endIndex: Int) {
		require(startIndex in 0 until length)
		require(endIndex in 0 until length)
		require(startIndex <= endIndex)

		for (i in startIndex until endIndex) {
			ledsBuffer.setRGB(i, color.red, color.green, color.blue)
		}
		leds.setData(ledsBuffer)
	}

	fun setPixelRangeRGB(color: RGBColor, range: IntRange) {
		setPixelRangeRGB(color, range.first, range.last)
	}

	fun setPixelRGB(color: RGBColor, pixelIndex: Int) {
		require(pixelIndex in 0 until length)
		ledsBuffer.setRGB(pixelIndex, color.red, color.green, color.blue)
		leds.setData(ledsBuffer)
	}

	fun setAllStripHSV(color: HSVColor) {
		for (i in 0 until length) {
			ledsBuffer.setHSV(i, color.hue, color.saturation, color.value)
		}
		leds.setData(ledsBuffer)
	}

	fun setPixelRangeHSV(color: HSVColor, startIndex: Int, endIndex: Int) {
		require(startIndex in 0 until length)
		require(endIndex in 0 until length)
		require(startIndex <= endIndex)

		for (i in startIndex until endIndex) {
			ledsBuffer.setHSV(i, color.hue, color.saturation, color.value)
		}
		leds.setData(ledsBuffer)
	}

	fun setPixelRangeHSV(color: HSVColor, range: IntRange) {
		setPixelRangeHSV(color, range.first, range.last)
	}

	fun setPixelHSV(color: HSVColor, pixelIndex: Int) {
		require(pixelIndex in 0 until length)
		ledsBuffer.setHSV(pixelIndex, color.hue, color.saturation, color.value)
		leds.setData(ledsBuffer)
	}

	fun turnOff() {
		for (i in 0 until length) {
			ledsBuffer.setRGB(i, 0, 0, 0)
		}
		leds.setData(ledsBuffer)
	}

	// Essentially copied from WPILib

	private var rainbowFirstPixelHue = 0
	/** Should be called periodically. */
	private fun rainbow() {
		// For every pixel
		for (i in 0 until ledsBuffer.length) {
			// Calculate the hue - HSV is easier for rainbows because the color
			// shape is a circle so only one value needs to precess
			val hue = (rainbowFirstPixelHue + i * 180 / ledsBuffer.length) % 180
			// Set the value
			ledsBuffer.setHSV(i, hue, 255, 128)
		}
		// Increase by to make the rainbow "move"
		rainbowFirstPixelHue += 3
		// Check bounds
		rainbowFirstPixelHue %= 180
	}
}