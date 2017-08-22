package pt.joseluisvf.glucopath.domain.user

import java.time.LocalDateTime

import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import pt.joseluisvf.glucopath.domain.day.Days
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, WarningLevel}
import pt.joseluisvf.glucopath.exception.DayDoesNotExistException

class UserTest extends WordSpec with Matchers with BeforeAndAfterEach {
  var user: User = null
  val aMeasurement = Measurement(10, BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, "no comments")
  val anotherMeasurement = Measurement(1, BeforeOrAfterMeal.BEFORE_MEAL, "orange", 2, "no comments")
  val aMeasurementForToday = Measurement(10, LocalDateTime.now(), BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, "no comments", WarningLevel.YELLOW)
  val anotherMeasurementForToday = Measurement(100, LocalDateTime.now(), BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, "no comments", WarningLevel.YELLOW)

  override def beforeEach() {
    user = new User("joseluisvf", Days(), DiabeticProfile())
  }


  "a User" should {
    "add measurement" should {
      "add a measurement if it is valid" in {
        user.addMeasurement(aMeasurement)
        val retrievedMeasurement = user.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate).get
        retrievedMeasurement should equal(aMeasurement)
      }
    }

    "get measurement" should {
      "not do anything if the measurement does not exist" in {
        user.addMeasurement(aMeasurement)
        user.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        user.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
      }

      "throw an exception if we want a measurement for a day that does not exist" in {
        assertThrows[DayDoesNotExistException] {
          user.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        }
      }

      "retrieve the correct measurement if it exists" in {
        user.addMeasurement(aMeasurement)
        val retrievedMeasurement = user.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate).get
        retrievedMeasurement should equal(aMeasurement)
      }
    }

    "remove measurements" should {
      "remove a measurement if it exists" in {
        user.addMeasurement(aMeasurement)
        val numberMeasurementsBeforeRemoval = user.getAllMeasurements.measurements.size
        user.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        user.getAllMeasurements.measurements.size should equal(numberMeasurementsBeforeRemoval - 1)
      }

      "not do anything if the measurement does not exist" in {
        user.addMeasurement(aMeasurement)
        val numberMeasurementsBeforeRemoval = user.getAllMeasurements.measurements.size
        user.removeMeasurement(anotherMeasurement.id, anotherMeasurement.date.toLocalDate)
        user.getAllMeasurements.measurements.size should equal(numberMeasurementsBeforeRemoval)
      }
    }

    "get all measurements" should {
      "return empty if no measurements exist" in {
        val allMeasurements = user.getAllMeasurements
        allMeasurements.measurements.size should equal(0)
      }

      "return all measurements if they exist" in {
        user.addMeasurement(aMeasurement)
        user.addMeasurement(anotherMeasurement)
        user.addMeasurement(aMeasurement)
        user.getAllMeasurements.measurements.size should equal(3)
      }
    }

    "get today measurements" should {
      "return empty if no measurements exist for today" in {
        user.getTodayMeasurements shouldBe None
      }
      "return all measurements for today if they exist" in {
        user.addMeasurement(aMeasurementForToday)
        user.addMeasurement(anotherMeasurementForToday)
        val todayMeasurements = user.getTodayMeasurements.get
        todayMeasurements.measurements.size should equal(2)
        todayMeasurements.measurements should contain allOf(aMeasurementForToday, anotherMeasurementForToday)
      }
    }


  }
}
