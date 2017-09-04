package pt.joseluisvf.glucopath.domain.day

import java.time.LocalTime

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}
import pt.joseluisvf.glucopath.exception.{AmountOutsideReasonableBoundsError, SlowInsulinException}

class SlowInsulinTest extends WordSpec with Matchers with GivenWhenThen {
  "Slow Insulin" should {
    "not be created with an amount of slow insulin below reasonable bounds" in {
      Given("A slow insulin with a lower insulin amount than the minimum")
      val result = intercept[SlowInsulinException] {
      When("it is created")
        SlowInsulin(LocalTime.of(12, 12), SlowInsulin.MINIMUM_AMOUNT - 1)
      }
      Then("an error corresponding to the low amount of insulin should be returned inside a wrapper exception")
      result.getGlucopathError should equal(AmountOutsideReasonableBoundsError(SlowInsulin.MINIMUM_AMOUNT - 1))
    }

    "not be created with an amount of slow insulin above reasonable bounds" in {
      Given("A slow insulin with a higher insulin amount than the maximum")
      val result = intercept[SlowInsulinException] {
      When("it is created")
        SlowInsulin(LocalTime.of(12, 12), SlowInsulin.MAXIMUM_AMOUNT + 1)
      }
      Then("an error corresponding to the high amount of insulin should be returned inside a wrapper exception")
      result.getGlucopathError should equal(AmountOutsideReasonableBoundsError(SlowInsulin.MAXIMUM_AMOUNT + 1))
    }

    "be created with an amount of slow insulin within reasonable bounds" in {
      Given("A slow insulin with a lower insulin amount than the minimum")
      When("it is created")
      Then("an error corresponding to the low amount of insulin should be returned inside a wrapper exception")
      val result = SlowInsulin(LocalTime.of(12, 12), 30)
      result.amount should equal(30)
    }
  }
}
