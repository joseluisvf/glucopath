package pt.joseluisvf.glucopath.domain.user

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}
import pt.joseluisvf.glucopath.exception.{CarbohydrateMitigationOutsideReasonableBoundsError, DiabeticProfileException, GlucoseMitigationOutsideReasonableBoundsError, GlucoseRangeOutsideReasonableBoundsError}

class DiabeticProfileTest extends WordSpec with Matchers with GivenWhenThen {
  var diabeticProfile: DiabeticProfile = _
  var caught: DiabeticProfileException = _

  "A diabetic profile" when {
    "being created with invalid values" when {

      "the glucose mitigation is negative" should {
        "fail" in {
          Given("a diabetic profile with negative glucose mitigation")
          When("it is created")
          Then("a corresponding error should be the result")
          caught = intercept[DiabeticProfileException] {
            diabeticProfile = DiabeticProfile(-1, (80, 150), 12)
          }
          caught.getGlucopathError should equal(GlucoseMitigationOutsideReasonableBoundsError(-1))
        }
      }

      "the minimum glucose range is negative" should {
        "fail" in {
          Given("a diabetic profile with negative minimum glucose")
          When("it is created")
          Then("a corresponding error should be the result")
          caught = intercept[DiabeticProfileException] {
            diabeticProfile = DiabeticProfile(50, (-1, 150), 12)
          }
          caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((-1, 150)))
        }
      }

      "the maximum glucose range is negative" should {
        "fail" in {
          Given("a diabetic profile with negative maximum mitigation")
          When("it is created")
          Then("a corresponding error should be the result")
          caught = intercept[DiabeticProfileException] {
            diabeticProfile = DiabeticProfile(50, (80, -1), 12)
          }
          caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((80, -1)))
        }
      }

      "the carbohydrate mitigation is negative" should {
        "fail" in {
          Given("a diabetic profile with negative carbohydrate mitigation")
          When("it is created")
          Then("a corresponding error should be the result")
          caught = intercept[DiabeticProfileException] {
            diabeticProfile = DiabeticProfile(50, (80, 150), -1)
          }
          caught.getGlucopathError should equal(CarbohydrateMitigationOutsideReasonableBoundsError(-1))
        }
      }

      "the range is invalid" when {
        "the minimum range is higher than the minimum range" should {
          "fail" in {
            Given("a diabetic profile with a minimum range higher than the maximum")
            When("it is created")
            Then("a corresponding error should be the result")
            caught = intercept[DiabeticProfileException] {
              diabeticProfile = DiabeticProfile(50, (80, 60), 12)
            }
            caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((80, 60)))
          }
        }
        "the maximum range is lower than the minimum range" should {
          "fail" in {
            Given("a diabetic profile with a maximum range lower than the maximum")
            When("it is created")
            Then("a corresponding error should be the result")
            caught = intercept[DiabeticProfileException] {
              diabeticProfile = DiabeticProfile(50, (200, 150), 12)
            }
            caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((200, 150)))
          }
        }
      }

      "the carbohydrate mitigation is outside bounds" should {
        "fail" in {
          Given("a diabetic profile with the carbohydrate mitigation outside bounds")
          When("it is created")
          Then("a corresponding error should be the result")
          caught = intercept[DiabeticProfileException] {
            diabeticProfile = DiabeticProfile(50, (80, 150), 900)
          }

          caught.getGlucopathError should equal(CarbohydrateMitigationOutsideReasonableBoundsError(900))

        }
      }
    }

    "calculating how much insulin to administer" should {
      "work properly given a normal scenario" in {
        Given("a well-formed diabetic profile, some carbohydrates being eaten and a high glucose reading")
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 200
        val carbohydratesEaten = 36
        val expected = 5
        When("calculating how much insulin to administer")
        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        Then("the result should be high, taking into account the glucose and carbohydrates")
        result should equal(expected)
      }

      "work even if no carbs were consumed" in {
        Given("a well-formed diabetic profile, no carbohydrates being eaten and a high glucose reading")
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 200
        val carbohydratesEaten = 0
        val expected = 2
        When("calculating how much insulin to administer")
        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        Then("the result should be high, only taking into account the glucose")
        result should equal(expected)
      }

      "take into consideration when ones glucose levels are lower than normal" in {
        Given("a well-formed diabetic profile, no carbohydrates being eaten and a low glucose reading")
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 60
        val carbohydratesEaten = 0
        val expected = 0
        When("calculating how much insulin to administer")
        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        Then("the result should be nil, taking into account the glucose")
        result should equal(expected)
      }

      "take into consideration when ones glucose levels are lower than normal when carbs are consumed" in {
        Given("a well-formed diabetic profile, some carbohydrates being eaten and a low glucose reading")
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 40
        val carbohydratesEaten = 36
        val expected = 2
        When("calculating how much insulin to administer")
        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        Then("the result should be low, taking into account the glucose measured and the carbohydrates")
        result should equal(expected)
      }

      "work properly for very specific ranges" in {
        diabeticProfile = DiabeticProfile(50, (100, 101), 12)
        Given("a diabetic profile with a short range, some carbohydrates being eaten and a high glucose reading")
        val glucoseMeasured = 200
        val carbohydratesEaten = 36
        val expected = 5
        When("calculating how much insulin to administer")
        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        Then("the result should be high, taking into account the glucose measured and the carbohydrates")
        result should equal(expected)
      }
    }

    "checking a glucose value" should {
      "properly identify hypoglicemias" in {
        Given("a well-formed diabetic profile")
        diabeticProfile = DiabeticProfile(40, (80, 160), 10)
        When("identifying glucose readings as hypoglycemias")
        Then("a reading below the minimum should be identified as a hypoglycemia")
        diabeticProfile.isGlucoseHypoglycemia(79) should equal(true)
        Then("a reading equal to the minimum should not be identified as a hypoglycemia")
        diabeticProfile.isGlucoseHypoglycemia(80) should equal(false)
      }

      "properly identify hyperglicemias" in {
        Given("a well-formed diabetic profile")
        diabeticProfile = DiabeticProfile(40, (80, 160), 10)
        When("identifying glucose readings as hyperglycemias")
        Then("a reading above the maximum should be identified as a hyperglycemia")
        diabeticProfile.isGlucoseHyperglycemia(161) should equal(true)
        Then("a reading equal to the maximum should not be identified as a hyperglycemia")
        diabeticProfile.isGlucoseHyperglycemia(160) should equal(false)
      }

      "properly identify if a glucose is in range" in {
        Given("a well-formed diabetic profile")
        diabeticProfile = DiabeticProfile(40, (80, 160), 10)
        When("identifying glucose readings in the ideal range")
        Then("a reading equal to the minimum should be identified as being in range")
        diabeticProfile.isGlucoseInRange(80) should equal(true)
        Then("a reading equal to the maximum should be identified as being in range")
        diabeticProfile.isGlucoseInRange(160) should equal(true)
        Then("a reading inside the range should be identified as being in range")
        diabeticProfile.isGlucoseInRange(120) should equal(true)
        Then("a reading below the minimum should be identified as not being in range")
        diabeticProfile.isGlucoseInRange(79) should equal(false)
        Then("a reading above the maximum should be identified as not being in range")
        diabeticProfile.isGlucoseInRange(161) should equal(false)
      }
    }
  }
}
