package pt.joseluisvf.glucopath

import java.io.FileNotFoundException
import java.time.{LocalDate, LocalDateTime}

import measurement.{DayProto, MeasurementProto, UserProto}
import pt.joseluisvf.glucopath.domain.day.Days
import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, Measurements, WarningLevel}
import pt.joseluisvf.glucopath.domain.user.{DiabeticProfile, User}
import pt.joseluisvf.glucopath.domain.util.DateParser
import pt.joseluisvf.glucopath.persistence.GlucopathIO
import pt.joseluisvf.glucopath.presentation.util.{DisplayOptions, ErrorHandler, FileMeasurementsLoader}
import pt.joseluisvf.glucopath.service.impl.UserServiceImpl
import pt.joseluisvf.glucopath.service.mapper.{DayMapperImpl, MeasurementMapperImpl, UserMapperImpl}


object GlucopathEntryPoint extends App {
  var adam: User = loadUserOrCreateNewOne()
  var optionSelected = ""

  println(s"Welcome to Glucopath ${adam.name}\n\n")


  while (true) {
    GlucopathMenu.showAvailableOptions()
    optionSelected = GlucopathMenu.requestChoiceFromUser()
    GlucopathMenu.executeOption(optionSelected, adam)
  }

  def loadUserOrCreateNewOne(): User = {
    val maybeUser = GlucopathIO.loadUserFromFile()

    maybeUser match {
      case Some(u) => u
      case None =>
        println("We could not find a file with user information.\nPlease answer the following questions in order to create a new user.")
        val user: User = createUser()
        GlucopathIO.saveUserToFile(user)
        user
    }
  }

  def createUser(): User = {
    val name = GlucopathMenu.requestChoiceFromUser("Your name?")
    val days = Days()
    val diabeticProfile = createDiabeticProfile()
    new User(name, days, diabeticProfile)
  }

  def createDiabeticProfile(): DiabeticProfile = {
    println("Glucose mitigation per insulin unit (in mg/dl)?\ne.g. 50mg/dl per 1 unit of insulin")
    val glucoseMitigation = GlucopathMenu.getNonNegativeMeasurement("glucose mitigation")
    val idealGlucoseRangeMinimum = GlucopathMenu.getNonNegativeMeasurement("minimum glucose?")
    val idealGlucoseRangeMaximum = GlucopathMenu.getNonNegativeMeasurement("maximum glucose?")
    println("Carbohydrate mitigation per insulin unit (in grams)?\ne.g. 1 unit of insulin mitigates 12 gr of carbohydrates")
    val carbohydrateMitigation = GlucopathMenu.getNonNegativeMeasurement("carbohydrate mitigation?")
    DiabeticProfile(glucoseMitigation, (idealGlucoseRangeMinimum, idealGlucoseRangeMaximum), carbohydrateMitigation)
  }

  def updateUser(newUser: User): Unit = {
    adam = newUser
  }
}

object GlucopathMenu {

  def showAvailableOptions(): Unit = {
    println(
      "1 - add measurement\n" +
        "2 - view today's measurements\n" +
        "3 - view another day's measurements\n" +
        "4 - view all measurements\n" +
        "5 - calculate insulin to administer\n" +
        "6 - import measurements from a CSV file\n" +
        "7 - show overall info\n" +
        "8 - write measurements to file\n" +
        "9 - show metrics per time period\n" +
        "10 - write metrics per time period\n" +
        "11 - exit"
    )

    println(DisplayOptions.getSeparator)
  }

  def requestChoiceFromUser(): String = {
    println("Please pick an option")
    scala.io.StdIn.readLine()

  }

  def requestChoiceFromUser(prompt: String): String = {
    println(prompt)
    scala.io.StdIn.readLine()

  }

  def executeOption(option: String, user: User): Unit = {
    option match {
      case "1" => GlucopathEntryPoint.updateUser(addMeasurement(user)); println(DisplayOptions.getSeparator)
      case "2" => viewTodaysMeasurements(user); println(DisplayOptions.getSeparator)
      case "3" => viewAnotherDaysMeasurements(user); println(DisplayOptions.getSeparator)
      case "4" => viewAllMeasurements(user); println(DisplayOptions.getSeparator)
      case "5" => calculateInsulinToAdminister(user); println(DisplayOptions.getSeparator)
      case "6" => importMeasurementsFromCsvFile(user); GlucopathIO.saveUserToFile(user); println(DisplayOptions.getSeparator)
      case "7" => showOverallInfo(user); println(DisplayOptions.getSeparator)
      case "8" => writeMeasurementsToFile(user); println(DisplayOptions.getSeparator)
      case "9" => showMetricsPerTimePeriod(user); println(DisplayOptions.getSeparator)
      case "10" => writeMetricsPerTimePeriod(user); println(DisplayOptions.getSeparator)
      case "11" => System.exit(1)
      case _ => println("Invalid Option; please pick again")
    }
  }

