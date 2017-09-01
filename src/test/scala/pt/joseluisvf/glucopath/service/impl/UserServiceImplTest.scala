package pt.joseluisvf.glucopath.service.impl

import java.io.{File, FileNotFoundException}
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import measurement.{DiabeticProfileProto, MeasurementProto, SlowInsulinProto, UserProto}
import org.scalatest._
import pt.joseluisvf.glucopath.domain.day.{Day, Days, SlowInsulin}
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, WarningLevel}
import pt.joseluisvf.glucopath.domain.user.{DiabeticProfile, User}
import pt.joseluisvf.glucopath.exception._
import pt.joseluisvf.glucopath.service.mapper._
import pt.joseluisvf.glucopath.util.TextUtil

class UserServiceImplTest extends WordSpec with Matchers with BeforeAndAfterEach with GivenWhenThen with PrivateMethodTester {
  val userWithoutDays: User = new User("test", Days(), DiabeticProfile(40, (80, 150), 10))
  val testDate: LocalDate = LocalDate.of(2000, 2, 20)
  val testDateTime: LocalDateTime = LocalDateTime.of(2000, 2, 20, 10, 15)
  val testDay: Day = Day(testDate)
  val userWithDay: User = new User("test", Days(scala.collection.mutable.ListBuffer.empty[Day] += testDay), DiabeticProfile(40, (80, 150), 10))
  val userWithDays: User = new User("test", Days(scala.collection.mutable.ListBuffer.empty[Day] += testDay += testDay += testDay), DiabeticProfile(40, (80, 150), 10))
  var userProto: UserProto = _
  var measurement: Measurement = Measurement(10, LocalDateTime.of(2000, 10, 10, 10, 10), BeforeOrAfterMeal.BEFORE_MEAL, "noodles", 60, 10, "no comments", WarningLevel.GREEN)
  var measurementProto: MeasurementProto = _
  var resultingUser: User = _
  var resultingUserProto: UserProto = _

  override def beforeEach() {
    userProto = UserMapperImpl.toProto(userWithoutDays)
    measurementProto = MeasurementMapperImpl.toProto(measurement)
  }

