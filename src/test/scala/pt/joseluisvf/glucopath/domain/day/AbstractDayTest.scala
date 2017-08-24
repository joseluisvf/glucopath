package pt.joseluisvf.glucopath.domain.day

import java.time.{LocalDate, LocalDateTime}

import org.scalatest.{BeforeAndAfterEach, Matchers, PrivateMethodTester, WordSpec}
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement}

abstract class AbstractDayTest extends WordSpec with Matchers with BeforeAndAfterEach with PrivateMethodTester {
  private val localDateAlpha = LocalDateTime.of(1, 1, 1, 1, 1)
  private val localDateBeta = LocalDateTime.of(2, 2, 2, 2, 2)
  private val today = LocalDateTime.now()

  val aMeasurement = Measurement(10, BeforeOrAfterMeal.BEFORE_MEAL, "apple", 1, 1, "no comments")
  val anotherMeasurement = Measurement(1, BeforeOrAfterMeal.BEFORE_MEAL, "orange", 1, 2, "no comments")
  val yetAnotherMeasurement = Measurement(5, BeforeOrAfterMeal.AFTER_MEAL, "pear", 1, 3, "no comments")
  val measurementOnDateAlpha = Measurement(10, localDateAlpha, BeforeOrAfterMeal.BEFORE_MEAL, "banana", 1, 1, "no comments")
  val anotherMeasurementOnDateAlpha = Measurement(20, localDateAlpha, BeforeOrAfterMeal.BEFORE_MEAL, "bigger banana", 1, 1, "no comments")
  val measurementOnDateBeta = Measurement(30, localDateBeta, BeforeOrAfterMeal.BEFORE_MEAL, "grapefruit", 1, 1, "no comments")
  val aMeasurementForToday = Measurement(20, today, BeforeOrAfterMeal.BEFORE_MEAL, "grape", 1, 1, "no comments")
  val anotherMeasurementForToday = Measurement(20, today, BeforeOrAfterMeal.BEFORE_MEAL, "peach", 1, 1, "no comments")


  def getDayByDate(days: Days, date: LocalDate): Option[Day] = {
    val getDayByDate = PrivateMethod[Option[Day]]('getDayByDate)
    days invokePrivate getDayByDate(date)
  }


}
