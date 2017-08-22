package pt.joseluisvf.glucopath.domain.measurement

import java.time.LocalDateTime

class MeasurementTest extends AbstractMeasurementTest {

  "A Measurement" should {
    "be created with default id" in {
      val measurement = makeDefaultMeasurement()
      assert(measurement.id !== null)
    }

    "be created with default warning level" in {
      val measurement = Measurement(DEFAULT_GLUCOSE, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      assert(measurement.warningLevel === WarningLevel.defaultWarningLevel)
    }

    "be created with default future date" in {
      val measurement = Measurement(DEFAULT_GLUCOSE, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      val timeNow = LocalDateTime.now()
      assert(measurement.date.isBefore(timeNow) || measurement.date.isEqual(timeNow))
    }

    "not be allowed to have a negative glucose reading" in {
      assertThrows[IllegalArgumentException] {
        Measurement(-1, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, DEFAULT_INSULIN_ADMINISTERED, DEFAULT_COMMENTS)
      }
    }

    "not be allowed to have a negative insuline administration" in {
      assertThrows[IllegalArgumentException] {
        Measurement(DEFAULT_GLUCOSE, DEFAULT_DATE_TIME, DEFAULT_BEFORE_OR_AFTER_MEAL, DEFAULT_WHAT_WAS_EATEN, -1, DEFAULT_COMMENTS)
      }
    }
  }


}
