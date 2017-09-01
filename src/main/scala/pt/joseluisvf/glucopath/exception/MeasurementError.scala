package pt.joseluisvf.glucopath.exception

import pt.joseluisvf.glucopath.domain.measurement.Measurement

sealed trait MeasurementError extends GlucopathError

final case class GlucoseOutsideBoundsError(glucose: Int) extends MeasurementError {
  override val reason: String = s"The provided glucose reading <$glucose> should be within reasonable bounds" +
    s" [${Measurement.MINIMUM_GLUCOSE} - ${Measurement.MAXIMUM_GLUCOSE}]"
}

final case class InsulinAdministeredOutsideBoundsError(insulin: Int) extends MeasurementError {
  override val reason: String = s"The provided insulin administered <$insulin> should be within reasonable bounds" +
    s" [${Measurement.MINIMUM_INSULIN_ADMINISTERED} - ${Measurement.MAXIMUM_INSULIN_ADMINISTERED}]"
}

final case class CarbohydratesEatenInGramsOutsideBoundsError(carbohydratesEatenInGrams: Int) extends MeasurementError {
  override val reason: String = s"The provided carbohydrates eaten in grams <$carbohydratesEatenInGrams> should be within reasonable bounds" +
    s" [${Measurement.MINIMUM_CARBOHYDRATES_EATEN_IN_GRAMS} - ${Measurement.MAXIMUM_CARBOHYDRATES_EATEN_IN_GRAMS}]"
}


