package pt.joseluisvf.glucopath.domain.day

import java.time.{LocalDateTime, LocalTime}

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.exception.{DayError, DayWithDateDoesNotExistError}
import pt.joseluisvf.glucopath.presentation.util.DisplayOptions
import pt.joseluisvf.glucopath.util.TextUtil

class DaysTest extends AbstractDayTest {
  val today = Day()

  "Days" when {
    "adding a measurement" should {
      "add a measurement where a corresponding day does not exist" in withEmptyDays { days =>
        Given("empty days")
        val numberDaysBefore = days.days.size
        When("adding a measurement")
        days.addMeasurement(aMeasurement)
        days.days.size should equal(numberDaysBefore + 1)

        val eitherRetrievedMeasurement: Either[DayError, Option[Measurement]] =
          days.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        Then("the measurement should be retrieved")
        eitherRetrievedMeasurement.right.get should equal(Some(aMeasurement))

        val maybeRetrievedDay = getDayByDate(days, aMeasurement.date.toLocalDate)
        maybeRetrievedDay shouldBe a[Some[_]]
        val retrievedDay = maybeRetrievedDay.get
        aMeasurement.date.toLocalDate should equal(retrievedDay.date)

        Then("the corresponding day should be created")
        retrievedDay.measurements.measurements should contain(aMeasurement)
      }

      "add a measurement where a corresponding day exists" in withEmptyDays { days =>
        Given("empty days")
        When("two measurements on the same date are added")
        val numberDaysBefore = days.days.size
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(anotherMeasurementOnDateAlpha)
        Then("one new day must have been created")
        days.days.size should equal(numberDaysBefore + 1)
      }

      "group measurements from the same date together" in withEmptyDays { days =>
        Given("empty days")
        When("adding two measurements on the same date")
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(anotherMeasurementOnDateAlpha)
        Then("only one day should have been created containing these measurements")
        val retrievedDay = getDayByDate(days, measurementOnDateAlpha.date.toLocalDate).get
        retrievedDay.measurements.measurements should contain allOf(measurementOnDateAlpha, anotherMeasurementOnDateAlpha)
      }

      "create different days for each different date received" in withEmptyDays { days =>
        Given("empty days")
        val numberDaysBefore = days.days.size
        When("adding two measurements on different dates")
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(measurementOnDateBeta)
        Then("two new days should have been created")
        days.days.size should equal(numberDaysBefore + 2)
      }

      "not group measurements from different dates together" in withEmptyDays { days =>
        Given("empty days")
        When("adding two measurements on different dates")
        days.addMeasurement(measurementOnDateAlpha)
        days.addMeasurement(measurementOnDateBeta)
        Then("these should not reside in the same day")
        val dayAlpha = getDayByDate(days, measurementOnDateAlpha.date.toLocalDate).get
        dayAlpha.measurements.measurements should not contain measurementOnDateBeta
      }
    }

    "getting a measurement" should {
      "throw an error if we want a measurement for a day that does not exist" in withEmptyDays { days =>
        Given("empty days")
        When("getting a measurement for a day that does not exist")
        Then("an error corresponding to a day not existing should result wrapped in an exception")
        days
          .getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
          .left
          .get should equal(DayWithDateDoesNotExistError(aMeasurement.date.toLocalDate))
      }

      "retrieve the correct measurement if it exists" in withEmptyDays { days =>
        Given("empty days")
        When("adding a measurement")
        days.addMeasurement(aMeasurement)
        When("getting this measurement")
        val eitherRetrievedMeasurement: Either[DayError, Option[Measurement]] =
          days.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        Then("the measurement should be retrieved")
        eitherRetrievedMeasurement
          .right
          .get
          .get should equal(aMeasurement)
      }

      "not do anything if we want a measurement that does not exist for a day that exists" in withEmptyDays { days =>
        Given("empty days")
        Given("a measurement is added and then removed")
        days.addMeasurement(aMeasurement)
        days.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        When("getting this measurement")
        val retrievedMeasurements: Either[DayError, Measurements] =
          days.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        Then("the measurement should not exist")
        retrievedMeasurements.right.get.measurements.size should equal(0)
      }
    }

    "getting today measurements" should {
      "return no measurements if none exist" in withEmptyDays { days =>
        Given("empty days")
        When("getting today measurements")
        val todayMeasurements: Option[Measurements] = days.getTodayMeasurements
        Then("they should be empty")
        todayMeasurements shouldBe None
      }

      "return all measurements for today if they exist" in withEmptyDays { days =>
        Given("empty days")
        When("three measurements are added, two of them for today")
        days.addMeasurement(aMeasurementForToday)
        days.addMeasurement(anotherMeasurementForToday)
        days.addMeasurement(measurementOnDateAlpha)
        val todayMeasurements = days.getTodayMeasurements.get
        Then("two measurements should be returned")
        todayMeasurements.measurements.size should equal(2)
      }
    }

    "removing a measurement" should {
      "remove a measurement if it exists" in withEmptyDays { days =>
        Given("empty days")
        Given("a measurement was added")
        days.addMeasurement(aMeasurementForToday)
        val today = getDayByDate(days, aMeasurementForToday.date.toLocalDate).get
        val numberMeasurementsBeforeRemoval = today.measurements.measurements.size
        When("removing it")
        days.removeMeasurement(aMeasurementForToday.id, aMeasurementForToday.date.toLocalDate)
        Then("it should be removed")
        today.measurements.measurements.size should equal(numberMeasurementsBeforeRemoval - 1)
      }
      "not do anything if a measurement does not exist" in withEmptyDays { days =>
        Given("empty days")
        When("removing a measurement that does not exist")
        Then("an error corresponding to the day not existing should be returned wrapped in an exception")
        days
          .removeMeasurement(aMeasurementForToday.id, aMeasurementForToday.date.toLocalDate)
          .left
          .get should equal(DayWithDateDoesNotExistError(aMeasurementForToday.date.toLocalDate))

      }
    }

    "getting all measurements" should {
      "not return anything if no measurements exist" in withEmptyDays { days =>
        Given("empty days")
        When("getting all measurements")
        val numberMeasurements = days.getAllMeasurements.measurements.size
        Then("these should be empty")
        numberMeasurements should equal(0)
      }

      "return all measurements when they are present" in withEmptyDays { days =>
        Given("empty days")
        Given("three measurements are added")
        days.addMeasurement(aMeasurement)
        days.addMeasurement(anotherMeasurement)
        days.addMeasurement(yetAnotherMeasurement)
        When("getting all measurements")
        Then("these should be empty")
        days.getAllMeasurements.measurements.size should equal(3)
      }
    }

    "getting today" should {
      "find the existing day with todays date" in withNonEmptyDays ( days => {
        Given("a day with today")
        When("getting today")
        val result = days.getToday
        Then("today should be returned")
        result.get should equal(today)
      }, today)

      "not find today if it doesnt exist" in withEmptyDays { days =>
        Given("a day without today")
        When("getting today")
        val result = days.getToday
        Then("nothing should be returned")
        result should equal(None)
      }
    }

    "aggregating day statistics" should {
      "be empty if no days are present" in withEmptyDays { days =>
        Given("empty days")
        val expected = DayStatistics(0, 0, 0, 0)
        When("aggregating its statistics")
        val result = days.aggregateDayStatistics
        Then("they should be zero")
        result should equal(expected)
      }

      "be equivalent to one day's statistics when only that day is present" in  withNonEmptyDays( days => {
        Given("empty days")
        Given("a day with non-empty statistics is added")
        val dayStatistics = DayStatistics(1, 1, 1, 1)
        When("aggregating its statistics")
        val result = days.aggregateDayStatistics
        Then("they should be zero")
        result should equal(dayStatistics)
      },Day(DayStatistics(1, 1, 1, 1)))

      "calculate the correct results with several days" in withNonEmptyDays(days => {
        Given("days with several days with non-empty statistics")
        val expected = DayStatistics(6, 6, 6, 6)
        When("aggregating their statistics")
        val result = days.aggregateDayStatistics
        Then("the result should take every day into consideration")
        result should equal(expected)
      }, Day(DayStatistics(1, 1, 1, 1)), Day(DayStatistics(2, 2, 2, 2)), Day(DayStatistics(3, 3, 3, 3)))
    }

    "being translated to a string" should {
      "provide output for each day" in withNonEmptyDays(days => {
        Given("days with several days with non-empty statistics")
        When("translating it to a string")
        val result = days.toString
        val dayCount = TextUtil.countOcurrencesTextIn(DisplayOptions.getSeparator, result)
        val expectedCount = 2
        Then("it should have ocurrences for each day present")
        dayCount should equal(expectedCount)
      }, Day(DayStatistics(1, 1, 1, 1)), Day(DayStatistics(2, 2, 2, 2)), Day(DayStatistics(3, 3, 3, 3)))

      "be empty when no days are present" in withEmptyDays { days =>
        Given("empty days")
        When("translating it to a string")
        val result = days.toString
        val expected = "Days:\n"
        Then("it should be devoid of information")
        result should equal(expected)
      }
    }

    "altering the slow insulin" should {
      "alter the slow insulin of an existing day" in withNonEmptyDays(days => {
        Given("days with today")
        val time = LocalTime.of(10, 20)
        val slowInsulinToAlter = SlowInsulin(time, 10)
        When("altering the slow insulin for todays date")
        days.alterSlowInsulin(slowInsulinToAlter, LocalDateTime.of(today.date, time))
        val retrievedToday = days.getToday.get
        val result = retrievedToday.slowInsulin
        Then("it should be altered")
        result should equal(slowInsulinToAlter)
      }, today)

      "alter the slow insulin of a day that does not exist" in withEmptyDays { days =>
        Given("empty days")
        val dateToRegister = LocalDateTime.now.withHour(10).withMinute(20)
        val slowInsulinToAlter = SlowInsulin(dateToRegister.toLocalTime, 30)
        When("altering the slow insulin")
        days.alterSlowInsulin(slowInsulinToAlter, dateToRegister)
        Then("a day should have been created with this slow insulin")
        val retrievedSlowInsulin = days.getDayByDate(dateToRegister.toLocalDate).get.slowInsulin
        retrievedSlowInsulin should equal(slowInsulinToAlter)
      }
    }
  }
}