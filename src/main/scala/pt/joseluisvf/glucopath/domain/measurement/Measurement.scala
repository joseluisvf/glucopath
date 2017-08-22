package pt.joseluisvf.glucopath.domain.measurement

import java.time.LocalDateTime
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel

trait Measurement {
  def glucose: Int

  def date: LocalDateTime

  def beforeOrAfterMeal: BeforeOrAfterMeal

  def whatWasEaten: String

  def insulinAdministered: Int

  def comments: String

  def warningLevel: WarningLevel

  def id: UUID

  require(glucose > 0, "glucose must be a valid non-negative amount")
  require(insulinAdministered > 0, "insulin administered must be a valid non-negative amount")
}

object Measurement {

  private case class MeasurementImpl(
                                      glucose: Int,
                                      date: LocalDateTime,
                                      beforeOrAfterMeal: BeforeOrAfterMeal,
                                      whatWasEaten: String,
                                      insulinAdministered: Int,
                                      comments: String,
                                      warningLevel: WarningLevel,
                                      id: UUID = java.util.UUID.randomUUID()) extends Measurement

  def apply(
             glucose: Int,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             insulinAdministered: Int,
             comments: String): Measurement =
    MeasurementImpl(glucose, LocalDateTime.now(), beforeOrAfterMeal, whatWasEaten, insulinAdministered, comments, WarningLevel.defaultWarningLevel)

  def apply(
             glucose: Int,
             date: LocalDateTime,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             insulinAdministered: Int,
             comments: String): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, insulinAdministered, comments, WarningLevel.defaultWarningLevel)

  def apply(
             glucose: Int,
             date: LocalDateTime,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             insulinAdministered: Int,
             comments: String,
             warningLevel: WarningLevel): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, insulinAdministered, comments, warningLevel)

  // TODO temos de permutar sobre todos os defaults ou que??? raios partam


  // for testing purposes only
  protected def makeMeasurementWithId(
                                       glucose: Int,
                                       date: LocalDateTime,
                                       beforeOrAfterMeal: BeforeOrAfterMeal,
                                       whatWasEaten: String,
                                       insulinAdministered: Int,
                                       comments: String,
                                       warningLevel: WarningLevel,
                                       id: UUID): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, insulinAdministered, comments, warningLevel, id)
}
