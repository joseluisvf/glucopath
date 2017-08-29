package pt.joseluisvf.glucopath.exception

import java.time.LocalDate

sealed trait DayError extends GlucopathError

final case class DayWithDateDoesNotExistError(date: LocalDate) extends DayError {
  override val reason: String = s"Could not find a day for the provided date <$date> ."
}
