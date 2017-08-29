package pt.joseluisvf.glucopath.presentation.util

import java.time.LocalDateTime

import measurement.{DiabeticProfileProto, MeasurementProto, SlowInsulinProto, UserProto}
import pt.joseluisvf.glucopath.domain.day.SlowInsulin
import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, WarningLevel}
import pt.joseluisvf.glucopath.domain.user.User
import pt.joseluisvf.glucopath.domain.util.DateParser
import pt.joseluisvf.glucopath.exception.{DiabeticProfileError, SlowInsulinError, SlowInsulinException}
import pt.joseluisvf.glucopath.service.impl.UserServiceImpl
import pt.joseluisvf.glucopath.service.mapper.{MeasurementMapperImpl, SlowInsulinMapperImpl, UserMapperImpl}

object CreateMenu extends GlucopathMenu {
  private val OPTION_1 = "1"
  private val OPTION_1_TEXT = "Add Measurement"
  private val OPTION_2 = "2"
  private val OPTION_2_TEXT = "Calculate Insulin to Administer"
  private val OPTION_3 = "3"
  private val OPTION_3_TEXT = "Alter Diabetic Profile"
  private val OPTION_4 = "4"
  private val OPTION_4_TEXT = "Add slow insulin information"


  override protected var availableOptions: String =
    DisplayOptions.getSmallSeparator +
      "Create Menu:\n" +
      DisplayOptions.getSmallSeparator +
      s"$OPTION_0 - $OPTION_0_TEXT\n" +
      s"$OPTION_1 - $OPTION_1_TEXT\n" +
      s"$OPTION_2 - $OPTION_2_TEXT\n" +
      s"$OPTION_3 - $OPTION_3_TEXT\n" +
      s"$OPTION_4 - $OPTION_4_TEXT\n" +
      DisplayOptions.getSeparator + "\n"

  override protected def executeOption(option: String): Boolean = {
    option match {
      case OPTION_0 => false;
      case OPTION_1 => updateUser(collectAndAddMeasurement()); println(DisplayOptions.getSeparator); true
      case OPTION_2 => calculateInsulinToAdminister() match {
        case Some(u) => updateUser(u)
        case _ =>
      }
        println(DisplayOptions.getSeparator)
        true

      case OPTION_3 => alterDiabeticProfile() match {
        case Some(u) => updateUser(u)
        case _ =>
      }
        println(DisplayOptions.getSeparator)
        true

      case OPTION_4 => addSlowInsulinInformation() match {
        case Some(u) => updateUser(u)
        case _ =>
      }
        println(DisplayOptions.getSeparator)
        true

      case _ => UserFeedbackHandler.displayInformationalMessage("Invalid Option; please pick again"); true
    }
  }

  private def collectAndAddMeasurement(): User = {
    val toAdd: Measurement = collectMeasurementFromUser()
    addMeasurement(toAdd)
  }

  private def addMeasurement(toAdd: Measurement): User = {
    val measurementProto: MeasurementProto = MeasurementMapperImpl.toProto(toAdd)
    var userProto: UserProto = UserMapperImpl.toProto(user)

    userProto = UserServiceImpl.addMeasurement(userProto, measurementProto)

    UserFeedbackHandler.displaySuccessMessage("Measurement added with success!")
    UserMapperImpl.toEntity(userProto)
  }

  final private def collectMeasurementFromUser(): Measurement = {
    println("[>       ]")
    val glucose: Int = getNonNegativeMeasurement("glucose")
    println("[->      ]")
    val date: LocalDateTime = getDateTime
    println("[-->     ]")
    val beforeOrAfterMeal: BeforeOrAfterMeal = getBeforeOrAfterMeal
    println("[--->    ]")
    val whatWasEaten = requestChoiceFromUser("food?").replace(",", " ")
    println("[---->   ]")
    val carbohydratesEatenInGrams: Int = getNonNegativeMeasurement("how many grams of carbohydrates were eaten?")
    println("[----->  ]")
    val insulinAdministered = getNonNegativeMeasurement("insulin")
    println("[------> ]")
    val comments = requestChoiceFromUser("comments?").replace(",", " ")
    println("[------->]")
    val warningLevel = getWarningLevel

    Measurement(glucose, date, beforeOrAfterMeal, whatWasEaten, carbohydratesEatenInGrams, insulinAdministered, comments, warningLevel)
  }

  final def getNonNegativeMeasurement(kind: String): Int = {
    var glucose: Int = Integer.MIN_VALUE
    var glucoseOption: String = null
    while (glucose < 0) {
      glucoseOption = requestChoiceFromUser(s"$kind?")
      try {
        glucose = Integer.parseInt(glucoseOption)
        if (glucose < 0) {
          glucose = -1
          UserFeedbackHandler.displayErrorMessage(s"$kind value must be non-negative")
        }
      } catch {
        case _: NumberFormatException => UserFeedbackHandler.displayErrorMessage(s"Invalid $kind. Please try again.")
      }
    }
    glucose
  }

