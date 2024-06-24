package com.hamosad1657.lib.leds

import com.hamosad1657.lib.math.clamp
import com.hamosad1657.lib.units.Seconds
import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer

@OptIn(ExperimentalStdlibApi::class)

/** ONLY CREATE ONE INSTANCE OF THIS CLASS. */
class LEDStrip(val length: Int, pwmPort: Int) {
	// --- LEDs ---

	private val ledBuffer = AddressableLEDBuffer(length)
	private val ledStrip = AddressableLED(pwmPort).apply {
		setLength(length)
		setData(ledBuffer)
		start()
	}

	// Used for toggling the LEDs
	private var lastAppliedColor = LEDS_OFF

	private val blinkTimer = Timer()


	fun getColorForIndex(index: Int) = RGBColor(
		ledBuffer.getRed(index),
		ledBuffer.getGreen(index),
		ledBuffer.getBlue(index)
	)

	// --- LEDs Control ---

	fun applyColorForAll(color: RGBColor) {
		for (i in 0..length) {
			bufferSetRGB(i, color)
		}
		ledStrip.setData(ledBuffer)
		if (color != LEDS_OFF) lastAppliedColor = color
	}

	/**
	 * @param color color to apply if the strip is off, instead of the last applied color.
	 */
	fun toggleEntireStrip(color: RGBColor) {
		val isOn = getColorForIndex(0) != LEDS_OFF
		if (isOn) turnOff()
		else applyColorForAll(color)
	}

	/** Reapplies the last applied color if the strip is off. */
	fun toggleEntireStrip() {
		toggleEntireStrip(lastAppliedColor)
	}

	/** Should be called periodically.
	 * @param color color to blink in, instead of the last applied color.
	 */
	fun blinkEntireStrip(blinkTime: Seconds, color: RGBColor) {
		blinkTimer.start()
		if (blinkTimer.hasElapsed(blinkTime)) {
			toggleEntireStrip(color)
			blinkTimer.restart()
		}
	}

	/** Should be called periodically. Blinks in the last applied color. */
	fun blinkEntireStrip(blinkTime: Seconds) {
		blinkEntireStrip(blinkTime, lastAppliedColor)
	}

	fun turnOff() {
		applyColorForAll(LEDS_OFF)
	}

	/** If setting the entire strip, use [applyColorForAll]. */
	fun applyColor(color: RGBColor, step: Int = 1, startIndex: Int = 0, endIndex: Int = length) {
		val start = clampAndReportIndex(startIndex)
		val end = clampAndReportIndex(endIndex)

		for (i in start..<end step step) {
			bufferSetRGB(i, color)
		}
		ledStrip.setData(ledBuffer)
		if (color != LEDS_OFF) lastAppliedColor = color
	}

	/**
	 * If toggling the entire strip together, use [toggleEntireStrip], it is more efficient.
	 * @param color color to apply if a LED is off, instead of the last applied color.
	 */
	fun toggle(color: RGBColor, step: Int = 1, startIndex: Int = 0, endIndex: Int = length) {
		fun toggleSingleLED(index: Int) {
			val currentColor = getColorForIndex(index)
			val colorToApply = if (currentColor != LEDS_OFF) LEDS_OFF else color
			bufferSetRGB(index, colorToApply)
		}

		val start = clampAndReportIndex(startIndex)
		val end = clampAndReportIndex(endIndex)

		for (i in start..end step step) {
			toggleSingleLED(i)
		}
		ledStrip.setData(ledBuffer)
	}

	/**
	 * Applies the last applied color if a LED is off.
	 * If toggling the entire strip together, use [toggleEntireStrip], it is more efficient.
	 */
	fun toggle(step: Int = 1, startIndex: Int = 0, endIndex: Int = length) {
		toggle(lastAppliedColor)
	}

	/**
	 * Should be called periodically.
	 * If blinking the entire strip, use [blinkEntireStrip], it is more efficient.
	 * @param color color to blink in, instead of the last applied color.
	 */
	fun blink(blinkTime: Seconds, color: RGBColor, step: Int = 1, startIndex: Int = 0, endIndex: Int = length) {
		blinkTimer.start()
		if (blinkTimer.hasElapsed(blinkTime)) {
			toggle(color, step, startIndex, endIndex)
			blinkTimer.restart()
		}
	}

	/**
	 * Should be called periodically.
	 * If blinking the entire strip, use [blinkEntireStrip], it is more efficient.
	 */
	fun blink(blinkTime: Seconds, step: Int = 1, startIndex: Int = 0, endIndex: Int = length) {
		blink(blinkTime, lastAppliedColor, step, startIndex, endIndex)
	}
	
	companion object {
		val LEDS_OFF = RGBColor.BLACK
	}

	private fun clampAndReportIndex(index: Int): Int {
		if (index in 0..length) return index
		DriverStation.reportError("Index $index is out of range for length $length. Is being clamped between 0 and $length to avoid an error.",
			true)
		return clamp(index, 0, length)
	}

	private fun bufferSetRGB(index: Int, color: RGBColor) {
		ledBuffer.setRGB(index, color.red, color.green, color.blue)
	}
}