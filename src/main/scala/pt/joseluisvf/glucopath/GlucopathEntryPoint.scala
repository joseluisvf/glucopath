package pt.joseluisvf.glucopath

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import pt.joseluisvf.glucopath.domain.day.Days
import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, Measurements, WarningLevel}
import pt.joseluisvf.glucopath.domain.user.{DiabeticProfile, User}
import pt.joseluisvf.glucopath.presentation.util.ErrorHandler

object GlucopathEntryPoint extends App {
  println("Welcome to glucopath!\n\n")
  val adam = new User("Papu", Days(), DiabeticProfile())

  var optionSelected = ""

  while (true) {
    GlucopathMenu.showAvailableOptions()
    optionSelected = GlucopathMenu.requestChoiceFromUser()
    GlucopathMenu.executeOption(optionSelected, adam)
  }
}

object GlucopathMenu {
  //  add measurement
  // get measurement
  // list measurements by day
  // list measurements of this day
  // list all measurements
  def showAvailableOptions(): Unit = {
    println(
      "1 - add measurement\n" +
        "2 - view today's measurements\n" +
        "3 - view another day's measurements\n" +
        "4 - view a single measurement\n" +
        "5 - view all measurements\n" +
        "6 - exit"
    )

    showSeparator()
  }

  def requestChoiceFromUser(): String = {
    println("Please pick an option")
    scala.io.StdIn.readLine()

  }

  def requestChoiceFromUser(prompt: String): String = {
    println(prompt)
    scala.io.StdIn.readLine()

  }

  def showSeparator(): Unit = println("_________________________")

  def executeOption(option: String, user: User): Unit = {
    option match {
      case "1" => addMeasurement(user)
      case "2" => viewTodaysMeasurements(user)
      case "3" => viewAnotherDaysMeasurements()
      case "4" => viewSingleMeasurement()
      case "5" => viewAllMeasurements(user)
      case "6" => System.exit(1)
      case _ => println("Invalid Option; please pick again")
    }
  }

  private def addMeasurement(user: User): Unit = {
    val measurement: Measurement = collectMeasurementFromUser()
    user.addMeasurement(measurement)
  }

  private def collectMeasurementFromUser(): Measurement = {
    println("[>      ]")
    val glucose: Int = getNonNegativeMeasurement("glucose")
    println("[->     ]")
    val date: LocalDateTime = getDate
    println("[-->    ]")
    val beforeOrAfterMeal: BeforeOrAfterMeal = getBeforeOrAfterMeal
    println("[--->   ]")
    val whatWasEaten = requestChoiceFromUser("food?")
    println("[---->  ]")
    val insulinAdministered = getNonNegativeMeasurement("insulin")
    println("[-----> ]")
    val comments = requestChoiceFromUser("comments?")
    println("[------>]")
    val warningLevel = getWarningLevel

    Measurement(glucose, date, beforeOrAfterMeal, whatWasEaten, insulinAdministered, comments, warningLevel)
  }

  private def getNonNegativeMeasurement(kind: String): Int = {
    var glucose: Int = Integer.MIN_VALUE
    var glucoseOption: String = null
    while (glucose < 0) {
      glucoseOption = requestChoiceFromUser(s"$kind?")
      try {
        glucose = Integer.parseInt(glucoseOption)
        if (glucose < 0) {
          glucose = -1
          ErrorHandler.displayErrorMessage(s"$kind value must be non-negative")
        }
      } catch {
        case _: NumberFormatException => ErrorHandler.displayErrorMessage(s"Invalid $kind. Please try again.")
      }
    }
    glucose
  }

  private def getDate: LocalDateTime = {
    var date: LocalDateTime = null
    var dateOption: String = null

    while (date == null) {
      dateOption = requestChoiceFromUser("date? <dd MM yyyy HH:mm> \n(press enter for default - now)")
      if (dateOption == "") {
        date = LocalDateTime.now()
      } else {
        try {
          date = LocalDateTime.parse(dateOption, DateTimeFormatter.ofPattern("dd MM yyyy HH:mm"))
        } catch {
          case e: Exception =>
            ErrorHandler.displayErrorMessage(s"Invalid date format!\n ${e.getMessage}\n")
            dateOption = ""
        }
      }
    }
    date
  }

  private def getBeforeOrAfterMeal: BeforeOrAfterMeal = {
    var beforeOrAfterMeal: BeforeOrAfterMeal = null
    var beforeOrAfterMealOption: String = null

    while (beforeOrAfterMeal == null) {
      beforeOrAfterMealOption = requestChoiceFromUser("before or after meal? <b/a> \n(press enter for default - before)")
      if (beforeOrAfterMealOption == "") {
        beforeOrAfterMeal = BeforeOrAfterMeal.BEFORE_MEAL
      } else {
        beforeOrAfterMeal = beforeOrAfterMealOption match {
          case "b" => BeforeOrAfterMeal.BEFORE_MEAL
          case "a" => BeforeOrAfterMeal.AFTER_MEAL
          case _ =>
            ErrorHandler.displayErrorMessage("before or after meal must be either b or a.")
            null
        }
      }

    }
    beforeOrAfterMeal
  }

  private def getWarningLevel: WarningLevel = {
    var warningLevelOption: String = null
    var warningLevel: WarningLevel = null

    while (warningLevel == null) {
      warningLevelOption = requestChoiceFromUser("Warning Level? <green / yellow / red> \n(press enter for default - green).")
      if (warningLevelOption == "") {
        warningLevel = WarningLevel.GREEN
      } else {
        try {
          warningLevel = WarningLevel.withName(warningLevelOption.toUpperCase())
        } catch {
          case _: Exception =>
            ErrorHandler.displayErrorMessage("Invalid warning level")
        }
      }
    }
    warningLevel
  }

  def viewTodaysMeasurements(user: User): Unit = {
    val todaysMeasurements = user.getTodayMeasurements
    todaysMeasurements match {
      case Some(measurements) =>
        println(s"Here are the measurements for today (${LocalDate.now}):\n")
        println(measurements)
        showSeparator()
      case _ => ErrorHandler.displayErrorMessage("No measurements were found for today.")
    }
  }

  // TODO all measurements are being added to the same day!!!

  def viewAnotherDaysMeasurements(): Unit = {

  }

  def viewSingleMeasurement(): Unit = {

  }

  def viewAllMeasurements(user: User): Unit = {
    val measurements: Measurements = user.getAllMeasurements
    println(measurements)
  }
}
