package com.hamosad1657.lib.math.filters.movingwindowfilters

import java.util.LinkedList

/**
 * A class for a moving median filter, a type of low-pass filter with a finite memory.
 * Useful for removing occasional outliers from the input stream.
 * ```
 * Since filters have memory, use a separate instance for every input stream.
 * ```
 * @param window - The number of samples to be included in the median calculation.
 *                  Assuming calculate is called periodically, the period times the
 *                  window is the time frame of the filter. The filter will more-or-less
 *                  cancel out dynamics that happen in a shorter time frame than this,
 *                  and that will also be the approximate phase lag.
 */
class MovingMedianFilter(window: Int) : MovingWindowFilter() {
    override var window: Int = window
        set(value) {
            require(value > 0) {"window must be positive"}
            field = value
        }
    override val calculation = {values: LinkedList<Double> -> median(values) }
}

private fun median(linkedList: LinkedList<Double>): Double {
    val list = linkedList.sorted()
    val listSize = list.size
    if(listSize == 2) return list.average()

    return if(listSize % 2 == 0) {
        (list[listSize / 2] + list[(listSize / 2) - 1]) / 2.0
    }
    else {
        list[((listSize / 2) - 0.5).toInt()]
    }
}