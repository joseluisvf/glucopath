package pt.joseluisvf.glucopath.service.impl

import java.time.LocalDateTime

import measurement._
import pt.joseluisvf.glucopath.domain.day.SlowInsulin
import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.domain.user.{User, UserStatistics}
import pt.joseluisvf.glucopath.domain.util.DateParser
import pt.joseluisvf.glucopath.exception.{DiabeticProfileError, DiabeticProfileException, SlowInsulinError, SlowInsulinException}
import pt.joseluisvf.glucopath.persistence.GlucopathIO
import pt.joseluisvf.glucopath.service.UserService
import pt.joseluisvf.glucopath.service.mapper._

object UserServiceImpl extends UserService {
  override def addMeasurement(userProto: UserProto, measurementProto: MeasurementProto): UserProto = {
    val user: User = UserMapperImpl.toEntity(userProto)
    val measurement: Measurement = MeasurementMapperImpl.toEntity(measurementProto)
    user.addMeasurement(measurement)
    saveUserToFile(user)
    UserMapperImpl.toProto(user)
  }

  override def getDayByDate(userProto: UserProto, date: String): Option[DayProto] = {
    val user: User = UserMapperImpl.toEntity(userProto)
    val parsedDate = DateParser.toLocalDate(date)
    val day = user.getDayByDate(parsedDate)

    day match {
      case Some(d) => Some(DayMapperImpl.toProto(d))
      case _ => None
    }
  }

  override def calculateInsulinToAdminister(userProto: UserProto, glucoseMeasured: Int, carbohydratesEaten: Int): Int = {
    val user: User = UserMapperImpl.toEntity(userProto)
    user.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesEaten)
  }

  override def getOverallInfo(userProto: UserProto): String = {
    val user: User = UserMapperImpl.toEntity(userProto)
    user.getOverallInfo
  }

  def saveUserToFile(user: User): Unit = GlucopathIO.saveUserToFile(user)

  override def exportMeasurements(userProto: UserProto): Unit = {
    val user: User = UserMapperImpl.toEntity(userProto)
    val measurements: Measurements = user.getAllMeasurements
    val toWrite = measurements.measurements.map(measurementToString).mkString("\n")
    GlucopathIO.saveMeasurementsToFile(toWrite)
  }

  private def measurementToString(measurement: Measurement) = {
    s"${measurement.glucose}," +
      s"${measurement.date.toString}," +
      s"${measurement.beforeOrAfterMeal.toString}," +
      s"${measurement.whatWasEaten}," +
      s"${measurement.carbohydratesEatenInGrams}," +
      s"${measurement.insulinAdministered}," +
      s"${measurement.comments}," +
      s"${measurement.warningLevel.toString}"
  }

  override def showMetricsPerTimePeriod(userProto: UserProto): String = {
    val user: User = UserMapperImpl.toEntity(userProto)
    UserStatistics.showMetricsPerTimePeriod(user)
  }

  override def writeMetricsPerTimePeriod(userProto: UserProto): Unit = {
    val user: User = UserMapperImpl.toEntity(userProto)
    val metricsAsCsv = UserStatistics.getMetricsAsCsv(user)
    GlucopathIO.saveMetricsToFile(metricsAsCsv)
  }

  override def alterDiabeticProfile(
                                     userProto: UserProto,
                                     diabeticProfileProto: DiabeticProfileProto): Either[DiabeticProfileError, UserProto] = {
    val user: User = UserMapperImpl.toEntity(userProto)
    try {
      val newDiabeticProfile = DiabeticProfileMapperImpl.toEntity(diabeticProfileProto)
      user.alterDiabeticProfile(newDiabeticProfile)

      saveUserToFile(user)
      Right(UserMapperImpl.toProto(user))
    } catch {
      case dpe: DiabeticProfileException =>
        Left(dpe.getGlucopathError.asInstanceOf[DiabeticProfileError])
    }
  }

  override def alterSlowInsulin(
                                 userProto: UserProto,
                                 slowInsulinProto: SlowInsulinProto,
                                 localDateTime: LocalDateTime): Either[SlowInsulinError, UserProto] = {
    val user: User = UserMapperImpl.toEntity(userProto)

    try {
      val slowInsulin: SlowInsulin = SlowInsulinMapperImpl.toEntity(slowInsulinProto)
      user.alterSlowInsulin(slowInsulin, localDateTime)
      saveUserToFile(user)
      Right(UserMapperImpl.toProto(user))
    } catch {
      case sie: SlowInsulinException =>
        Left(sie.getGlucopathError.asInstanceOf[SlowInsulinError])
    }
  }
}
