package pt.joseluisvf.glucopath.domain.measurement

import java.time.LocalDateTime

import pt.joseluisvf.glucopath.exception._
import pt.joseluisvf.glucopath.presentation.util.DisplayOptions
import pt.joseluisvf.glucopath.util.TextUtil

class MeasurementTest extends AbstractMeasurementTest {

  "A Measurement" should {
    "be created with default id" in withDefaultMeasurement{ measurement =>
      Given("a default measurement")
      When("it is created")
      Then("its id must not be null")
      assert(measurement.id !== null)
    }

    "be created with default warning level" in {
      Given("a default measurement without specifying the warning level")
      When("it is created")
      val measurement = Measurement(DEFAULT_GLUCOSE, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_CARBOHYDRATES_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      Then("its warning level must be the default")
      assert(measurement.warningLevel === Measurement.defaultWarningLevel)
    }

    "be created with default future date" in {
      Given("a default measurement without specifying the date")
      When("it is created")
      val measurement = Measurement(DEFAULT_GLUCOSE, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_CARBOHYDRATES_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      val timeNow = LocalDateTime.now()
      Then("its date must be the same as the present date")
      assert(measurement.date.isBefore(timeNow) || measurement.date.isEqual(timeNow))
    }

    "not be allowed to have a negative glucose reading" in {
      Given("a default measurement with a negative default reading")
      caught = intercept[MeasurementException] {
      When("it is created")
        Measurement(-1, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_CARBOHYDRATES_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      }
      Then("an error corresponding to the negative glucose should be returned inside the wrapping exception")
      caught.getGlucopathError should equal(GlucoseOutsideBoundsError(-1))
    }

    "not be allowed to have a negative insulin administration" in {
      Given("a default measurement with a negative insulin administration")
      caught = intercept[MeasurementException] {
      When("it is created")
        Measurement(DEFAULT_GLUCOSE, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_CARBOHYDRATES_EATEN, -1, DEFAULT_COMMENTS)
      }
      Then("an error corresponding to the negative insulin administration should be returned inside the wrapping exception")
      caught.getGlucopathError should equal(InsulinAdministeredOutsideBoundsError(-1))
    }

    "not be allowed to have a negative carbohydrate consumption" in {
      Given("a default measurement with a negative carbohydrate consumption")
      caught = intercept[MeasurementException] {
      When("it is created")
        Measurement(DEFAULT_GLUCOSE, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, -1, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      }
      Then("an error corresponding to the negative carbohydrate consumption should be returned inside the wrapping exception")
      caught.getGlucopathError should equal(CarbohydratesEatenInGramsOutsideBoundsError(-1))
    }

    "translate to a string" when {
      "default values are used" should {
        "provide some information" in withDefaultMeasurement { measurement =>
          Given("a default measurement")
          When("translating it to a string")
          val result = measurement.toString
          Then("some information should be returned")
          result.length should be > 20
        }
      }
    }
  }
}
