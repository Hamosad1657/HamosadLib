package com.hamosad1657.lib.lights

/** Color values from 0 to 255. */
data class RGBColor(val red: Int, val green: Int, val blue: Int)

/**
 * [hue] is from 0 to 180.
 * [saturation] and [value] are 0 to 255.
 */
data class HSVColor(val hue: Int, val saturation: Int, val value: Int)

val HAMOSAD_1657_RGB = RGBColor(22, 87, 0)
val HAMOSAD_1657_HSV = HSVColor(52, 255, 87)