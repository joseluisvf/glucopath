package pt.joseluisvf.glucopath.service

import java.time.LocalDateTime

import measurement._
import pt.joseluisvf.glucopath.exception.{DiabeticProfileError, MeasurementError, SlowInsulinError}
import pt.joseluisvf.glucopath.persistence.GlucopathIO

trait UserService {
  def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): Either[MeasurementError, UserProto]

  def getDayByDate(userProto: UserProto, date: String): Option[DayProto]

  def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int

  def getOverallInfo(userProto: UserProto): String

  def exportMeasurements(userProto: UserProto, pathToFile: String = GlucopathIO.measurementsFileLocation): Unit

  def showMetricsPerTimePeriod(userProto: UserProto): String

  def writeMetricsPerTimePeriod(userProto: UserProto, pathToFile: String = GlucopathIO.measurementsFileLocation): Unit

  def alterDiabeticProfile(userProto: UserProto, diabeticProfileProto: DiabeticProfileProto): Either[DiabeticProfileError, UserProto]

  def alterSlowInsulin(userProto: UserProto, slowInsulinProto: SlowInsulinProto, localDateTime: LocalDateTime): Either[SlowInsulinError, UserProto]
}
