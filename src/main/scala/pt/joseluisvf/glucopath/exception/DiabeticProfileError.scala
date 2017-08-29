package pt.joseluisvf.glucopath.exception

import pt.joseluisvf.glucopath.domain.user.DiabeticProfile

sealed trait DiabeticProfileError extends GlucopathError

final case class GlucoseRangeOutsideReasonableBoundsError(range: (Int, Int)) extends DiabeticProfileError {
  override val reason: String = s"The provided range <[${range._1} ${range._2}]> must consist of positive integers and the minimum range must be less than the maximum range"
}

final case class GlucoseMitigationOutsideReasonableBoundsError(glucoseMitigation: Int) extends DiabeticProfileError {
  override val reason: String = s"The provided glucose mitigation <$glucoseMitigation> should be within reasonable bounds [${DiabeticProfile.MINIMUM_GLUCOSE_MITIGATION} - ${DiabeticProfile.MAXIMUM_GLUCOSE_MITIGATION}]"
}

final case class CarbohydrateMitigationOutsideReasonableBoundsError(carbohydrateMitigation: Int) extends DiabeticProfileError {
  override val reason: String = s"The provided carbohydrate mitigation <$carbohydrateMitigation> should be within reasonable bounds [${DiabeticProfile.MINIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT} - ${DiabeticProfile.MAXIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT}]"
}