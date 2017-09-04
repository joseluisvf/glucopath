package pt.joseluisvf.glucopath.domain.user

import java.time.{LocalDate, LocalDateTime}

import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, Matchers, WordSpec}
import pt.joseluisvf.glucopath.domain.day.{Day, Days}
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, WarningLevel}
import pt.joseluisvf.glucopath.exception.{DayError, DayWithDateDoesNotExistError, DiabeticProfileNotDefinedError, UserException}

class UserTest extends WordSpec with Matchers with BeforeAndAfterEach with GivenWhenThen {
  var user: User = _
  val today: Day = Day(LocalDate.now)
  val userWithToday = new User("test user", Days(scala.collection.mutable.ListBuffer.empty[Day] += today), DiabeticProfile(50, (80, 150), 12))
  val aMeasurement = Measurement(10, BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, 1, "no comments")
  val anotherMeasurement = Measurement(1, BeforeOrAfterMeal.BEFORE_MEAL, "orange", 1, 2, "no comments")
  val aMeasurementForToday = Measurement(10, LocalDateTime.now(), BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, 1, "no comments", WarningLevel.YELLOW)
  val anotherMeasurementForToday = Measurement(100, LocalDateTime.now(), BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, 1, "no comments", WarningLevel.YELLOW)

  override def beforeEach() {
    user = new User("joseluisvf", Days(), DiabeticProfile(50, (80, 150), 12))
  }


  "a User" when {
    "it is created with a null diabetic profile" should {
      "not be created" in {
        Given("a null diabetic profile")
        val diabeticProfile = null
        When("a user is created with it")
        Then("an exception related to the diabetic profile being null should be thrown")
        val interceptedException = intercept[UserException] {
          new User("test user", Days(scala.collection.mutable.ListBuffer.empty[Day] += today), diabeticProfile)
        }
        interceptedException.getGlucopathError should equal(DiabeticProfileNotDefinedError())
      }
    }

    "adding a measurement" should {
      "add a measurement if it is valid" in {
        Given("a measurement")
        When("it is added")
        user.addMeasurement(aMeasurement)
        Then("retrieving it should return the measurement that was added")
        val eitherRetrievedMeasurement: Either[DayError, Option[Measurement]] =
          user.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        eitherRetrievedMeasurement
          .right
          .get
          .get should equal(aMeasurement)
      }
    }

    "getting measurement" should {
      "not do anything if the measurement does not exist" in {
        Given("the added measurement was removed")
        user.addMeasurement(aMeasurement)
        user.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        When("getting it")
        val eitherMeasurement: Either[DayError, Option[Measurement]] = user.getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        Then("nothing should be retrieved")
        eitherMeasurement.right.get should equal(None)
      }

      "throw an exception if we want a measurement for a day that does not exist" in {
        Given("no day exists for the desired date")
        When("getting it")
        Then("an error related to this should be returned")
        user
          .getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
          .left
          .get should equal(DayWithDateDoesNotExistError(aMeasurement.date.toLocalDate))
      }

      "retrieve the correct measurement if it exists" in {
        Given("a day exists for the desired date")
        user.addMeasurement(aMeasurement)
        When("getting it")
        Then("the retrieved measurement should equal the one added")
        user
          .getMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
          .right
          .get
          .get should equal(aMeasurement)
      }
    }

    "removing measurements" should {
      "remove a measurement if it exists" in {
        Given("a measurement was added")
        user.addMeasurement(aMeasurement)
        val numberMeasurementsBeforeRemoval = user.getAllMeasurements.measurements.size
        When("it is removed")
        user.removeMeasurement(aMeasurement.id, aMeasurement.date.toLocalDate)
        Then("it should not exist")
        user.getAllMeasurements.measurements.size should equal(numberMeasurementsBeforeRemoval - 1)
      }

      "not do anything if the measurement does not exist" in {
        Given("a measurement exists")
        user.addMeasurement(aMeasurement)
        val numberMeasurementsBeforeRemoval = user.getAllMeasurements.measurements.size
        When("we try to remove one that doesn't exist")
        user.removeMeasurement(anotherMeasurement.id, anotherMeasurement.date.toLocalDate)
        Then("nothing should change")
        user.getAllMeasurements.measurements.size should equal(numberMeasurementsBeforeRemoval)
      }
    }

    "getting all measurements" should {
      "return empty if no measurements exist" in {
        Given("a user without measurements")
        When("we get them all")
        val allMeasurements = user.getAllMeasurements
        Then("nothing should be retrieved")
        allMeasurements.measurements.size should equal(0)
      }

      "return all measurements if they exist" in {
        Given("a user with some measurements")
        user.addMeasurement(aMeasurement)
        user.addMeasurement(anotherMeasurement)
        user.addMeasurement(aMeasurement)
        When("we get them all")
        val allMeasurements = user.getAllMeasurements.measurements
        Then("we should retrieve them all")
        allMeasurements.size should equal(3)
      }
    }

    "getting today measurements" should {
      "return empty if no measurements exist for today" in {
        Given("a user without measurements for today")
        When("we get them all")
        Then("none should be retrieved")
        user.getTodayMeasurements shouldBe None
      }

      "return all measurements for today if they exist" in {
        Given("a user with measurements for today")
        user.addMeasurement(aMeasurementForToday)
        user.addMeasurement(anotherMeasurementForToday)
        When("we get them all")
        val todayMeasurements = user.getTodayMeasurements.get
        Then("we should retrieve them all")
        todayMeasurements.measurements.size should equal(2)
        todayMeasurements.measurements should contain allOf(aMeasurementForToday, anotherMeasurementForToday)
      }
    }

    "getting today" should {
      "succeed given today exists" in {
        Given("a user with today")
        When("getting today")
        val maybeDay = userWithToday.getToday
        Then("today must be retrieved")
        maybeDay.get should equal(today)
      }

      "not return today if it doesnt exist" in {
        Given("a user without today")
        When("getting today")
        val maybeDay = user.getToday
        Then("nothing should be retrieved")
        maybeDay should equal(None)
      }
    }
  }
}
