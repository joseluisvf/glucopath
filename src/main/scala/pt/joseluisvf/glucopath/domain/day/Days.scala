package pt.joseluisvf.glucopath.domain.day

import java.time.LocalDate
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}
import pt.joseluisvf.glucopath.exception.{DayError, DayWithDateDoesNotExistError}
import pt.joseluisvf.glucopath.presentation.util.DisplayOptions

import scala.collection.mutable.ListBuffer

case class Days(days: ListBuffer[Day] = ListBuffer.empty) {

  private def addDay(toAdd: Day): Days = {
    days += toAdd
    this
  }

  def addMeasurement(toAdd: Measurement): Measurements = {
    val maybeDay = getDayByDate(toAdd.date.toLocalDate)
    maybeDay match {
      case Some(day) => day.addMeasurement(toAdd)
      case _ =>
        val newDay = Day(toAdd.date.toLocalDate)
        newDay.addMeasurement(toAdd)
        this.addDay(newDay)
        newDay.measurements
    }
  }

  def getDayByDate(date: LocalDate): Option[Day] = days.find(_.date == date)

  def getMeasurement(id: UUID, date: LocalDate): Either[DayError, Option[Measurement]] = {
    val maybeDay = getDayByDate(date)

    maybeDay match {
      case Some(day) => Right(day.getMeasurement(id))
      case _ => Left(DayWithDateDoesNotExistError(date))
    }
  }

  def getToday: Option[Day] = {
    getDayByDate(LocalDate.now())
  }

  def getTodayMeasurements: Option[Measurements] = {
    val today = getDayByDate(LocalDate.now())
    today match {
      case Some(day) => Some(day.measurements)
      case _ => None
    }
  }

  def removeMeasurement(id: UUID, date: LocalDate): Either[DayError, Measurements] = {
    val maybeDay = getDayByDate(date)
    maybeDay match {
      case Some(day) => Right(day.removeMeasurement(id))
      case _ => Left(DayWithDateDoesNotExistError(date))
    }
  }

  def getAllMeasurements: Measurements = {
    val allMeasurements = Measurements()
    for (day <- days) {
      allMeasurements.addMeasurements(day.measurements)
    }
    allMeasurements
  }

  def aggregateDayStatistics: DayStatistics = {
    days.foldLeft(DayStatistics()) {
      (accum, item) => accum + item.dayStatistics
    }
  }

  override def toString: String = {
    s"Days:\n${days.map(_.toString()).mkString(DisplayOptions.getSeparator)}"
  }
}
