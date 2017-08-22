package pt.joseluisvf.glucopath.domain.user

import java.time.LocalDate
import java.util.UUID

import pt.joseluisvf.glucopath.domain.day.Days
import pt.joseluisvf.glucopath.domain.measurement.{Measurement, Measurements}

class User(val name: String, days: Days, diabeticProfile: DiabeticProfile) {
  def addMeasurement(toAdd: Measurement): Measurements = days.addMeasurement(toAdd)
  def getMeasurement(id: UUID, date: LocalDate): Option[Measurement] = days.getMeasurement(id, date)
  def removeMeasurement(id: UUID, date: LocalDate): Measurements = days.removeMeasurement(id, date)
  def getAllMeasurements = days.getAllMeasurements
  def getTodayMeasurements: Option[Measurements] = days.getTodayMeasurements
}
