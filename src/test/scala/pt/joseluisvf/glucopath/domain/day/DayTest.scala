package pt.joseluisvf.glucopath.domain.day

import java.time.LocalTime
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}

import scala.collection.mutable.ListBuffer

class DayTest extends AbstractDayTest {
  var day = Day()

  "A Day" when {
    "being created" should {
      "be initialized with the default slow insulin" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        When("it is created")
        Then("the slow insulin should be the default")
        day.slowInsulin.isDefault should equal(true)
      }, aMeasurement)
    }

    "modifying the slow insulin" should {
      "be able to be modified" in withDayWithOneMeasurement ( day => {
        Given("a default day and a well-formed slow insulin")
        When("altering the slow insulin")
        val slowInsulin: SlowInsulin = SlowInsulin(LocalTime.of(14, 15), 32)
        Then("the altered slow insulin should be the one passed")
        day.slowInsulin_(slowInsulin).slowInsulin should equal(slowInsulin)
      }, aMeasurement)
    }

    "adding a measurement" should {
      "work properly given a well-formed measurement" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        val sizeBefore = day.measurements.measurements.size
        When("adding a measurement")
        day.addMeasurement(anotherMeasurement)
        Then("the added measurement should be in the day")
        val retrievedAnotherMeasurement = day.getMeasurement(anotherMeasurement.id).get
        day.measurements.measurements.size should equal(sizeBefore + 1)
        retrievedAnotherMeasurement should equal(anotherMeasurement)
      }, aMeasurement)

      "add more than one measurement in succession" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        val sizeBefore = day.measurements.measurements.size
        When("adding more than one measurement in succession")
        day.addMeasurement(anotherMeasurement)
        day.addMeasurement(yetAnotherMeasurement)
        Then("they should be in the day")
        day.measurements.measurements.size should equal(sizeBefore + 2)
        day.measurements.measurements should contain allOf(aMeasurement, anotherMeasurement, yetAnotherMeasurement)
      }, aMeasurement)

      "add several measurements simultaneously" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        val sizeBefore = day.measurements.measurements.size
        val toAdd = Measurements(ListBuffer.empty[Measurement] :+ anotherMeasurement :+ yetAnotherMeasurement)
        When("several measurements are added simultaneously")
        day.addMeasurements(toAdd)
        Then("all of them should be in the day")
        day.measurements.measurements.size should equal(sizeBefore + 2)
        day.measurements.measurements should contain allOf(aMeasurement, anotherMeasurement, yetAnotherMeasurement)
      }, aMeasurement)
    }

    "getting a measurement" should {
      "get an existing measurement" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        When("retrieving a measurement that exists in it")
        val retrievedMeasurement = day.getMeasurement(aMeasurement.id)
        Then("the retrieved measurement should be the one we expect")
        retrievedMeasurement should equal(Some(aMeasurement))
      }, aMeasurement)

      "not get a measurement that doesn't exist" in withDayWithOneMeasurement ( day => {
        Given("a default day")
        When("retrieving a measurement that doesn't exists in it")
        val retrievedMeasurement = day.getMeasurement(UUID.randomUUID())
        Then("no measurement should be returned")
        retrievedMeasurement should equal(None)
      }, aMeasurement)
    }

    "calculating the glucose average" should {
      "provide the correct glucose average with only one measurement" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement")
        When("calculating the glucose average")
        val expectedAverage = aMeasurement.glucose
        Then("the average should correspond to only that measurement")
        val result = day.getGlucoseAverage
        result should equal(expectedAverage)
      }, aMeasurement)

      "provide the correct glucose average with only one measurement when another measurement is added" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement")
        When("adding another measurement")
        day.addMeasurement(anotherMeasurement)
        Then("the average should correspond to both measurements")
        val expectedAverage = (aMeasurement.glucose + anotherMeasurement.glucose).asInstanceOf[Float] / 2
        day.getGlucoseAverage should equal(expectedAverage)
      }, aMeasurement)

      "provide the correct glucose average when one element is removed" in withDayWithMoreThanOneMeasurement ( day => {
        Given("a default day with two measurements")
        When("removing a measurement")
        day.removeMeasurement(anotherMeasurement.id)
        Then("the average should correspond to only the other measurement")
        val expectedAverage = aMeasurement.glucose
        day.getGlucoseAverage should equal(expectedAverage)
      }, aMeasurement, anotherMeasurement)

      "provide the correct glucose average when one element is removed and then added" in withDayWithMoreThanOneMeasurement ( day => {
        Given("a default day with two measurements")
        When("removing and adding another measurement")
        day.removeMeasurement(anotherMeasurement.id)
        day.addMeasurement(anotherMeasurement)
        Then("the average should correspond to both measurements")
        val expectedAverage = (aMeasurement.glucose + anotherMeasurement.glucose).asInstanceOf[Float] / 2
        day.getGlucoseAverage should equal(expectedAverage)
      }, aMeasurement, anotherMeasurement)
    }

    "removing a measurement" should {
      "remove an element that exists" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement")
        When("that measurement is removed")
        day.removeMeasurement(aMeasurement.id)
        Then("it should be removed")
        day.measurements.measurements should not contain aMeasurement
      }, aMeasurement)

      "not remove an element that does not exist" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement")
        When("attempting to remove a measurement that doesn't exist")
        val sizeBefore = day.measurements.measurements.size
        day.removeMeasurement(UUID.randomUUID())
        Then("it should not be removed")
        day.measurements.measurements.size should equal(sizeBefore)
      }, aMeasurement)
    }

    "being translated to a string" should {
      "show everything if the non default slow insulin is used" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement and non default slow insulin")
        val dayWithSlowInsulin: Day = day.slowInsulin_(SlowInsulin(LocalTime.of(14, 15), 20))
        When("translating it to a string")
        val result = dayWithSlowInsulin.toString
        Then("the result should contain information about the slow insulin")
        result.contains("SlowInsulin") should equal(true)
      }, aMeasurement)

      "show everything except for slow insulin if the default slow insulin is used" in withDayWithOneMeasurement ( day => {
        Given("a default day with one measurement and the default slow insulin")
        When("translating it to a string")
        Then("the result should not contain information about the slow insulin")
        day.toString.contains("SlowInsulin") should equal(false)
      }, aMeasurement)
    }

    "altering its day statistics" when {
      "+ is invoked" should {
        "work correctly with another day statistics" in {
          Given("two day statistics")
          val dayStatistics: DayStatistics = DayStatistics(10, 10, 100, 100)
          val toAdd: DayStatistics = DayStatistics(1, 1, 10, 10)
          When("adding them")
          val result = dayStatistics + toAdd
          val expected = DayStatistics(10 + 1, 10 + 1, 100 + 10, 100 + 10)
          Then("the result should be equivalent to their combined values")
          result should equal(expected)
        }

        "work correctly with empty day statistics" in {
          Given("two day statistics, one of them empty")
          val dayStatistics: DayStatistics = DayStatistics(10, 10, 100, 100)
          When("adding them")
          val toAdd: DayStatistics = DayStatistics(0,0,0,0)
          val result = dayStatistics + toAdd
          Then("the result should be equivalent to the non empty one")
          result should equal(dayStatistics)
        }
      }
    }
  }
}