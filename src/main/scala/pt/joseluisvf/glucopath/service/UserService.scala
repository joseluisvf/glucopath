package pt.joseluisvf.glucopath.service

import measurement.{DayProto, MeasurementProto, UserProto}

trait UserService {
  def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): UserProto
  def getDayByDate(userProto: UserProto, date: String): Option[DayProto]
  def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int
  def getOverallInfo(userProto: UserProto): String
  def exportMeasurements(userProto: UserProto): Unit
  def showMetricsPerTimePeriod(userProto: UserProto): String
  def writeMetricsPerTimePeriod(userProto: UserProto): Unit
}
