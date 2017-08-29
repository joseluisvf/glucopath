package pt.joseluisvf.glucopath.domain.util

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime}

/**
  * Responsible for marshalling a date to and from a string representation
  */
object DateParser {
  def toLocalDate(date: String): LocalDate = LocalDate.parse(date)
  def toLocalDateFromUserInput(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MM yyyy"))
  def localDateToString(date: LocalDate): String = date.toString
  def toLocalDateTime(date: String): LocalDateTime = LocalDateTime.parse(date)
  def toLocalTime(date: String): LocalTime = LocalTime.parse(date)
  def toLocalDateTimeFromUserInput(date: String): LocalDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd MM yyyy HH:mm"))
  def localDateTimeToString(date: LocalDateTime): String = date.toString
}
