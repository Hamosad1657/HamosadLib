package math

import com.hamosad1657.lib.math.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OperationsTest {

	@Test
	fun testDeadband() {
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