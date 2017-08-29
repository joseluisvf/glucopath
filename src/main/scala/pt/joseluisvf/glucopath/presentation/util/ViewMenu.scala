package pt.joseluisvf.glucopath.presentation.util

import java.time.LocalDate

import measurement.{DayProto, UserProto}
import pt.joseluisvf.glucopath.domain.measurement.Measurements
import pt.joseluisvf.glucopath.service.impl.UserServiceImpl
import pt.joseluisvf.glucopath.service.mapper.{DayMapperImpl, UserMapperImpl}

object ViewMenu extends GlucopathMenu {
  val OPTION_1 = "1"
  val OPTION_1_TEXT = "View measurements of today"
  val OPTION_2 = "2"
  val OPTION_2_TEXT = "View measurements of another today"
  val OPTION_3 = "3"
  val OPTION_3_TEXT = "View all measurements"
  val OPTION_4 = "4"
  val OPTION_4_TEXT = "Show overall data"
  val OPTION_5 = "5"
  val OPTION_5_TEXT = "Show metrics per time period"

  override protected var availableOptions: String =
    DisplayOptions.getSmallSeparator +
      "View Menu:\n" +
      DisplayOptions.getSmallSeparator +
      s"$OPTION_0 - $OPTION_0_TEXT\n" +
      s"$OPTION_1 - $OPTION_1_TEXT\n" +
      s"$OPTION_2 - $OPTION_2_TEXT\n" +
      s"$OPTION_3 - $OPTION_3_TEXT\n" +
      s"$OPTION_4 - $OPTION_4_TEXT\n" +
      s"$OPTION_5 - $OPTION_5_TEXT\n" +
      DisplayOptions.getSeparator + "\n"

  override def executeOption(option: String): Boolean = {
    option match {
      case OPTION_0 => false
      case OPTION_1 => viewMeasurementsOfToday(); println(DisplayOptions.getSeparator); true
      case OPTION_2 => viewMeasurementsOfAnotherDay(); println(DisplayOptions.getSeparator); true
      case OPTION_3 => viewAllMeasurements(); println(DisplayOptions.getSeparator); true
      case OPTION_4 => showOverallInfo(); println(DisplayOptions.getSeparator); true
      case OPTION_5 => showMetricsPerTimePeriod(); println(DisplayOptions.getSeparator); true
      case _ => UserFeedbackHandler.displayErrorMessage("Invalid Option; please pick again"); true
    }
  }

  private def viewMeasurementsOfToday(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val todayDate = LocalDate.now().toString
    val maybeTodayProto: Option[DayProto] = UserServiceImpl.getDayByDate(userProto, todayDate)

    maybeTodayProto match {
      case Some(d) =>
        UserFeedbackHandler.displaySuccessMessage(s"Here are the measurements for today (${LocalDate.now}):")
        val today = DayMapperImpl.toEntity(d)
        println(today)
      case _ =>
        UserFeedbackHandler.displayErrorMessage(s"No measurements were found for today(${LocalDate.now}).")
    }
  }

  private def viewMeasurementsOfAnotherDay(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val desiredDate: LocalDate = getDate
    val maybeTodayProto: Option[DayProto] = UserServiceImpl.getDayByDate(userProto, desiredDate.toString)

    maybeTodayProto match {
      case Some(d) =>
        UserFeedbackHandler.displaySuccessMessage(s"Here are the measurements for the date ($desiredDate):")
        val today = DayMapperImpl.toEntity(d)
        println(today)
      case _ =>
        UserFeedbackHandler.displayErrorMessage(s"No measurements were found for the date($desiredDate).")
    }
  }

  private def viewAllMeasurements(): Unit = {
    val measurements: Measurements = user.getAllMeasurements
    println(measurements)
  }

  private def showOverallInfo(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val result = UserServiceImpl.getOverallInfo(userProto)
    println(result)
  }

  private def showMetricsPerTimePeriod(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    val result = UserServiceImpl.showMetricsPerTimePeriod(userProto)
    println(result)
  }
}