  "User Service Impl" when {
    "adding a measurement" should {
      "add a valid measurement" in {
        Given("a valid measurement")
        When("it is added")
        val eitherResult = UserServiceImpl.addMeasurement(userProto, measurementProto)
        Then("the contained measurement should equal the one inserted")
        val maybeSomething = eitherResult.right
        val resultingUserProto = maybeSomething.get
        resultingUser = UserMapperImpl.toEntity(resultingUserProto)
        val result: Either[DayError, Option[Measurement]] = resultingUser.getMeasurement(measurement.id, measurement.date.toLocalDate)
        val retrievedMeasurement = result.right.get.get
        retrievedMeasurement should equal(measurement)
      }

      "not add an invalid measurement" that {
        "has invalid glucose" in {
          Given("a measurement with invalid glucose")
          measurementProto = MeasurementProto(-100, LocalDateTime.now.toString, MeasurementProto.BeforeOrAfterMeal.BEFORE_MEAL, "bad noodles", 40, 10, Some("no comments"), MeasurementProto.WarningLevel.GREEN, UUID.randomUUID().toString)
          When("it is added")
          val resultingError = UserServiceImpl.addMeasurement(userProto, measurementProto).left.get
          Then("a glucose-related error should be triggered")
          resultingError should equal(GlucoseOutsideBoundsError(-100))
        }

        "has invalid insulin" in {
          Given("a measurement with invalid insulin")
          measurementProto = MeasurementProto(200, LocalDateTime.now.toString, MeasurementProto.BeforeOrAfterMeal.BEFORE_MEAL, "bad noodles", 40, 1000, Some("no comments"), MeasurementProto.WarningLevel.GREEN, UUID.randomUUID().toString)
          When("it is added")
          val resultingError = UserServiceImpl.addMeasurement(userProto, measurementProto).left.get
          Then("an insulin-related  error should be triggered")
          resultingError should equal(InsulinAdministeredOutsideBoundsError(1000))
        }

        "has invalid carbohydrates" in {
          Given("a measurement with invalid carbohydrates")
          measurementProto = MeasurementProto(200, LocalDateTime.now.toString, MeasurementProto.BeforeOrAfterMeal.BEFORE_MEAL, "bad noodles", 400, 10, Some("no comments"), MeasurementProto.WarningLevel.GREEN, UUID.randomUUID().toString)
          When("it is added")
          val resultingError = UserServiceImpl.addMeasurement(userProto, measurementProto).left.get
          Then("a carbohydrate-related  error should be triggered")
          resultingError should equal(CarbohydratesEatenInGramsOutsideBoundsError(400))
        }
      }
    }

    "getting a day by date" should {
      "get the correct existing day" in {
        Given("a user with a day registered")
        userProto = UserMapperImpl.toProto(userWithDay)
        When("getting a day with its date")
        val maybeDayProto = UserServiceImpl.getDayByDate(userProto, testDate.toString)
        Then("the same day must be retrieved")
        maybeDayProto.get should equal(DayMapperImpl.toProto(testDay))
      }

      "not get a day that doesn't exist" in {
        Given("a user with no days")
        When("getting a day by a certain date")
        val maybeDayProto = UserServiceImpl.getDayByDate(userProto, testDate.toString)
        Then("no date should be retrieved")
        maybeDayProto should equal(None)
      }
    }

    "calculating the insulin to administer" when {
      "the glucose measurement is high" when {
        "no carbohydrates are consumed" in {
          Given("a user with a known diabetic profile")
          When("no carbohydrates are consumed")
          val result = UserServiceImpl.calculateInsulinToAdminister(userProto, 200, 0)
          Then("the insulin to administer should take into account only the current glucose")
          result should equal(3)
        }

        "some carbohydrates are consumed" in {
          Given("a user with a known diabetic profile")
          When("some carbohydrates are consumed")
          val result = UserServiceImpl.calculateInsulinToAdminister(userProto, 200, 40)
          Then("the insulin to administer should take into account both the current glucose and the carbohydrates consumed")
          result should equal(7)
        }
      }

      "the glucose measurement is low" when {
        "no carbohydrates are consumed" in {
          Given("a user with a known diabetic profile")
          When("no carbohydrates are consumed")
          val result = UserServiceImpl.calculateInsulinToAdminister(userProto, 40, 0)
          Then("the insulin to administer should take into account only the current glucose")
          result should equal(0)
        }

        "some carbohydrates are consumed" in {
          Given("a user with a known diabetic profile")
          When("some carbohydrates are consumed")
          val result = UserServiceImpl.calculateInsulinToAdminister(userProto, 40, 40)
          Then("the insulin to administer should take into account both the current glucose and the carbohydrates consumed")
          result should equal(3)
        }
      }
    }

    "getting overall info" should {
      "return quite a lot of information" in {
        Given("a user with three days")
        userProto = UserMapperImpl.toProto(userWithDays)
        When("getting overall info")
        val result = UserServiceImpl.getOverallInfo(userProto)
        Then("the returned information should contain these days")
        val dayCount = TextUtil.countOcurrencesTextIn("Metrics for", result)
        dayCount should equal(3)
      }

      "return no information related to days" in {
        Given("a user with no days")
        userProto = UserMapperImpl.toProto(userWithoutDays)
        When("getting overall info")
        val result = UserServiceImpl.getOverallInfo(userProto)
        Then("the returned information should not contain any days")
        val dayCount = TextUtil.countOcurrencesTextIn("Metrics for", result)
        dayCount should equal(0)
      }
    }

    "exporting measurements" should {
      "write measurements if the path exists" in {
        Given("a valid path")
        val path = "src/test/test_resources/testMeasurements.csv"
        When("measurements are written")
        UserServiceImpl.exportMeasurements(userProto, path)
        Then("the file should exist")
        val file: File = new File(path)
        file.exists should equal(true)
      }

      "not write measurements if the path doesn't exist" in {
        Given("an invalid path")
        val path = "src/test/iDontExist/testMeasurements.csv"
        When("measurements are written")
        Then("the file should not exist")
        assertThrows[FileNotFoundException] {
          UserServiceImpl.exportMeasurements(userProto, path)
        }
      }
    }

    "showing metrics per time period" should {
      "return a lot of data" in {
        Given("a user with days")
        userProto = UserMapperImpl.toProto(userWithDays)
        When("showing metrics")
        val result = UserServiceImpl.showMetricsPerTimePeriod(userProto)
        Then("the returned data should be lengthy")
        result.length should be > 40
      }
    }

    "write metrics per time period" should {
      "write metrics if the path exists" in {
        Given("a valid path")
        val path = "src/test/test_resources/testMetrics.csv"
        When("metrics are written")
        UserServiceImpl.writeMetricsPerTimePeriod(userProto, path)
        Then("the file should exist")
        val file: File = new File(path)
        file.exists should equal(true)
      }

      "not write metrics if the path doesn't exist" in {
        Given("an invalid path")
        val path = "src/test/iDontExist/testMetrics.csv"
        When("metrics are written")
        Then("the file should not exist")
        assertThrows[FileNotFoundException] {
          UserServiceImpl.writeMetricsPerTimePeriod(userProto, path)
        }
      }
    }

    "altering diabetic profile" when {
      "a valid alteration is provided" should {
        "alter the diabetic profile with success" in {
          Given("a valid diabetic profile")
          val newDiabeticProfile = DiabeticProfile(40, (50, 150), 10)
          val newDiabeticProfileProto = DiabeticProfileMapperImpl.toProto(newDiabeticProfile)
          When("the user's diabetic profile is altered")
          val eitherResult: Either[DiabeticProfileError, UserProto] = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)
          Then("the alteration should go through")
          val retrievedUser = UserMapperImpl.toEntity(eitherResult.right.get)
          val retrievedDiabeticProfile = retrievedUser.diabeticProfile

          retrievedDiabeticProfile should equal(newDiabeticProfile)
        }
      }

      "an invalid alteration is provided" when {
        "glucose mitigation is outside bounds" should {
          "trigger an error" in {
            Given("a diabetic profile with invalid glucose mitigation")
            val newDiabeticProfileProto = DiabeticProfileProto(-40, 50, 150, 10)
            When("the user's diabetic profile is altered")
            val eitherResult = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)
            Then("an error related to the glucose mitigation should be triggered")
            eitherResult.left.get should equal(GlucoseMitigationOutsideReasonableBoundsError(-40))
          }
        }

        "ideal glucose range minimum is invalid" should {
          "trigger an error" in {
            Given("a diabetic profile with invalid minimum glucose range")
            val newDiabeticProfileProto = DiabeticProfileProto(40, -40, 150, 10)
            When("the user's diabetic profile is altered")
            val eitherResult = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)
            Then("an error related to the invalid minimum glucose should be triggered")
            eitherResult.left.get should equal(GlucoseRangeOutsideReasonableBoundsError((-40, 150)))
          }
        }

