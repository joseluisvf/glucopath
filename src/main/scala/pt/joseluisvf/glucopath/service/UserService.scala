package pt.joseluisvf.glucopath.service

import measurement.{DayProto, DaysProto, MeasurementProto, UserProto}

trait UserService {
  def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): UserProto
  def getDayByDate(userProto: UserProto, date: String): Option[DayProto]
  def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int
  def getOverallInfo(userProto: UserProto): String
  def exportMeasurements(userProto: UserProto): Unit
}
