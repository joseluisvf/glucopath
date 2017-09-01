package pt.joseluisvf.glucopath.domain.day

import java.time.LocalDateTime
import java.util.regex.{Matcher, Pattern}

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.exception.{DayError, DayWithDateDoesNotExistError}
import pt.joseluisvf.glucopath.presentation.util.DisplayOptions
import pt.joseluisvf.glucopath.util.TextUtil

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
          .get should equal(DayWithDateDoesNotExistError(aMeasurementForToday.date.toLocalDate))

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

    "get today" should {
      "find the existing day with todays date" in {
        val today = Day()
        val daysWithToday = Days(scala.collection.mutable.ListBuffer.empty[Day] += today)
        val result = daysWithToday.getToday

        result.get should equal(today)
      }

      "not find today if it doesnt exist" in {
        val result = days.getToday
        result should equal(None)
      }
    }

    "aggregate day statistics" should {
      "be empty if no days are present" in {
        val expected = DayStatistics(0, 0, 0, 0)
        val result = days.aggregateDayStatistics

        result should equal(expected)
      }

      "be equivalent to one day's statistics when only that day is present" in {
        val dayStatistics = DayStatistics(1, 1, 1, 1)
        val day = Day(dayStatistics)
        val daysWithDay = Days(scala.collection.mutable.ListBuffer.empty[Day] += day)
        val result = daysWithDay.aggregateDayStatistics

        result should equal(dayStatistics)
      }

      "calculate the correct results with several days" in {
        val daysWithDay = Days(scala.collection.mutable.ListBuffer.empty[Day] +=
          Day(DayStatistics(1, 1, 1, 1)) +=
          Day(DayStatistics(2, 2, 2, 2)) +=
          Day(DayStatistics(3, 3, 3, 3))
        )

        val expected = DayStatistics(6, 6, 6, 6)
        val result = daysWithDay.aggregateDayStatistics

        result should equal(expected)
      }
    }

    "to string" should {
      "provide output for each day" in {
        val daysWithDay = Days(scala.collection.mutable.ListBuffer.empty[Day] +=
          Day(DayStatistics(1, 1, 1, 1)) +=
          Day(DayStatistics(2, 2, 2, 2)) +=
          Day(DayStatistics(3, 3, 3, 3))
        )

        val result = daysWithDay.toString
        val dayCount = TextUtil.countOcurrencesTextIn(DisplayOptions.getSeparator, result)
        val expectedCount = 2

        dayCount should equal(expectedCount)
      }

      "be empty when no days are present" in {
        val result = days.toString
        val expected = "Days:\n"

        result should equal(expected)
      }
    }

    "alter slow insulin" should {
      "alter the slow insulin of an existing day" in {
        val today = Day()
        val daysWithToday = Days(scala.collection.mutable.ListBuffer.empty[Day] += today)
        val todayDate = LocalDateTime.now.withHour(10).withMinute(20)
        val slowInsulinToAlter = SlowInsulin(todayDate.toLocalTime, 10)
        daysWithToday.alterSlowInsulin(slowInsulinToAlter, todayDate)

        val retrievedToday = daysWithToday.getToday.get
        val result = retrievedToday.slowInsulin

        result should equal(slowInsulinToAlter)
      }

      "alter the slow insulin of a day that does not exist" in {
        val dateToRegister = LocalDateTime.now.withHour(10).withMinute(20)
        val slowInsulinToAlter = SlowInsulin(dateToRegister.toLocalTime, 30)

        days.alterSlowInsulin(slowInsulinToAlter, dateToRegister)

        val retrievedSlowInsulin = days.getDayByDate(dateToRegister.toLocalDate).get.slowInsulin
        retrievedSlowInsulin should equal(slowInsulinToAlter)
      }
    }
  }
}