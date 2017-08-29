package pt.joseluisvf.glucopath.domain.measurement

import java.time.LocalDateTime
import java.util.UUID

import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel
import pt.joseluisvf.glucopath.exception.{CarbohydratesEatenInGramsOutsideBoundsError, GlucoseOutsideBoundsError, InsulinAdministeredOutsideBoundsError, MeasurementException}

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

  require(isGlucoseWithinBounds, throw new MeasurementException(GlucoseOutsideBoundsError(glucose)))
  require(isInsulinWithinBounds, throw new MeasurementException(InsulinAdministeredOutsideBoundsError(insulinAdministered)))
  require(isCarbohydratesEatenInGramsWithinBounds, throw new MeasurementException(CarbohydratesEatenInGramsOutsideBoundsError(carbohydratesEatenInGrams)))
git st
  override def toString: String =
    s"Measurement(glucose = $glucose, date = $date, beforeOrAfterMeal = $beforeOrAfterMeal\n" +
      s" whatWasEaten = $whatWasEaten, carbohydratesEatenInGrams = $carbohydratesEatenInGrams, insulinAdministered = $insulinAdministered\n" +
      s" comments = $comments, warningLevel = $warningLevel, id = $id)"

  private def isGlucoseWithinBounds: Boolean =
    Measurement.MINIMUM_GLUCOSE <= glucose &&
      glucose <= Measurement.MAXIMUM_GLUCOSE

  private def isInsulinWithinBounds: Boolean =
    Measurement.MINIMUM_INSULIN_ADMINISTERED <= insulinAdministered &&
      insulinAdministered <= Measurement.MAXIMUM_GLUCOSE

  private def isCarbohydratesEatenInGramsWithinBounds: Boolean =
    Measurement.MINIMUM_CARBOHYDRATES_EATEN_IN_GRAMS <= carbohydratesEatenInGrams &&
      carbohydratesEatenInGrams <= Measurement.MAXIMUM_CARBOHYDRATES_EATEN_IN_GRAMS

}

object Measurement {
  val defaultWarningLevel = WarningLevel.GREEN
  val MINIMUM_GLUCOSE: Int = 0
  val MAXIMUM_GLUCOSE: Int = 700
  val MINIMUM_INSULIN_ADMINISTERED: Int = 0
  val MAXIMUM_INSULIN_ADMINISTERED: Int = 50
  val MINIMUM_CARBOHYDRATES_EATEN_IN_GRAMS: Int = 0
  val MAXIMUM_CARBOHYDRATES_EATEN_IN_GRAMS: Int = 300

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
