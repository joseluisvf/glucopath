package pt.joseluisvf.glucopath.domain.day

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
        retrievedMeasurement should equal (Some(aMeasurement))
      }

      "Not get an non-existing measurement" in {
        val retrievedMeasurement = day.getMeasurement(UUID.randomUUID())
        retrievedMeasurement should equal (None)
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
  }
}