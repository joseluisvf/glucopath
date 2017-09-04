package pt.joseluisvf.glucopath.domain.user

import pt.joseluisvf.glucopath.exception._

case class DiabeticProfile(
                            // e.g. one unit mitigates 50mg/dl glucose
                            var glucoseMitigationPerInsulinUnit: Int,
                            // e.g. [80 - 150]
                            var idealGlucoseRange: (Int, Int),
                            // e.g. one unit annuls 12gr carbs
                            var carbohydrateMitigationPerInsulinUnit: Int) {

  require(isGlucoseMitigationWithinBounds,
    throw new DiabeticProfileException(GlucoseMitigationOutsideReasonableBoundsError(glucoseMitigationPerInsulinUnit)))

  require(idealGlucoseRange._1 > 0, throw new DiabeticProfileException(GlucoseRangeOutsideReasonableBoundsError(idealGlucoseRange)))

  require(idealGlucoseRange._2 > 0, throw new DiabeticProfileException(GlucoseRangeOutsideReasonableBoundsError(idealGlucoseRange)))

  require(idealGlucoseRange._2 > idealGlucoseRange._1,
    throw new DiabeticProfileException(GlucoseRangeOutsideReasonableBoundsError(idealGlucoseRange)))

  require(isCarbohydrateMitigationWithinBounds,
    throw new DiabeticProfileException(CarbohydrateMitigationOutsideReasonableBoundsError(carbohydrateMitigationPerInsulinUnit)))

  def calculateHowMuchInsulinToAdminister(glucoseMeasured: Int, carbohydratesConsumed: Int): Int = {
    val glucoseGoal: Int = (idealGlucoseRange._1 + idealGlucoseRange._2) / 2

    val insulinUnitsRequiredForGoal =
      if (glucoseMeasured < glucoseGoal) {
        // try to compensate by providing a negative value
        Math.ceil((glucoseMeasured - glucoseGoal).asInstanceOf[Float] / glucoseMitigationPerInsulinUnit)
      } else {
        (glucoseMeasured - glucoseGoal).asInstanceOf[Float] / glucoseMitigationPerInsulinUnit
      }

    val insulinUnitsRequiredForCarbohydratesConsumed =
      carbohydratesConsumed.asInstanceOf[Float] / carbohydrateMitigationPerInsulinUnit

    val result = Math.ceil(insulinUnitsRequiredForGoal + insulinUnitsRequiredForCarbohydratesConsumed).asInstanceOf[Int]

    if (result < 0) 0
    else result
  }

  def isGlucoseHypoglycemia(glucose: Int): Boolean = glucose < idealGlucoseRange._1

  def isGlucoseHyperglycemia(glucose: Int): Boolean = glucose > idealGlucoseRange._2

  def isGlucoseInRange(glucose: Int): Boolean = !(isGlucoseHypoglycemia(glucose) || isGlucoseHyperglycemia(glucose))

  private def isGlucoseMitigationWithinBounds: Boolean =
    DiabeticProfile.MINIMUM_GLUCOSE_MITIGATION <= glucoseMitigationPerInsulinUnit &&
      glucoseMitigationPerInsulinUnit <= DiabeticProfile.MAXIMUM_GLUCOSE_MITIGATION

  private def isCarbohydrateMitigationWithinBounds: Boolean =
    DiabeticProfile.MINIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT <= carbohydrateMitigationPerInsulinUnit &&
      carbohydrateMitigationPerInsulinUnit <= DiabeticProfile.MAXIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT
}

case object DiabeticProfile {
  val MINIMUM_GLUCOSE_MITIGATION: Int = 0
  val MAXIMUM_GLUCOSE_MITIGATION: Int = 200
  val MINIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT: Int = 0
  val MAXIMUM_CARBOHYDRATE_MITIGATION_PER_INSULIN_UNIT: Int = 50
}