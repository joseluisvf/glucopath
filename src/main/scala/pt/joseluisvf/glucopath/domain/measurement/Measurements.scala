package pt.joseluisvf.glucopath.domain.measurement

import java.util.UUID

import pt.joseluisvf.glucopath.presentation.util.DisplayOptions

import scala.collection.mutable.ListBuffer

case class Measurements(var measurements: ListBuffer[Measurement] = ListBuffer.empty) {

  def addMeasurement(toAdd: Measurement): Measurements = {
    measurements += toAdd
    measurements = measurements.sortWith((x, y) => sortMeasurements(x, y))
    this
  }

  private def sortMeasurements(x: Measurement, y: Measurement) = {
    x.date.compareTo(y.date) < 0
  }

  def addMeasurements(toAdd: Measurements): Measurements = {
    measurements ++= toAdd.measurements
    this
  }

  def getMeasurement(id: UUID): Option[Measurement] = measurements.find(_.id == id)

  def removeMeasurement(id: UUID): Option[Measurements] = {
    val sizeBefore = measurements.size
    measurements --= measurements.filter(_.id == id)
    if (sizeBefore != measurements.size) Some(this) else None
  }

  override def toString: String = {
    measurements.map(_.toString()).mkString(s"\n${DisplayOptions.getSmallSeparator}\n")
  }
}
