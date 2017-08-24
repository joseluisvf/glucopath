package pt.joseluisvf.glucopath.domain.measurement

class MeasurementsTest extends AbstractMeasurementTest {

  private val aMeasurement = makeMeasurementWithGivenId(DEFAULT_GLUCOSE_1, DEFAULT_DATE_1, DEFAULT_BEFORE_OR_AFTER_MEAL_1, DEFAULT_WHAT_WAS_EATEN_1, DEFAULT_CARBOHYDRATES_EATEN_1, DEFAULT_INSULIN_ADMINISTERED_1, DEFAULT_COMMENTS_1, DEFAULT_WARNING_LEVEL_1, DEFAULT_UUID_1)
  private val anotherMeasurement = makeMeasurementWithGivenId(DEFAULT_GLUCOSE_2, DEFAULT_DATE_2, DEFAULT_BEFORE_OR_AFTER_MEAL_2, DEFAULT_WHAT_WAS_EATEN_2,DEFAULT_CARBOHYDRATES_EATEN_2, DEFAULT_INSULIN_ADMINISTERED_2, DEFAULT_COMMENTS_2, DEFAULT_WARNING_LEVEL_2, DEFAULT_UUID_2)

  "Measurements" should {
    "be created by default with no measurements" in withEmptyMeasurements { measurements =>
      assert(measurements.measurements.isEmpty)
    }

    "have the correct measurement when it was added" in withNonEmptyMeasurements(measurements => {
      measurements.measurements should contain(expectedElement = aMeasurement)
    }, aMeasurement)

    "have the correct measurements when they were added" in withNonEmptyMeasurements(measurements => {
      measurements.measurements should contain allOf(aMeasurement, anotherMeasurement)
    }, aMeasurement, anotherMeasurement)

    "have only one measurement when one measurement was added" in withNonEmptyMeasurements(1, measurements => {
      measurements.measurements should have size 1
    })
  }
}
