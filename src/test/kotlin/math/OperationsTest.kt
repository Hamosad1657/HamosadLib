package math

import com.hamosad1657.lib.math.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.math.pow

class OperationsTest {
	private fun Double.roundWithPrecision(decimals: Int): Double {
		val multiplier = 10.0.pow(decimals)
		return floor(this * multiplier) / multiplier
	}

	private fun assertEqualsWithRounding(actual: Double, expected: Double) {
		Assertions.assertEquals(
			expected.roundWithPrecision(5),
			actual.roundWithPrecision(5)
		)
	}

	@Test
	fun testSimpleDeadband() {
		assertEquals(simpleDeadband(0.0, 0.0), 0.0)
		assertEquals(simpleDeadband(3.0, 0.0), 3.0)
		assertEquals(simpleDeadband(0.0, 3.0), 0.0)
		assertEquals(simpleDeadband(3.0, 3.0), 3.0)
		assertEquals(simpleDeadband(3.0, 4.0), 0.0)
		assertEquals(simpleDeadband(4.0, 3.0), 4.0)
		assertEquals(simpleDeadband(-3.0, 0.0), -3.0)
		assertEquals(simpleDeadband(-3.0, 3.0), -3.0)
		assertEquals(simpleDeadband(-3.0, 4.0), 0.0)
		assertEquals(simpleDeadband(-4.0, 3.0), -4.0)
	}

	@Test
	fun testContinuousDeadband() {
		assertEquals(continuousDeadband(0.0, 0.0), 0.0)
		assertEquals(continuousDeadband(0.05, 0.0), 0.05)

		assertEquals(continuousDeadband(0.05, 0.1), 0.0)
		assertEquals(continuousDeadband(0.1, 0.1), 0.0)
		assertEquals(continuousDeadband(1.0, 0.1), 1.0)
		assertEqualsWithRounding(continuousDeadband(0.5, 0.1), 0.44444)

		assertEquals(continuousDeadband(-0.05, 0.1), 0.0)
		assertEquals(continuousDeadband(-0.1, 0.1), 0.0)
		assertEquals(continuousDeadband(-1.0, 0.1), -1.0)
		assertEqualsWithRounding(continuousDeadband(-0.5, 0.1), -0.44445)
	}

	@Test
	fun testClamp() {
		assertEquals(clamp(0.0, 0.0, 0.0), 0.0)
		assertEquals(clamp(3.0, 0.0, 0.0), 0.0)
		assertEquals(clamp(0.0, 3.0, 0.0), 0.0)
		assertEquals(clamp(0.0, 0.0, 3.0), 0.0)
		assertEquals(clamp(3.0, 3.0, 0.0), 0.0)
		assertEquals(clamp(0.0, 3.0, 3.0), 3.0)
		assertEquals(clamp(3.0, 0.0, 3.0), 3.0)
		assertEquals(clamp(3.0, 3.0, 3.0), 3.0)
		assertEquals(clamp(4.0, 0.0, 3.0), 3.0)
		assertEquals(clamp(3.0, 0.0, 4.0), 3.0)
		assertEquals(clamp(-3.0, -3.0, 0.0), -3.0)
		assertEquals(clamp(-4.0, -3.0, 0.0), -3.0)
	}

	@Test
	fun testMapRange() {
		println(mapRange(0.5, 0.1, 1.0, 0.0, 1.0))
		assertEquals(mapRange(3.0, -3.0, 3.0, -3.0, 3.0), 3.0)
		assertEquals(mapRange(-3.0, -3.0, 3.0, -3.0, 3.0), -3.0)

		assertEquals(mapRange(50.0, -100.0, 100.0, -1.0, 1.0), 0.5)

		assertEquals(mapRange(20.0, 0.0, 360.0, -180.0, 180.0), -160.0)
	}

	@Test
	fun testMedian() {
		val array1 = doubleArrayOf(45.2, -1.0, 5.07, -13.9)
		assertEquals(median(array1), 2.035)

		val array2 = doubleArrayOf(45.2, -1.0, 5.07, -13.9, 40.905)
		assertEquals(median(array2), 5.07)
	}
}