  private def addMeasurement(user: User): User = {
    val measurement: Measurement = collectMeasurementFromUser()
    val measurementProto: MeasurementProto = MeasurementMapperImpl.toProto(measurement)
    var userProto: UserProto = UserMapperImpl.toProto(user)

    userProto = UserServiceImpl.addMeasurement(userProto, measurementProto)

    UserMapperImpl.toEntity(userProto)
  }

  private def collectMeasurementFromUser(): Measurement = {
    println("[>       ]")
    val glucose: Int = getNonNegativeMeasurement("glucose")
    println("[->      ]")
    val date: LocalDateTime = getDateTime
    println("[-->     ]")
    val beforeOrAfterMeal: BeforeOrAfterMeal = getBeforeOrAfterMeal
    println("[--->    ]")
    val whatWasEaten = requestChoiceFromUser("food?")
    println("[---->   ]")
    val carbohydratesEatenInGrams: Int = getNonNegativeMeasurement("how many grams of carbohydrates were eaten?")
    println("[----->  ]")
    val insulinAdministered = getNonNegativeMeasurement("insulin")
    println("[------> ]")
    val comments = requestChoiceFromUser("comments?")
    println("[------->]")
    val warningLevel = getWarningLevel

    Measurement(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, warningLevel)
  }

  def getNonNegativeMeasurement(kind: String): Int = {
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

  private def getDateTime: LocalDateTime = {
    var date: LocalDateTime = null
    var dateOption: String = null

    while (date == null) {
      dateOption = requestChoiceFromUser("date? <dd MM yyyy HH:mm> \n(press enter for default - now)")
      if (dateOption == "") {
        date = LocalDateTime.now()
      } else {
        try {
          date = DateParser.toLocalDateTimeFromUserInput(dateOption)
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
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val todayDate = LocalDate.now().toString
    val maybeTodayProto: Option[DayProto] = UserServiceImpl.getDayByDate(userProto, todayDate)

    maybeTodayProto match {
      case Some(d) =>
        println(s"Here are the measurements for today (${LocalDate.now}):\n")
        val today = DayMapperImpl.toEntity(d)
        println(today)
      case _ =>
        ErrorHandler.displayErrorMessage(s"No measurements were found for today(${LocalDate.now}).")
    }
  }

  def viewAnotherDaysMeasurements(user: User): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val desiredDate: LocalDate = getDate
    val maybeTodayProto: Option[DayProto] = UserServiceImpl.getDayByDate(userProto, desiredDate.toString)

    maybeTodayProto match {
      case Some(d) =>
        println(s"Here are the measurements for the date ($desiredDate):\n")
        val today = DayMapperImpl.toEntity(d)
        println(today)
      case _ =>
        ErrorHandler.displayErrorMessage(s"No measurements were found for the date($desiredDate).")
    }
  }

  private def getDate: LocalDate = {
    var date: LocalDate = null
    var dateOption: String = null

    while (date == null) {
      dateOption = requestChoiceFromUser("date? <dd MM yyyy>")
      try {
        date = DateParser.toLocalDateFromUserInput(dateOption)
      } catch {
        case e: Exception =>
          ErrorHandler.displayErrorMessage(s"Invalid date format!\n ${e.getMessage}\n")
          dateOption = ""
      }
    }
    date
  }

  def viewAllMeasurements(user: User): Unit = {
    val measurements: Measurements = user.getAllMeasurements
    println(measurements)
  }

  def calculateInsulinToAdminister(user: User): Unit = {
    val glucoseMeasured = getNonNegativeMeasurement("glucose measured (mg/dl)")
    val carbohydratesConsumed = getNonNegativeMeasurement("carbohydrates consumed (grams)")
    val result = user.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesConsumed)

    println(s"Given the collected data and your diabetic profile, we recommend you administer\n$result units of insulin.\n")
  }

  def importMeasurementsFromCsvFile(user: User): Unit = {
    val pathToFile = requestChoiceFromUser("Absolute path to CSV file?")
    println("Importing measurements...")
    try {
      FileMeasurementsLoader.importMeasurementsFrom(pathToFile, user)
      println("Measurements imported with success.")
    } catch {
      case _: FileNotFoundException =>
        ErrorHandler.displayErrorMessage(s"The provided path <$pathToFile> does not correspond to an existing file")
    }
  }

  def showOverallInfo(user: User): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val result = UserServiceImpl.getOverallInfo(userProto)
    println(result)
  }

  def writeMeasurementsToFile(user: User): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    println("Exporting measurements...")
    UserServiceImpl.exportMeasurements(userProto)
    println("Measurements imported with success.")
  }

  def showMetricsPerTimePeriod(user: User): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val result = UserServiceImpl.showMetricsPerTimePeriod(userProto)
    println(result)
  }

  def writeMetricsPerTimePeriod(user: User): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    println("Exporting metrics...")
    UserServiceImpl.writeMetricsPerTimePeriod(userProto)
    println("Metrics imported with success.")
  }
}