        "ideal glucose range maximum is invalid" should {
          "trigger an error" in {
            Given("a diabetic profile with invalid maximum glucose range")
            val newDiabeticProfileProto = DiabeticProfileProto(40, 40, -150, 10)
            When("the user's diabetic profile is altered")
            val eitherResult = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)
            Then("an error related to the invalid maximum glucose should be triggered")
            eitherResult.left.get should equal(GlucoseRangeOutsideReasonableBoundsError((40, -150)))
          }
        }

        "ideal glucose range is invalid (minimum > maximum)" should {
          "trigger an error" in {
            Given("a diabetic profile with invalid glucose range")
            val newDiabeticProfileProto = DiabeticProfileProto(40, 100, 50, 10)
            When("the user's diabetic profile is altered")
            val eitherResult = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)
            Then("an error related to the invalid glucose range should be triggered")
            eitherResult.left.get should equal(GlucoseRangeOutsideReasonableBoundsError((100, 50)))
          }
        }
      }
    }

    "altering slow insulin" when {
      "a valid replacement is provided" should {
        "alter the slow insulin with success" in {
          Given("a valid slow insulin")
          userProto = UserMapperImpl.toProto(userWithDay)
          val newSlowInsulin = SlowInsulin(testDateTime.toLocalTime, 20)
          val newSlowInsulinProto = SlowInsulinMapperImpl.toProto(newSlowInsulin)
          When("a day's slow insulin is altered")
          val eitherResult = UserServiceImpl.alterSlowInsulin(userProto, newSlowInsulinProto, testDateTime)
          val resultingUserProto: UserProto = eitherResult.right.get
          Then("the day's slow insulin should be altered")
          val retrievedDay = UserMapperImpl.toEntity(resultingUserProto).getDayByDate(testDate).get
          retrievedDay.slowInsulin should equal(newSlowInsulin)
        }
      }

      "an invalid replacement is provided" should {
        "trigger an error" when {
          "the amount of insulin is negative" in {
            Given("a slow insulin with negative amount")
            val invalidSlowInsulinProto = SlowInsulinProto("13:15", -10)
            When("a day's slow insulin is altered")
            val eitherResult = UserServiceImpl.alterSlowInsulin(userProto, invalidSlowInsulinProto, testDateTime)
            Then("an error related to the negative amount should be triggered")
            eitherResult.left.get should equal(AmountOutsideReasonableBoundsError(-10))

          }

         "the amount of insulin is higher than the maximum" in {
           Given("a slow insulin with an amount higer than the maximum")
           val invalidSlowInsulinProto = SlowInsulinProto("13:15", SlowInsulin.MAXIMUM_AMOUNT + 1)
           When("a day's slow insulin is altered")
           val eitherResult = UserServiceImpl.alterSlowInsulin(userProto, invalidSlowInsulinProto, testDateTime)
           Then("an error related to the insulin amount being higher than the maximum should be triggered")
           eitherResult.left.get should equal(AmountOutsideReasonableBoundsError(SlowInsulin.MAXIMUM_AMOUNT + 1))
         }
        }
      }
    }

    "converting a measurement to string" should {
      "work properly" in {
        Given("a measurement")
        val measurement = Measurement(200, BeforeOrAfterMeal.BEFORE_MEAL, "test chicken", 0,2,"no comments")
        When("converting it to a string")
        val privateMethod = PrivateMethod[String]('measurementToString)
        val result = UserServiceImpl invokePrivate privateMethod(measurement)
        Then("the result should be as expected")
        result.length should be > 20
      }
    }
  }
}

