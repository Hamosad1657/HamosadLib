package units

import com.hamosad1657.lib.units.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.floor
import kotlin.math.pow

class ConversionsTest {
	private fun Double.roundWithPrecision(decimals: Int): Double {
		val multiplier = 10.0.pow(decimals)
		return floor(this * multiplier) / multiplier
	}

	private fun assertEqualsWithPrecision(actual: Double, expected: Double) {
		Assertions.assertEquals(
			expected.roundWithPrecision(4),
			actual.roundWithPrecision(4)
		)
	}

	/// --- Angular Velocities to Angular Velocities Conversions ---

	@Test
	fun rpmToRpsTest() {
		assertEqualsWithPrecision(rpmToRps(0.0), 0.0)
		assertEqualsWithPrecision(rpmToRps(3.0), 0.05)
	}

	@Test
	fun rpmToRadPsTest() {
		assertEqualsWithPrecision(rpmToRadPs(0.0), 0.0)
		assertEqualsWithPrecision(rpmToRadPs(3.0), 0.3141)
	}

	@Test
	fun rpmToDegPsTest() {
		assertEqualsWithPrecision(rpmToDegPs(0.0), 0.0)
		assertEqualsWithPrecision(rpmToDegPs(3.0), 18.0)
	}

	@Test
	fun rpsToRpmTest() {
		assertEqualsWithPrecision(rpsToRpm(0.0), 0.0)
		assertEqualsWithPrecision(rpsToRpm(3.0), 180.0)
	}

	@Test
	fun rpsToRadPsTest() {
		assertEqualsWithPrecision(rpsToRadPs(0.0), 0.0)
		assertEqualsWithPrecision(rpsToRadPs(3.0), 18.8495)
	}

	@Test
	fun rpsToDegPsTest() {
		assertEqualsWithPrecision(rpsToDegPs(0.0), 0.0)
		assertEqualsWithPrecision(rpsToDegPs(3.0), 1080.0)
	}

	@Test
	fun radPsToRpmTest() {
		assertEqualsWithPrecision(radPsToRpm(0.0), 0.0)
		assertEqualsWithPrecision(radPsToRpm(3.0), 28.6478)
	}

	@Test
	fun radPsToRpsTest() {
		assertEqualsWithPrecision(radPsToRps(0.0), 0.0)
		assertEqualsWithPrecision(radPsToRps(3.0), 0.4774)
	}

	@Test
	fun radPsToDegPsTest() {
		assertEqualsWithPrecision(radPsToDegPs(0.0), 0.0)
		assertEqualsWithPrecision(radPsToDegPs(3.0), 171.8873)
	}

	@Test
	fun degPsToRpmTest() {
		assertEqualsWithPrecision(degPsToRpm(0.0), 0.0)
		assertEqualsWithPrecision(degPsToRpm(3.0), 0.5)
	}

	@Test
	fun degPsToRpsTest() {
		assertEqualsWithPrecision(degPsToRps(0.0), 0.0)
		assertEqualsWithPrecision(degPsToRps(3.0), 0.0083)
	}

	@Test
	fun degPsToRadPsTest() {
		assertEqualsWithPrecision(degPsToRadPs(0.0), 0.0)
		assertEqualsWithPrecision(degPsToRadPs(3.0), 0.0523)
	}

	/// --- Angular Velocities to Linear Velocity Conversions ---

	@Test
	fun rpmToMpsTest() {
		assertEqualsWithPrecision(rpmToMps(0.0, Length.fromCentimeters(10.0)), 0.0)
		assertEqualsWithPrecision(rpmToMps(3.0, Length.fromCentimeters(10.0)), 0.0314)
	}

	@Test
	fun mpsToRpmTest() {
		assertEqualsWithPrecision(mpsToRpm(0.0, Length.fromCentimeters(10.0)), 0.0)
		assertEqualsWithPrecision(mpsToRpm(3.0, Length.fromCentimeters(10.0)), 286.4788)
	}

	/// --- Lengths to Lengths Conversions ---

	@Test
	fun metersToInchesTest() {
		assertEqualsWithPrecision(metersToInches(0.0), 0.0)
		assertEqualsWithPrecision(metersToInches(3.0), 118.1102)
	}

	@Test
	fun inchesToMetersTest() {
		assertEqualsWithPrecision(inchesToMeters(0.0), 0.0)
		assertEqualsWithPrecision(inchesToMeters(30.0), 0.7619)
	}

	@Test
	fun inchesToFeetTest() {
		assertEqualsWithPrecision(inchesToFeet(0.0), 0.0)
		assertEqualsWithPrecision(inchesToFeet(3.0), 0.25)
	}

	@Test
	fun feetToInchesTest() {
		assertEqualsWithPrecision(feetToInches(0.0), 0.0)
		assertEqualsWithPrecision(feetToInches(3.0), 36.0)
	}
}