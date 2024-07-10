package math

import com.hamosad1657.lib.math.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.math.pow

private const val allowableFloatingPointError = 0.0001

class OperationsTest {

	@Test
	fun testSimpleDeadband() {
		assertEquals(0.0, simpleDeadband(0.0, 0.0))
		assertEquals(3.0, simpleDeadband(3.0, 0.0))
		assertEquals(0.0, simpleDeadband(0.0, 3.0))
		assertEquals(3.0, simpleDeadband(3.0, 3.0))
		assertEquals(0.0, simpleDeadband(3.0, 4.0))
		assertEquals(4.0, simpleDeadband(4.0, 3.0))
		assertEquals(-3.0, simpleDeadband(-3.0, 0.0))
		assertEquals(-3.0, simpleDeadband(-3.0, 3.0))
		assertEquals(0.0, simpleDeadband(-3.0, 4.0))
		assertEquals(-4.0, simpleDeadband(-4.0, 3.0))
	}

	@Test
	fun testContinuousDeadband() {
		assertEquals(0.0, continuousDeadband(0.0, 0.0))
		assertEquals(0.05, continuousDeadband(0.05, 0.0))

		assertEquals(0.0, continuousDeadband(0.05, 0.1))
		assertEquals(0.0, continuousDeadband(0.1, 0.1))
		assertEquals(1.0, continuousDeadband(1.0, 0.1))
		assertEquals(0.4444, continuousDeadband(0.5, 0.1), allowableFloatingPointError)

		assertEquals(0.0, continuousDeadband(-0.05, 0.1))
		assertEquals(0.0, continuousDeadband(-0.1, 0.1))
		assertEquals(-1.0, continuousDeadband(-1.0, 0.1))
		assertEquals(-0.4444, continuousDeadband(-0.5, 0.1), allowableFloatingPointError)
	}

	@Test
	fun testDoubleClamp() {
		assertEquals(0.0, clamp(0.0, 0.0, 0.0))
		assertEquals(0.0, clamp(3.0, 0.0, 0.0))
		assertEquals(0.0, clamp(0.0, 3.0, 0.0))
		assertEquals(0.0, clamp(0.0, 0.0, 3.0))
		assertEquals(0.0, clamp(3.0, 3.0, 0.0))

		assertEquals(3.0, clamp(0.0, 3.0, 3.0))
		assertEquals(3.0, clamp(3.0, 0.0, 3.0))
		assertEquals(3.0, clamp(3.0, 3.0, 3.0))
		assertEquals(3.0, clamp(4.0, 0.0, 3.0))

		assertEquals(3.0, clamp(3.0, 0.0, 4.0))
		assertEquals(-3.0, clamp(-3.0, -3.0, 0.0))
		assertEquals(-3.0, clamp(-4.0, -3.0, 0.0))
	}

	@Test
	fun testIntClamp() {
		assertEquals(0, clamp(0, 0, 0))
		assertEquals(0, clamp(3, 0, 0))
		assertEquals(0, clamp(0, 3, 0))
		assertEquals(0, clamp(0, 0, 3))
		assertEquals(0, clamp(3, 3, 0))

		assertEquals(3, clamp(0, 3, 3))
		assertEquals(3, clamp(3, 0, 3))
		assertEquals(3, clamp(3, 3, 3))
		assertEquals(3, clamp(4, 0, 3))
		assertEquals(3, clamp(3, 0, 4))

		assertEquals(-3, clamp(-3, -3, 0))
		assertEquals(-3, clamp(-4, -3, 0))
	}

	@Test
	fun testDoubleMapRange() {
		assertEquals(3.0, mapRange(3.0, -3.0, 3.0, -3.0, 3.0))
		assertEquals(-3.0, mapRange(-3.0, -3.0, 3.0, -3.0, 3.0))

		assertEquals(0.5, mapRange(50.0, -100.0, 100.0, -1.0, 1.0))

		assertEquals(-160.0, mapRange(20.0, 0.0, 360.0, -180.0, 180.0))
	}

	@Test
	fun testIntMapRange() {
		assertEquals(3.0, mapRange(3, -3, 3, -3, 3))
		assertEquals(-3.0, mapRange(-3, -3, 3, -3, 3))

		assertEquals(0.5, mapRange(50, -100, 100, -1, 1))

		assertEquals(-160.0, mapRange(20, 0, 360, -180, 180))
	}

	@Test
	fun testMedian() {
		val array1 = doubleArrayOf(45.2, -1.0, 5.07, -13.9)
		assertEquals(2.035, median(array1))

		val array2 = doubleArrayOf(45.2, -1.0, 5.07, -13.9, 40.905)
		assertEquals(5.07, median(array2))
	}
}