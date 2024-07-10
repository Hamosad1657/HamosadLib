package units

import com.hamosad1657.lib.units.AngularVelocity
import com.hamosad1657.lib.units.Length
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LengthTest {

	@Test
	fun testLengthEquals() {
		val a = Length.fromMeters(1.0)
		val b = Length.fromMeters(1.0)
		val c = Length.fromMeters(2.0)
		Assertions.assertEquals(a, a)
		Assertions.assertEquals(a, b)
		Assertions.assertNotEquals(a, c)
	}

	@Test
	fun testLengthComparisons() {
		val a = Length.fromMeters(1.0)
		val b = Length.fromMeters(2.0)

		Assertions.assertTrue(a < b)
		Assertions.assertFalse(b < a)

		Assertions.assertTrue(b > a)
		Assertions.assertFalse(a > b)
	}

	@Test
	fun testLengthHashCode() {
		val a = Length.fromMeters(1.0)
		val b = Length.fromMeters(1.0)
		val c = Length.fromMeters(2.0)
		Assertions.assertEquals(a.hashCode(), a.hashCode())
		Assertions.assertEquals(a.hashCode(), b.hashCode())
		Assertions.assertNotEquals(a.hashCode(), c.hashCode())
	}
}