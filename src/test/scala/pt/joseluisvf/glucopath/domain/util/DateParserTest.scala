package pt.joseluisvf.glucopath.domain.util

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeParseException

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class DateParserTest extends WordSpec with Matchers with GivenWhenThen {
  "The date parser" when {
    "parsing a local date" should {
      "parse a date given by the user correctly" in {
        Given("a date given by the user")
        val given = LocalDate.of(2000, 3, 31)
        When("its string representation is parsed")
        val result = DateParser.toLocalDate(given.toString)
        Then("it should be equal to the date")
        result should equal(given)
      }
    }

    "parsing a date from user input to local date" should {
      "correctly parse a date in the desired format" in {
        Given("a date as a string given by the user")
        val given = "31 03 2000"
        When("it is parsed")
        val result = DateParser.toLocalDateFromUserInput(given)
        Then("it should be equal to the equivalent LocalDate")
        val expected = LocalDate.of(2000, 3, 31)
        result should equal(expected)
      }

      "not parse a date when omitting trailing zeroes" in {
        Given("a date as a string given by the user without trailing zeroes")
        val given = "31 3 2000"
        When("it is parsed")
        Then("an error related to this should be intercepted")
        assertThrows[DateTimeParseException] {
          DateParser.toLocalDateFromUserInput(given)
        }
      }

      "not parse a date when not in the desired format" in {
        Given("a date as a string that is not in the desired format")
        val given = "03 31 2000"
        When("it is parsed")
        Then("an error related to this should be intercepted")
        assertThrows[DateTimeParseException] {
          DateParser.toLocalDateFromUserInput(given)
        }
      }
    }

    "translating a local Date to String" should {
      "produce the expected result for a valid date" in {
        Given("a local date")
        val given = LocalDate.of(1000, 4, 28)
        When("translating it")
        val result = DateParser.localDateToString(given)
        Then("its string representation should be as expected")
        val expected = "1000-04-28"
        result should equal(expected)
      }
    }

    "translating a local date time to string" should {
      "produce the expected result" in {
        Given("a local date time")
        val given = LocalDateTime.of(2000, 12, 23, 13, 24)
        When("translating it")
        val result = DateParser.localDateTimeToString(given)
        Then("its string representation should be as expected")
        val expected = given.toString
        result should equal(expected)
      }
    }

    "parsing a local date time string to Local Date Time" should {
      "parse it correctly" in {
        Given("a local date time")
        val date = LocalDateTime.of(2017, 4, 30, 12, 15)
        val given = date.toString
        When("its string representation is parsed")
        val result = DateParser.toLocalDateTime(given)
        Then("it should equal the original")
        result should equal(date)
      }

      "not parse a user-given date" in {
        Given("a user given date as string")
        val given = "30 04 2017 12:15"
        When("it is parsed")
        Then("an exception related to this should be intercepted")
        assertThrows[DateTimeParseException] {
          DateParser.toLocalDateTime(given)
        }
      }
    }

    "parsing a local time string to local time" should {
      "parse it correctly" in {
        Given("a string representation of a local time")
        val time = LocalTime.of(12, 15)
        val given = time.toString
        When("it is parsed")
        val result = DateParser.toLocalTime(given)
        Then("the result should equal the original")
        result should equal(time)
      }

      "not parse a user-given string" in {
        Given("a user defined string representation of a local time")
        val given = "13:25"
        When("it is parsed")
        Then("an exception related to this should be intercepted")
        assertThrows[DateTimeParseException] {
          DateParser.toLocalDateTime(given)
        }
      }
    }

    "parsing a user given date to Local Date Time" should {
      "correctly parse it" in {
        Given("a user defined string representation of a local date time")
        val given = "24 03 2000 13:10"
        When("it is parsed")
        val result = DateParser.toLocalDateTimeFromUserInput(given)
        Then("the result should equal the equivalent local date time")
        val expected = LocalDateTime.of(2000, 3, 24, 13, 10)
        result should equal(expected)
      }

      "not parse a local date time - given date" in {
        Given("a local date time string representation")
        val given = LocalDateTime.of(2000, 3, 24, 13, 10).toString
        When("it is parsed")
        Then("an exception related to this should be intercepted")
        assertThrows[DateTimeParseException] {
          DateParser.toLocalDateTimeFromUserInput(given)
        }
      }
    }
  }
}
