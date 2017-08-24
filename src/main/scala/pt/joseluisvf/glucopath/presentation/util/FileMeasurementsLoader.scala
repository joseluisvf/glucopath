package pt.joseluisvf.glucopath.presentation.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, Measurements, WarningLevel}
import pt.joseluisvf.glucopath.domain.user.User

import scala.io.Source


object FileMeasurementsLoader {
  def importMeasurementsFrom(filePath: String, user: User): User = {

    for (line <- Source.fromFile(filePath).getLines) {
      user.addMeasurement(parseMeasurement(line))
    }

    user
  }

  private def parseMeasurement(measurementAsText: String): Measurement = {
    val splitMeasurement = measurementAsText.split(",")
    val glucose = Integer.parseInt(splitMeasurement(0))
    val date = LocalDateTime.parse(splitMeasurement(1), DateTimeFormatter.ofPattern("dd MM yyyy HH:mm"))
    val beforeOrAfterMeal = if (splitMeasurement(2) == "b") BeforeOrAfterMeal.BEFORE_MEAL else BeforeOrAfterMeal.AFTER_MEAL
    val whatWasEaten = splitMeasurement(3)
    val carbsEaten = Integer.parseInt(splitMeasurement(4))
    val insulinAdministered = Integer.parseInt(splitMeasurement(5))
    val description = splitMeasurement(6)
    val warningLevel = WarningLevel.withName(splitMeasurement(7))

    Measurement(glucose, date, beforeOrAfterMeal, whatWasEaten, carbsEaten, insulinAdministered, description, warningLevel)
  }
}
