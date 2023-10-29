package units

import com.hamosad1657.lib.units.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private const val allowableFloatingPointError = 0.0001

class ConversionsTest {

	/// --- Angular Velocities to Angular Velocities Conversions ---

	@Test
	fun rpmToRpsTest() {
		assertEquals(0.0, rpmToRps(0.0))
		assertEquals(0.05, rpmToRps(3.0), allowableFloatingPointError)
	}

	@Test
	fun rpmToRadPsTest() {
		assertEquals(0.0, rpmToRadPs(0.0))
		assertEquals(0.3141, rpmToRadPs(3.0), allowableFloatingPointError)
	}

	@Test
	fun rpmToDegPsTest() {
		assertEquals(0.0, rpmToDegPs(0.0))
		assertEquals(18.0, rpmToDegPs(3.0), allowableFloatingPointError)
	}

	@Test
	fun rpsToRpmTest() {
		assertEquals(0.0, rpsToRpm(0.0))
		assertEquals(180.0, rpsToRpm(3.0), allowableFloatingPointError)
	}

	@Test
	fun rpsToRadPsTest() {
		assertEquals(0.0, rpsToRadPs(0.0))
		assertEquals(18.8495, rpsToRadPs(3.0), allowableFloatingPointError)
	}

	@Test
	fun rpsToDegPsTest() {
		assertEquals(0.0, rpsToDegPs(0.0))
		assertEquals(1080.0, rpsToDegPs(3.0), allowableFloatingPointError)
	}

	@Test
	fun radPsToRpmTest() {
		assertEquals(0.0, radPsToRpm(0.0), 0.0)
		assertEquals(28.6478, radPsToRpm(3.0), allowableFloatingPointError)
	}

	@Test
	fun radPsToRpsTest() {
		assertEquals(0.0, radPsToRps(0.0))
		assertEquals(0.4774, radPsToRps(3.0), allowableFloatingPointError)
	}

	@Test
	fun radPsToDegPsTest() {
		assertEquals(0.0, radPsToDegPs(0.0))
		assertEquals(171.8873, radPsToDegPs(3.0), allowableFloatingPointError)
	}

	@Test
	fun degPsToRpmTest() {
		assertEquals(0.0, degPsToRpm(0.0))
		assertEquals(0.5, degPsToRpm(3.0), allowableFloatingPointError)
	}

	@Test
	fun degPsToRpsTest() {
		assertEquals(0.0, degPsToRps(0.0))
		assertEquals(0.0083, degPsToRps(3.0), allowableFloatingPointError)
	}

	@Test
	fun degPsToRadPsTest() {
		assertEquals(0.0, degPsToRadPs(0.0))
		assertEquals(0.0523, degPsToRadPs(3.0), allowableFloatingPointError)
	}

	/// --- Angular Velocities to Linear Velocity Conversions ---

	@Test
	fun rpmToMpsTest() {
		assertEquals(0.0, rpmToMps(0.0, Length.fromCentimeters(10.0)))
		assertEquals(0.0314, rpmToMps(3.0, Length.fromCentimeters(10.0)), allowableFloatingPointError)
	}

	@Test
	fun mpsToRpmTest() {
		assertEquals(0.0, mpsToRpm(0.0, Length.fromCentimeters(10.0)))
		assertEquals(286.4788, mpsToRpm(3.0, Length.fromCentimeters(10.0)), allowableFloatingPointError)
	}

	/// --- Lengths to Lengths Conversions ---

	@Test
	fun metersToInchesTest() {
		assertEquals(0.0, metersToInches(0.0))
		assertEquals(118.1102, metersToInches(3.0), allowableFloatingPointError)
	}

	@Test
	fun inchesToMetersTest() {
		assertEquals(0.0, inchesToMeters(0.0))
		assertEquals(0.7619, inchesToMeters(30.0), allowableFloatingPointError)
	}

	@Test
	fun inchesToFeetTest() {
		assertEquals(0.0, inchesToFeet(0.0))
		assertEquals(0.25, inchesToFeet(3.0), allowableFloatingPointError)
	}

	@Test
	fun feetToInchesTest() {
		assertEquals(0.0, feetToInches(0.0))
		assertEquals(36.0, feetToInches(3.0), allowableFloatingPointError)
	}
}