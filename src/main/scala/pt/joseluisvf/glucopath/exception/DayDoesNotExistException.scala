package pt.joseluisvf.glucopath.exception

import java.time.LocalDate

class DayDoesNotExistException(date: LocalDate)
  extends GlucopathException(s"Could not find a day for the provided date $date")
