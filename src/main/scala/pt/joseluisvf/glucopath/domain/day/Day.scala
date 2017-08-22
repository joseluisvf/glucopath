package pt.joseluisvf.glucopath.domain.day

import java.time.LocalDate
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}

case class Day(dayStatistics: DayStatistics, measurements: Measurements = Measurements(), date: LocalDate = LocalDate.now(), id: UUID = java.util.UUID.randomUUID()) {
  def addMeasurement(toAdd: Measurement): Measurements = {
    dayStatistics.addMeasurement(toAdd)
    measurements.addMeasurement(toAdd)
  }

  def addMeasurements(toAdd: Measurements): Measurements = {
    for (m <- measurements.measurements) {
      dayStatistics.addMeasurement(m)
    }

    measurements.addMeasurements(toAdd)
  }

  def getMeasurement(id: UUID): Option[Measurement] = measurements.getMeasurement(id)

  def removeMeasurement(id: UUID): Measurements = {
    val toRemove = getMeasurement(id)
    measurements.removeMeasurement(id) match {
      case Some(_) => dayStatistics.removeMeasurement(toRemove.get)
      case _ => // nothing to do.
    }
    measurements
  }

  def getGlucoseAverage: Float = dayStatistics.glucoseAverage
}

object Day {
  def apply() = new Day(DayStatistics())
  def apply(date: LocalDate) = new Day(DayStatistics(), date = date)
}