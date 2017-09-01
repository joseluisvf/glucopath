package pt.joseluisvf.glucopath.domain.day

import java.time.LocalTime

import org.scalatest.{Matchers, WordSpec}
import pt.joseluisvf.glucopath.exception.{AmountOutsideReasonableBoundsError, SlowInsulinException}

class SlowInsulinTest extends WordSpec with Matchers {
  "Slow Insulin" should {
    "not be created with an amount of slow insulin below reasonable bounds" in {
      val result = intercept[SlowInsulinException] {
        SlowInsulin(LocalTime.of(12,12), SlowInsulin.MINIMUM_AMOUNT - 1)
      }
      result.getGlucopathError should equal(AmountOutsideReasonableBoundsError(SlowInsulin.MINIMUM_AMOUNT - 1))
    }

    "not be created with an amount of slow insulin above reasonable bounds" in {
      val result = intercept[SlowInsulinException] {
        SlowInsulin(LocalTime.of(12,12), SlowInsulin.MAXIMUM_AMOUNT + 1)
      }
      result.getGlucopathError should equal(AmountOutsideReasonableBoundsError(SlowInsulin.MAXIMUM_AMOUNT + 1))
    }

    "be created with an amount of slow insulin within reasonable bounds" in {
      val result = SlowInsulin(LocalTime.of(12,12), 30)
      result.amount should equal(30)
    }
  }
}
