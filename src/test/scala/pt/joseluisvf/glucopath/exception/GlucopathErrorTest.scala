package pt.joseluisvf.glucopath.exception

import java.time.LocalDate

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}
import pt.joseluisvf.glucopath.domain.day.Day

class GlucopathErrorTest extends WordSpec with Matchers with GivenWhenThen {
  "Glucopath Error" should {
    "not equal something other than a glucopath error" in {
      Given("an object other than a glucopath error")
      val day = Day()
      val dayError = DayWithDateDoesNotExistError(LocalDate.now)
      When("comparing it to a glucopath error")
      Then("these should not be equal")
      dayError should not equal day
    }

    "be equal to an error of the same kind" in {
      Given("a glucopath error like the one being compared")
      val dayError = DayWithDateDoesNotExistError(LocalDate.now)
      val anotherDayError = DayWithDateDoesNotExistError(LocalDate.now)
      When("they are compared")
      Then("they should be equal")
      dayError should equal(anotherDayError)
    }
  }
}
