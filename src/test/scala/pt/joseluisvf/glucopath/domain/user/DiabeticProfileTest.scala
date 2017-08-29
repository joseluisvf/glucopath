package pt.joseluisvf.glucopath.domain.user

import org.scalatest.{Matchers, WordSpec}
import pt.joseluisvf.glucopath.exception.{CarbohydrateMitigationOutsideReasonableBoundsError, DiabeticProfileException, GlucoseMitigationOutsideReasonableBoundsError, GlucoseRangeOutsideReasonableBoundsError}

class DiabeticProfileTest extends WordSpec with Matchers {
  var diabeticProfile: DiabeticProfile = _
  var caught: DiabeticProfileException = _

  "A diabetic profile" should {
    "not be allowed to be created with negative values" in {
      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(-1, (80, 150), 12)
      }

      caught.getGlucopathError should equal(GlucoseMitigationOutsideReasonableBoundsError(-1))

      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(50, (-1, 150), 12)
      }
      caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((-1, 150)))

      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(50, (80, -1), 12)
      }

      caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((80, -1)))

      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(50, (80, 150), -1)
      }

      caught.getGlucopathError should equal(CarbohydrateMitigationOutsideReasonableBoundsError(-1))
    }

    "not be allowed to be created with an invalid range" in {
      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(50, (80, 60), 12)
      }

      caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((80, 60)))

      caught = intercept[DiabeticProfileException] {
        diabeticProfile = DiabeticProfile(50, (200, 150), 12)
      }

      caught.getGlucopathError should equal(GlucoseRangeOutsideReasonableBoundsError((200, 150)))
    }

    "calculateHowMuchInsulinToAdminister" should {
      "work properly given a normal scenario" in {
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 200
        val carbohydratesEaten = 36
        val expected = 5

        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        result should equal(expected)
      }

      "work even if no carbs were consumed" in {
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 200
        val carbohydratesEaten = 0
        val expected = 2

        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        result should equal(expected)
      }

      "take into consideration when ones glucose levels are lower than normal" in {
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 60
        val carbohydratesEaten = 0
        val expected = 0

        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        result should equal(expected)
      }

      "take into consideration when ones glucose levels are lower than normal when carbs are consumed" in {
        diabeticProfile = DiabeticProfile(50, (80, 150), 12)
        val glucoseMeasured = 40
        val carbohydratesEaten = 36
        val expected = 2

        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        result should equal(expected)
      }

      "work properly for very specific ranges" in {
        diabeticProfile = DiabeticProfile(50, (100, 101), 12)
        val glucoseMeasured = 200
        val carbohydratesEaten = 36
        val expected = 5

        val result = diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
        result should equal(expected)
      }
    }
  }

}
