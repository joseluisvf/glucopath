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

  def carbohydratesEatenInGrams: Int

  def insulinAdministered: Int

  def comments: String

  def warningLevel: WarningLevel

  def id: UUID

  require(glucose > 0, "glucose must be a valid non-negative amount")
  require(insulinAdministered >= 0, "insulin administered must be a valid non-negative amount")
  require(carbohydratesEatenInGrams >= 0, "carbohydrates eaten must be a valid non-negative amount")

  //  override def toString = s"Measurement($glucose, $date, $beforeOrAfterMeal, $whatWasEaten, $insulinAdministered, $comments, $warningLevel, $id)"

  override def toString = s"Measurement(glucose=$glucose, date=$date, beforeOrAfterMeal=$beforeOrAfterMeal\n whatWasEaten=$whatWasEaten, carbohydratesEatenInGrams=$carbohydratesEatenInGrams, insulinAdministered=$insulinAdministered\n comments=$comments, warningLevel=$warningLevel, id=$id)"
}

object Measurement {
  val defaultWarningLevel = WarningLevel.GREEN


  private case class MeasurementImpl(
                                      glucose: Int,
                                      date: LocalDateTime,
                                      beforeOrAfterMeal: BeforeOrAfterMeal,
                                      whatWasEaten: String,
                                      carbohydratesEatenInGrams: Int,
                                      insulinAdministered: Int,
                                      comments: String,
                                      warningLevel: WarningLevel,
                                      id: UUID = java.util.UUID.randomUUID()) extends Measurement

  def apply(
             glucose: Int,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             carbohydratesEatenInGrams: Int,
             insulinAdministered: Int,
             comments: String): Measurement =
    MeasurementImpl(glucose, LocalDateTime.now(), beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, defaultWarningLevel)

  def apply(
             glucose: Int,
             date: LocalDateTime,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             carbohydratesEatenInGrams: Int,
             insulinAdministered: Int,
             comments: String): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, defaultWarningLevel)

  def apply(
             glucose: Int,
             date: LocalDateTime,
             beforeOrAfterMeal: BeforeOrAfterMeal,
             whatWasEaten: String,
             carbohydratesEatenInGrams: Int,
             insulinAdministered: Int,
             comments: String,
             warningLevel: WarningLevel): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, warningLevel)

  // TODO temos de permutar sobre todos os defaults ou que??? raios partam

  def makeMeasurementWithId(
                             glucose: Int,
                             date: LocalDateTime,
                             beforeOrAfterMeal: BeforeOrAfterMeal,
                             whatWasEaten: String,
                             carbohydratesEatenInGrams: Int,
                             insulinAdministered: Int,
                             comments: String,
                             warningLevel: WarningLevel,
                             id: UUID): Measurement =
    MeasurementImpl(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, warningLevel, id)
}
