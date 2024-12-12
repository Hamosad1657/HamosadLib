package units

import com.hamosad1657.lib.controllers.powerProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.atan2

class ControllerTest {
	@Test
	fun aTan2Test() {
		assertEquals((7.0 / 4.0) * PI, PI + (PI + atan2(-1.0, 1.0)), 0.01)
	}

	@Test
	fun powerProfileTest() {
		assertEquals(-0.5 * 0.5 * 0.5, (-0.5).powerProfile(3))
	}
}