  final private def getDateTime: LocalDateTime = {
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
            UserFeedbackHandler.displayErrorMessage(s"Invalid date format!\n ${e.getMessage}\n")
            dateOption = ""
        }
      }
    }
    date
  }

  final private def getBeforeOrAfterMeal: BeforeOrAfterMeal = {
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
            UserFeedbackHandler.displayErrorMessage("before or after meal must be either b or a.")
            null
        }
      }
    }
    beforeOrAfterMeal
  }

  final private def getWarningLevel: WarningLevel = {
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
            UserFeedbackHandler.displayErrorMessage("Invalid warning level")
        }
      }
    }
    warningLevel
  }

  def calculateInsulinToAdminister(): Option[User] = {
    val glucoseMeasured = getNonNegativeMeasurement("glucose measured (mg/dl)")
    val carbohydratesConsumed = getNonNegativeMeasurement("carbohydrates consumed (grams)")
    val insulinToAdminister = user.calculateHowMuchInsulinToAdminister(glucoseMeasured, carbohydratesConsumed)

    UserFeedbackHandler.displayInformationalMessage(s"Given the collected data and your diabetic profile, we recommend you administer\n$insulinToAdminister units of insulin.\n" +
      s"Would you like to create a measurement based on this?")

    val userChoiceCreateMeasurement = requestChoiceFromUser("Y/N\n(press enter for default - yes)")
    userChoiceCreateMeasurement.toUpperCase() match {
      case "" | "Y" =>
        val toAdd: Measurement = collectPartialMeasurementFromUser(glucoseMeasured, carbohydratesConsumed, insulinToAdminister)
        val modifiedUser: User = addMeasurement(toAdd)
        UserFeedbackHandler.displaySuccessMessage("Measurement added with success.")
        Some(modifiedUser)
      case _ => None
    }
  }

  private def collectPartialMeasurementFromUser(glucoseMeasured: Int, carbohydratesConsumed: Int, insulinToAdminister: Int) = {
    println("[>   ]")
    val date: LocalDateTime = getDateTime
    println("[->  ]")
    val beforeOrAfterMeal: BeforeOrAfterMeal = BeforeOrAfterMeal.BEFORE_MEAL
    val whatWasEaten = requestChoiceFromUser("food?").replace(",", " ")
    println("[--> ]")
    val comments = requestChoiceFromUser("comments?").replace(",", " ")
    println("[--->]")
    val warningLevel = getWarningLevel

    Measurement(glucoseMeasured, date, beforeOrAfterMeal, whatWasEaten, carbohydratesConsumed, insulinToAdminister, comments, warningLevel)
  }

  def alterDiabeticProfile(): Option[User] = {
    val glucoseMitigationPerInsulinUnit: Int = getNonNegativeMeasurement("glucose mitigation")
    val minimumGlucoseRange: Int = getNonNegativeMeasurement("minimum glucose")
    val maximumGlucoseRange: Int = getNonNegativeMeasurement("maximum glucose")
    println("Carbohydrate mitigation per insulin unit (in grams)?\ne.g. 1 unit of insulin mitigates 12 gr of carbohydrates")
    val carbohydrateMitigation = getNonNegativeMeasurement("carbohydrate mitigation?")

    val newDiabeticProfileProto: DiabeticProfileProto =
      DiabeticProfileProto(
        glucoseMitigationPerInsulinUnit,
        minimumGlucoseRange,
        maximumGlucoseRange,
        carbohydrateMitigation)

    val userProto: UserProto = UserMapperImpl.toProto(user)
    val eitherAltereredUser: Either[DiabeticProfileError, UserProto] = UserServiceImpl.alterDiabeticProfile(userProto, newDiabeticProfileProto)

    eitherAltereredUser match {
      case Left(error) => UserFeedbackHandler.displayErrorMessage(error.reason); None
      case Right(u) =>
        UserFeedbackHandler.displaySuccessMessage("Diabetic Profile altered with success.")
        Some(UserMapperImpl.toEntity(u));
    }
  }

  def addSlowInsulinInformation(): Option[User] = {
    val date: LocalDateTime = getDateTime
    val amount: Int = getNonNegativeMeasurement("amount of slow insulin")

    try {
      val slowInsulin: SlowInsulin = SlowInsulin(date.toLocalTime, amount)
      val slowInsulinProto: SlowInsulinProto = SlowInsulinMapperImpl.toProto(slowInsulin)


      val userProto: UserProto = UserMapperImpl.toProto(user)
      UserServiceImpl.alterSlowInsulin(userProto, slowInsulinProto, date) match {
        case Left(error) => UserFeedbackHandler.displayErrorMessage(error.reason); None
        case Right(u) =>
          UserFeedbackHandler.displaySuccessMessage("Slow insulin altered with success.")
          Some(UserMapperImpl.toEntity(u))
      }
    } catch {
      case sie: SlowInsulinException =>
        UserFeedbackHandler.displayErrorMessage(sie.getGlucopathError.reason)
        None
    }
  }
}
