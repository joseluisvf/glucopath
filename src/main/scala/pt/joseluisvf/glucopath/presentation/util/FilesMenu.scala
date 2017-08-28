package pt.joseluisvf.glucopath.presentation.util
import java.io.FileNotFoundException

import measurement.UserProto
import pt.joseluisvf.glucopath.domain.user.User
import pt.joseluisvf.glucopath.service.impl.UserServiceImpl
import pt.joseluisvf.glucopath.service.mapper.UserMapperImpl

object FilesMenu extends GlucopathMenu{
  val OPTION_1 = "1"
  val OPTION_1_TEXT = "Import measurements from CSV file"
  val OPTION_2 = "2"
  val OPTION_2_TEXT = "Export measurements to file"
  val OPTION_3 = "3"
  val OPTION_3_TEXT = "Export metrics to file"

  override protected var availableOptions: String =
    DisplayOptions.getSmallSeparator +
      "Files Menu:\n" +
      DisplayOptions.getSmallSeparator +
      s"$OPTION_0 - $OPTION_0_TEXT\n" +
      s"$OPTION_1 - $OPTION_1_TEXT\n" +
      s"$OPTION_2 - $OPTION_2_TEXT\n" +
      s"$OPTION_3 - $OPTION_3_TEXT\n" +
      DisplayOptions.getSeparator + "\n"

  override protected def executeOption(option: String): Boolean = {
    option match {
      case OPTION_0 => false
      case OPTION_1 =>
        importMeasurementsFromCsvFile() match {
          case Some(constructedUser) => updateUser(constructedUser)
          case None =>
        }
        println(DisplayOptions.getSeparator); true
      case OPTION_2 => exportMeasurementsToFile(); println(DisplayOptions.getSeparator); true
      case OPTION_3 => exportMetricsToFile(); println(DisplayOptions.getSeparator); true
      case _ => println("Invalid Option; please pick again"); true
    }
  }

  def importMeasurementsFromCsvFile(): Option[User] = {
    val pathToFile = requestChoiceFromUser("Absolute path to CSV file?")
    println("Importing measurements...")
    try {
      val constructedUser: User = FileMeasurementsLoader.importMeasurementsFrom(pathToFile, user)
      UserServiceImpl.saveUserToFile(constructedUser)
      println("Measurements imported with success. User state has been saved.")
      Some(constructedUser)
    } catch {
      case _: FileNotFoundException =>
        ErrorHandler.displayErrorMessage(s"The provided path <$pathToFile> does not correspond to an existing file")
        None
    }
  }

  def exportMeasurementsToFile(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    println("Exporting measurements...")
    UserServiceImpl.exportMeasurements(userProto)
    println("Measurements imported with success.")
  }

  def exportMetricsToFile(): Unit = {
    val userProto: UserProto = UserMapperImpl.toProto(user)
    println("Exporting metrics...")
    UserServiceImpl.writeMetricsPerTimePeriod(userProto)
    println("Metrics imported with success.")
  }
}
