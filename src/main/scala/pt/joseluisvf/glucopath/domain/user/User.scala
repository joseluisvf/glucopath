package pt.joseluisvf.glucopath.domain.user

import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import pt.joseluisvf.glucopath.domain.day.{Day, Days, SlowInsulin}
import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.exception.{DayError, DiabeticProfileNotDefinedError, UserException}
import pt.joseluisvf.glucopath.presentation.util.DisplayOptions

class User(val name: String, val days: Days, var diabeticProfile: DiabeticProfile) {

  require(diabeticProfile != null, throw new UserException(DiabeticProfileNotDefinedError()))

  def addMeasurement(toAdd: Measurement): Measurements = days.addMeasurement(toAdd)

  def getMeasurement(id: UUID, date: LocalDate): Either[DayError, Option[Measurement]] = days.getMeasurement(id, date)

  def removeMeasurement(id: UUID, date: LocalDate): Either[DayError, Measurements] = days.removeMeasurement(id, date)

  def getAllMeasurements: Measurements = days.getAllMeasurements

  def getTodayMeasurements: Option[Measurements] = days.getTodayMeasurements

  def getToday: Option[Day] = getDayByDate(LocalDate.now())

  def getDayByDate(date: LocalDate): Option[Day] = days.getDayByDate(date)

  def calculateHowMuchInsulinToAdminister(glucoseMeasured: Int, carbohydratesConsumed: Int): Int =
    diabeticProfile.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesConsumed)

  def getOverallInfo: String = {
    s"${DisplayOptions.getSeparator}\nOverall information for user $name:\n" +
      s"$diabeticProfile\n${DisplayOptions.getSeparator}" +
      s"Aggregated Statistics:\n${this.days.aggregateDayStatistics}\n${DisplayOptions.getSeparator}" +
      s"All days:\n${days.toString}"
  }

  def alterDiabeticProfile(newDiabeticProfile: DiabeticProfile): Unit = this.diabeticProfile = newDiabeticProfile

  def alterSlowInsulin(slowInsulin: SlowInsulin, localDateTime: LocalDateTime) = {
    days.alterSlowInsulin(slowInsulin, localDateTime)
  }
}
