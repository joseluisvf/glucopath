package pt.joseluisvf.glucopath.domain.measurement

import pt.joseluisvf.glucopath.presentation.util.DisplayOptions
import pt.joseluisvf.glucopath.util.TextUtil

class MeasurementsTest extends AbstractMeasurementTest {

  private val aMeasurement = makeMeasurementWithGivenId(DEFAULT_GLUCOSE_1, DEFAULT_DATE_1, DEFAULT_BEFORE_OR_AFTER_MEAL_1, DEFAULT_WHAT_WAS_EATEN_1, DEFAULT_CARBOHYDRATES_EATEN_1, DEFAULT_INSULIN_ADMINISTERED_1, DEFAULT_COMMENTS_1, DEFAULT_WARNING_LEVEL_1, DEFAULT_UUID_1)
  private val anotherMeasurement = makeMeasurementWithGivenId(DEFAULT_GLUCOSE_2, DEFAULT_DATE_2, DEFAULT_BEFORE_OR_AFTER_MEAL_2, DEFAULT_WHAT_WAS_EATEN_2, DEFAULT_CARBOHYDRATES_EATEN_2, DEFAULT_INSULIN_ADMINISTERED_2, DEFAULT_COMMENTS_2, DEFAULT_WARNING_LEVEL_2, DEFAULT_UUID_2)

  "Measurements" should {
    "be created by default with no measurements" in withEmptyMeasurements { measurements =>
      Given("empty measurements")
      When("it is created")
      Then("it should be empty")
      assert(measurements.measurements.isEmpty)
    }

    "have the correct measurement when it was added" in withNonEmptyMeasurements(measurements => {
      Given("measurements with one measurement")
      When("it is created")
      Then("it should contain the expected element")
      measurements.measurements should contain(expectedElement = aMeasurement)
    }, aMeasurement)

    "have the correct measurements when they were added" in withNonEmptyMeasurements(measurements => {
      Given("measurements with two measurements")
      When("it is created")
      Then("it should contain both elements")
      measurements.measurements should contain allOf(aMeasurement, anotherMeasurement)
    }, aMeasurement, anotherMeasurement)

    "have only one measurement when one measurement was added" in withNonEmptyMeasurements(1, measurements => {
      Given("measurements with one measurement")
      When("it is created")
      Then("it should only have one measurement")
      measurements.measurements should have size 1
    })

    "translate to a string" when {
      "it is empty" should {
        "provide not much information" in withEmptyMeasurements { measurements =>
          Given("no measurements")
          When("translating the measurements to a string")
          val result = measurements.toString
          Then("not much information should be returned")
          result.length should equal(0)
        }
      }

      "it has some measurements" should {
        "provide information for each measurement" in withNonEmptyMeasurements(3, measurements => {
          Given("some measurements")
          When("translating the measurements to a string")
          val measurementCount = TextUtil.countOcurrencesTextIn(DisplayOptions.getSmallSeparator, measurements.toString)
          Then("the count of measurements should be as expected")
          measurementCount should equal(2)
        })
      }
    }
  }
}