package pt.joseluisvf.glucopath.domain.day

import pt.joseluisvf.glucopath.domain.measurement.Measurement

case class DayStatistics(var numberOfMeasurements: Int, var insulinTotal: Int, var glucoseTotal: Int) {
  def addMeasurement(toAdd: Measurement) = {
    numberOfMeasurements += 1
    insulinTotal += toAdd.insulinAdministered
    glucoseTotal += toAdd.glucose
  }

  def removeMeasurement(toRemove: Measurement) = {
    numberOfMeasurements -= 1
    insulinTotal -= toRemove.insulinAdministered
    glucoseTotal -= toRemove.glucose
  }

  def glucoseAverage: Float = glucoseTotal.asInstanceOf[Float] / numberOfMeasurements
}

object DayStatistics {
  def apply() = new DayStatistics(0, 0, 0)
}