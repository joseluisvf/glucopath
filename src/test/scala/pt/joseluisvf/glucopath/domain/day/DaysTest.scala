package pt.joseluisvf.glucopath.domain.day

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.exception.{DayDoesNotExistException, DayError, DayWithDateDoesNotExistError}

class DaysTest extends AbstractDayTest {

  var days = Days()

  override def beforeEach() {
    days = Days()
  }

  "Days" should {
    "Add Measurement" should {
      "add a measurement where a corresponding day does not exist" in {
        val numberDaysBefore = days.days.size
        days.addMeasurement(aMeasurement)
        days.days.size should equal(numberDaysBefore + 1)

        val eitherRetrievedMeasurement: Either[DayError, Option[Measurement]] =
          days.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        eitherRetrievedMeasurement.right.get should equal(Some(aMeasurement))

        val maybeRetrievedDay = getDayByDate(days, aMeasurement.date.toLocalDate)
        maybeRetrievedDay shouldBe a[Some[_]]
        val retrievedDay = maybeRetrievedDay.get
        aMeasurement.date.toLocalDate should equal(retrievedDay.date)
        retrievedDay.measurements.measurements should contain(aMeasurement)
      }

      "add a measurement where a corresponding day exists" in {
        val numberDaysBefore = days.days.size

        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(anotherMeasurementOnDateAlpha)

        days.days.size should equal(numberDaysBefore + 1)
      }

      "group measurements from the same date together" in {
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(anotherMeasurementOnDateAlpha)

        val retrievedDay = getDayByDate(days, measurementOnDateAlpha.date.toLocalDate).get
        retrievedDay.measurements.measurements should contain allOf(measurementOnDateAlpha, anotherMeasurementOnDateAlpha)
      }

      "create different days for each different date received" in {
        val numberDaysBefore = days.days.size

        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(measurementOnDateBeta)

        days.days.size should equal(numberDaysBefore + 2)
      }

      "not group measurements from different dates together" in {
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(measurementOnDateBeta)

        val dayAlpha = getDayByDate(days, measurementOnDateAlpha.date.toLocalDate).get
        dayAlpha.measurements.measurements should not contain measurementOnDateBeta
      }
    }

    "get measurement" should {
      "throw an error if we want a measurement for a day that does not exist" in {

        days
          .getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
          .left
          .get should equal(DayWithDateDoesNotExistError(aMeasurement.date.toLocalDate))
      }

      "retrieve the correct measurement if it exists" in {
        days.addMeasurement(aMeasurement)
        val eitherRetrievedMeasurement: Either[DayError, Option[Measurement]] =
          days.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        eitherRetrievedMeasurement
          .right
          .get
          .get should equal(aMeasurement)
      }

      "not do anything if we want a measurement that does not exist for a day that exists" in {
        days.addMeasurement(aMeasurement)
        days.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        val retrievedMeasurements: Either[DayError, Measurements] =
          days.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        retrievedMeasurements.right.get.measurements.size should equal(0)
      }
    }

    "get today measurements" should {
      "return no measurements if none exist" in {
        val todayMeasurements: Option[Measurements] = days.getTodayMeasurements
        todayMeasurements shouldBe None
      }

      "return all measurements for today if they exist" in {
        days.addMeasurement(aMeasurementForToday)
        days.addMeasurement(anotherMeasurementForToday)
        days.addMeasurement(measurementOnDateAlpha)
        val todayMeasurements = days.getTodayMeasurements.get
        todayMeasurements.measurements.size should equal(2)
      }
    }

    "remove measurement" should {
      "remove a measurement if it exists" in {
        days.addMeasurement(aMeasurementForToday)
        val today = getDayByDate(days, aMeasurementForToday.date.toLocalDate).get
        val numberMeasurementsBeforeRemoval = today.measurements.measurements.size

        days.removeMeasurement(aMeasurementForToday.id, aMeasurementForToday.date.toLocalDate)

        today.measurements.measurements.size should equal(numberMeasurementsBeforeRemoval - 1)
      }
      "not do anything if a measurement does not exist" in {

        days
          .removeMeasurement(aMeasurementForToday.id, aMeasurementForToday.date.toLocalDate)
        .left
        .get should equal (DayWithDateDoesNotExistError(aMeasurementForToday.date.toLocalDate))

      }
    }

    "get all measurements" should {
      "not return anything if no measurements exist" in {
        val numberMeasurements = days.getAllMeasurements.measurements.size
        numberMeasurements should equal(0)
      }

      "return all measurements when they are present" in {
        days.addMeasurement(aMeasurement)
        days.addMeasurement(anotherMeasurement)
        days.addMeasurement(yetAnotherMeasurement)
        days.getAllMeasurements.measurements.size should equal(3)
      }
    }
  }
}

// get all measurements