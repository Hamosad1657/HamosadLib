package units

import com.hamosad1657.lib.units.AngularVelocity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AngularVelocityTest {

	@Test
	fun testAngularVelocityEquals() {
		val a = AngularVelocity.fromRpm(1.0)
		val b = AngularVelocity.fromRpm(1.0)
		val c = AngularVelocity.fromRpm(2.0)
		assertEquals(a, a)
		assertEquals(a, b)
		assertNotEquals(a, c)
	}

	@Test
	fun testAngularVelocityComparisons() {
		val a = AngularVelocity.fromRpm(1.0)
		val b = AngularVelocity.fromRpm(2.0)

		assertTrue(a < b)
		assertFalse(b < a)

		assertTrue(b > a)
		assertFalse(a > b)
	}

	@Test
	fun testAngularVelocityHashCode() {
		val a = AngularVelocity.fromRpm(1.0)
		val b = AngularVelocity.fromRpm(1.0)
		val c = AngularVelocity.fromRpm(2.0)
		assertEquals(a.hashCode(), a.hashCode())
		assertEquals(a.hashCode(), b.hashCode())
		assertNotEquals(a.hashCode(), c.hashCode())
	}

	@Test
	fun testAngularVelocityAbsoluteValue() {
		val a = AngularVelocity.fromRpm(0.0)
		val b = AngularVelocity.fromRpm(1.0)
		val c = AngularVelocity.fromRpm(-1.0)

		assertEquals(0.0, a.absoluteValue.asRpm)
		assertEquals(1.0, b.absoluteValue.asRpm)
		assertEquals(1.0, c.absoluteValue.asRpm)
	}
}