package pt.joseluisvf.glucopath.service

import java.time.LocalDateTime

import measurement._
import pt.joseluisvf.glucopath.exception.{DiabeticProfileError, SlowInsulinError}

trait UserService {
  def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): UserProto
  def getDayByDate(userProto: UserProto, date: String): Option[DayProto]
  def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int
  def getOverallInfo(userProto: UserProto): String
  def exportMeasurements(userProto: UserProto): Unit
  def showMetricsPerTimePeriod(userProto: UserProto): String
  def writeMetricsPerTimePeriod(userProto: UserProto): Unit
  def alterDiabeticProfile(userProto: UserProto, diabeticProfileProto: DiabeticProfileProto): Either[DiabeticProfileError, UserProto]
  def alterSlowInsulin(userProto: UserProto, slowInsulinProto: SlowInsulinProto, localDateTime: LocalDateTime): Either[SlowInsulinError, UserProto]
}
