package pt.joseluisvf.glucopath.domain.measurement

import java.time.LocalDateTime
import java.util.UUID

import org.scalatest.{Matchers, WordSpec}
import pt.joseluisvf.glucopath.domain.measurement
import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel

abstract class AbstractMeasurementTest extends WordSpec with Matchers {
  protected val DEFAULT_GLUCOSE = 100
  protected val DEFAULT_DATE_TIME: LocalDateTime =
    LocalDateTime
      .now()
      .withSecond(1)
      .withMinute(1)
      .withHour(1)
      .withDayOfMonth(1)
      .withMonth(1)
      .withYear(1)
  protected val DEFAULT_WHAT_WAS_EATEN = "food"
  protected val DEFAULT_CARBOHYDRATES_EATEN = 2
  protected val DEFAULT_INSULIN_ADMINISTERED = 4
  protected val DEFAULT_COMMENTS = "no comments"
  protected val DEFAULT_WARNING_LEVEL: measurement.WarningLevel.Value = Measurement.defaultWarningLevel
  protected val DEFAULT_BEFORE_OR_AFTER_MEAL = BeforeOrAfterMeal.BEFORE_MEAL

  protected val DEFAULT_GLUCOSE_1 = 100
  protected val DEFAULT_DATE_1: LocalDateTime = LocalDateTime.now()
  protected val DEFAULT_BEFORE_OR_AFTER_MEAL_1 = BeforeOrAfterMeal.BEFORE_MEAL
  protected val DEFAULT_WHAT_WAS_EATEN_1 = "Apples"
  protected val DEFAULT_CARBOHYDRATES_EATEN_1 = 1
  protected val DEFAULT_INSULIN_ADMINISTERED_1 = 10
  protected val DEFAULT_COMMENTS_1 = "no comments - 1"
  protected val DEFAULT_WARNING_LEVEL_1 = WarningLevel.GREEN
  protected val DEFAULT_UUID_1: UUID = UUID.randomUUID()

  protected val DEFAULT_GLUCOSE_2 = 200
  protected val DEFAULT_DATE_2: LocalDateTime = LocalDateTime.now()
  protected val DEFAULT_BEFORE_OR_AFTER_MEAL_2 = BeforeOrAfterMeal.AFTER_MEAL
  protected val DEFAULT_WHAT_WAS_EATEN_2 = "Oranges"
  protected val DEFAULT_CARBOHYDRATES_EATEN_2 = 2
  protected val DEFAULT_INSULIN_ADMINISTERED_2 = 20
  protected val DEFAULT_COMMENTS_2 = "no comments - 2"
  protected val DEFAULT_WARNING_LEVEL_2 = WarningLevel.YELLOW
  protected val DEFAULT_UUID_2: UUID = UUID.randomUUID()


  def makeDefaultMeasurement(): Measurement =
    Measurement(
      DEFAULT_GLUCOSE,
      DEFAULT_DATE_TIME,
      DEFAULT_BEFORE_OR_AFTER_MEAL,
      DEFAULT_WHAT_WAS_EATEN,
      DEFAULT_CARBOHYDRATES_EATEN,
      DEFAULT_INSULIN_ADMINISTERED,
      DEFAULT_COMMENTS,
      DEFAULT_WARNING_LEVEL)

  def makeMeasurementWithGivenId(
                                  glucose: Int,
                                  date: LocalDateTime,
                                  beforeOrAfterMeal: BeforeOrAfterMeal,
                                  whatWasEaten: String,
                                  carbohydratesEaten: Int,
                                  insulinAdministered: Int,
                                  comments: String,
                                  warningLevel: WarningLevel,
                                  id: UUID): Measurement = {

    val ru = scala.reflect.runtime.universe
    val rm = ru.runtimeMirror(getClass.getClassLoader)
    val instanceMirror = rm.reflect(Measurement)
    val methodmakeMeasurementWithId = ru.typeOf[Measurement.type].decl(ru.TermName("makeMeasurementWithId")).asMethod
    val bugh = instanceMirror.reflectMethod(methodmakeMeasurementWithId)
    bugh(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEaten, insulinAdministered, comments, warningLevel, id).asInstanceOf[Measurement]
  }


  def withEmptyMeasurements(testCode: Measurements => Any): Unit = {
    val measurements = Measurements()
    testCode(measurements)
  }

  def withNonEmptyMeasurements(howMany: Int, testCode: Measurements => Any): Unit = {
    val measurements = Measurements()
    for (_ <- 0 until howMany) {
      measurements.addMeasurement(makeDefaultMeasurement())
    }
    testCode(measurements)
  }

  def withNonEmptyMeasurements(testCode: Measurements => Any, toAdd: Measurement*): Unit = {
    val measurements = Measurements()
    toAdd.foreach(measurements.addMeasurement)
    testCode(measurements)
  }
}
