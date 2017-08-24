package pt.joseluisvf.glucopath.domain.day

import pt.joseluisvf.glucopath.domain.measurement.Measurement

case class DayStatistics(var numberOfMeasurements: Int, var insulinTotal: Int, var glucoseTotal: Int, var carboHydratesTotal: Int) {
  def addMeasurement(toAdd: Measurement): Unit = {
    numberOfMeasurements += 1
    insulinTotal += toAdd.insulinAdministered
    glucoseTotal += toAdd.glucose
    carboHydratesTotal += toAdd.carbohydratesEatenInGrams
  }

  def removeMeasurement(toRemove: Measurement): Unit = {
    numberOfMeasurements -= 1
    insulinTotal -= toRemove.insulinAdministered
    glucoseTotal -= toRemove.glucose
  }

  def glucoseAverage: Float = glucoseTotal.asInstanceOf[Float] / numberOfMeasurements

  override def toString: String = {
    s"Number of measurements:$numberOfMeasurements\n" +
      s"Insulin Total:$insulinTotal\n" +
      s"Glucose Total:$glucoseTotal\n" +
      s"Carbohydrates Total:$carboHydratesTotal\n" +
      s"Glucose Average: $glucoseAverage"
  }

  def +(that: DayStatistics): DayStatistics = {
    DayStatistics(
      this.numberOfMeasurements + that.numberOfMeasurements,
      this.insulinTotal + that.insulinTotal,
      this.glucoseTotal + that.glucoseTotal,
      this.carboHydratesTotal + that.carboHydratesTotal)
  }
}

object DayStatistics {
  def apply() = new DayStatistics(0, 0, 0, 0)
}