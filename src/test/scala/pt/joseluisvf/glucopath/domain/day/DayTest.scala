package pt.joseluisvf.glucopath.domain.day

import java.time.LocalTime
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}

import scala.collection.mutable.ListBuffer

class DayTest extends AbstractDayTest {
  var day = Day()

  override def beforeEach() {
    day = Day()
    day.addMeasurement(aMeasurement)
  }

  "A Day" should {
    "slow insulin" should {
      "be initialized with the default slow insulin" in {
        day.slowInsulin.isDefault should equal(true)
      }

      "be able to be modified" in {
        val slowInsulin: SlowInsulin = SlowInsulin(LocalTime.of(14, 15), 32)
        day.slowInsulin_(slowInsulin).slowInsulin should equal(slowInsulin)
      }
    }


    "Add Measurement" should {
      "Add a measurement" in {
        val sizeBefore = day.measurements.measurements.size
        day.addMeasurement(anotherMeasurement)
        val retrievedAnotherMeasurement = day.getMeasurement(anotherMeasurement.id).get
        day.measurements.measurements.size should equal(sizeBefore + 1)
        retrievedAnotherMeasurement should equal(anotherMeasurement)
      }

      "Add more than one measurement in succession" in {
        val sizeBefore = day.measurements.measurements.size
        day.addMeasurement(anotherMeasurement)
        day.addMeasurement(yetAnotherMeasurement)
        day.measurements.measurements.size should equal(sizeBefore + 2)
        day.measurements.measurements should contain allOf(aMeasurement, anotherMeasurement, yetAnotherMeasurement)
      }

      "Add several simultaneous measurements" in {
        val sizeBefore = day.measurements.measurements.size
        val toAdd = Measurements(ListBuffer.empty[Measurement] :+ anotherMeasurement :+ yetAnotherMeasurement)
        day.addMeasurements(toAdd)
        day.measurements.measurements.size should equal(sizeBefore + 2)
        day.measurements.measurements should contain allOf(aMeasurement, anotherMeasurement, yetAnotherMeasurement)
      }
    }

    "Get Measurement" should {
      "Get an existing measurement" in {
        val retrievedMeasurement = day.getMeasurement(aMeasurement.id)
        retrievedMeasurement should equal(Some(aMeasurement))
      }

      "Not get an non-existing measurement" in {
        val retrievedMeasurement = day.getMeasurement(UUID.randomUUID())
        retrievedMeasurement should equal(None)
      }
    }

    "Glucose average" should {
      "Provide the correct glucose average with only one measurement" in {
        val expectedAverage = aMeasurement.glucose
        day.getGlucoseAverage should equal(expectedAverage)
      }

      "Provide the correct glucose average with only one measurement when another measurement is added" in {
        day.addMeasurement(anotherMeasurement)
        val expectedAverage = (aMeasurement.glucose + anotherMeasurement.glucose).asInstanceOf[Float] / 2
        day.getGlucoseAverage should equal(expectedAverage)
      }

      "Provide the correct glucose average when one element is removed" in {
        day.addMeasurement(anotherMeasurement)
        day.removeMeasurement(anotherMeasurement.id)
        val expectedAverage = aMeasurement.glucose
        day.getGlucoseAverage should equal(expectedAverage)
      }

      "Provide the correct glucose average when one element is removed and then added" in {
        day.addMeasurement(anotherMeasurement)
        day.removeMeasurement(anotherMeasurement.id)
        day.addMeasurement(anotherMeasurement)
        val expectedAverage = (aMeasurement.glucose + anotherMeasurement.glucose).asInstanceOf[Float] / 2
        day.getGlucoseAverage should equal(expectedAverage)
      }
    }

    "Remove measurement" should {
      "Remove an element that exists" in {
        day.removeMeasurement(aMeasurement.id)
        day.measurements.measurements should not contain aMeasurement
      }

      "Not remove an element that does not exist" in {
        val sizeBefore = day.measurements.measurements.size
        day.removeMeasurement(UUID.randomUUID())
        day.measurements.measurements.size should equal(sizeBefore)
      }
    }

    "to string" should {
      "show everything if the non default slow insulin is used" in {
        val dayWithSlowInsulin: Day = day.slowInsulin_(SlowInsulin(LocalTime.of(14, 15), 20))
        val result = dayWithSlowInsulin.toString

        result.contains("SlowInsulin") should equal(true)
      }

      "show everything except for slow insulin if the default slow insulin is used" in {
        val result = day.toString

        result.contains("SlowInsulin") should equal(false)
      }
    }

    "Day Statistics" should {

      "+" should {
        "work correctly with another day statistics" in {
          val dayStatistics: DayStatistics = DayStatistics(10, 10, 100, 100)
          val toAdd: DayStatistics = DayStatistics(1, 1, 10, 10)
          val result = dayStatistics + toAdd
          val expected = DayStatistics(10 + 1, 10 + 1, 100 + 10, 100 + 10)
          result should equal(expected)
        }

        "work correctly with empty day statistics" in {
          val dayStatistics: DayStatistics = DayStatistics(10, 10, 100, 100)
          val toAdd: DayStatistics = DayStatistics(0,0,0,0)
          val result = dayStatistics + toAdd

          result should equal(dayStatistics)
        }
      }


    }
  }
}