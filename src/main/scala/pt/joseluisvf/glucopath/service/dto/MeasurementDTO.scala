package pt.joseluisvf.glucopath.service.dto

import java.time.LocalDateTime
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel

class MeasurementDTO(
                      var _glucose: Int,
                      var _date: String,
                      var _beforeOrAfterMeal: BeforeOrAfterMeal,
                      var _whatWasEaten: String,
                      var _insulinAdministered: Int,
                      var _comments: String,
                      var _warningLevel: WarningLevel,
                      var _id: String) extends Serializable {

  // getters
  def glucose: Int = _glucose
  def date: String = _date
  def beforeOrAfterMeal: BeforeOrAfterMeal = _beforeOrAfterMeal
  def whatWasEaten: String = _whatWasEaten
  def insulinAdministered: Int = _insulinAdministered
  def comments: String = _comments
  def warningLevel: WarningLevel = _warningLevel
  def id: String = _id
  // setters
  def glucose_(value: Int): Unit = _glucose = value
  def date_(value: String): Unit = _date = value
  def beforeOrAfterMeal_(value: BeforeOrAfterMeal): Unit = _beforeOrAfterMeal = value
  def whatWasEaten_(value: String): Unit = _whatWasEaten = value
  def insulinAdministered_(value: Int): Unit = _insulinAdministered = value
  def comments_(value: String): Unit = _comments = value
  def warningLevel_(value: WarningLevel): Unit = _warningLevel = value
  def id_(value: String): Unit = _id = value


}

object MeasurementDTO {
  def getLocalDateTimeConverted(date: String): LocalDateTime = LocalDateTime.parse(date)
  def getDateFromLocalDateTime(value: LocalDateTime): String = value.toString
  def getIdConverted(id: String): UUID = UUID.fromString(id)
  def getIdFromUUID(uuid: UUID): String = uuid.toString
}
