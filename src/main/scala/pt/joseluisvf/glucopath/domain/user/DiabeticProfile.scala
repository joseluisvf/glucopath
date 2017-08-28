package pt.joseluisvf.glucopath.domain.user

case class DiabeticProfile(
                     // e.g. one unit mitigates 50mg/dl glucose
                       var glucoseMitigationPerInsulinUnit: Int,
                     // e.g. [80 - 150]
                       var idealGlucoseRange: (Int, Int),
                     // e.g. one unit annuls 12gr carbs
                       var carbohydrateMitigationPerInsulinUnit: Int) {

  require(glucoseMitigationPerInsulinUnit > 0, "glucose mitigation must be a positive number\ne.g. 50")
  require(idealGlucoseRange._1 > 0, "the minimum range must be a positive number\ne.g. 80")
  require(idealGlucoseRange._2 > 0, "the maximum range must be a positive number\ne.g. 150")
  require(idealGlucoseRange._2 > idealGlucoseRange._1, "the maximum range must be higher than the minimum range")
  require(carbohydrateMitigationPerInsulinUnit > 0, "carbohydrate mitigation must be a positive number\ne.g. 12")


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

  val minGlucoseRange: Int = idealGlucoseRange._1
  val maxGlucoseRange: Int = idealGlucoseRange._2

  def isGlucoseHipoglicemia(glucose: Int): Boolean = glucose <= minGlucoseRange
  def isGlucoseHiperglicemia(glucose: Int): Boolean = glucose >= maxGlucoseRange
  def isGlucoseInRange(glucose: Int): Boolean = minGlucoseRange < glucose && glucose < maxGlucoseRange
}
