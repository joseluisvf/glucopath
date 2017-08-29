package pt.joseluisvf.glucopath.service

import measurement.{DayProto, DiabeticProfileProto, MeasurementProto, UserProto}
import pt.joseluisvf.glucopath.exception.DiabeticProfileError

trait UserService {
  def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): UserProto
  def getDayByDate(userProto: UserProto, date: String): Option[DayProto]
  def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int
  def getOverallInfo(userProto: UserProto): String
  def exportMeasurements(userProto: UserProto): Unit
  def showMetricsPerTimePeriod(userProto: UserProto): String
  def writeMetricsPerTimePeriod(userProto: UserProto): Unit
  def alterDiabeticProfile(userProto: UserProto, diabeticProfileProto: DiabeticProfileProto): Either[DiabeticProfileError, UserProto]
}
