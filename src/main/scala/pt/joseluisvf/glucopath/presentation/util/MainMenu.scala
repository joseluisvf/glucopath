package pt.joseluisvf.glucopath.presentation.util

import pt.joseluisvf.glucopath.domain.user.User

object MainMenu extends GlucopathMenu {
  private val OPTION_1 = "1"
  private val OPTION_1_TEXT = "View Menu"
  private val OPTION_2 = "2"
  private val OPTION_2_TEXT = "Files Menu"
  private val OPTION_3 = "3"
  private val OPTION_3_TEXT = "Measurement Menu"

  override protected var availableOptions: String =
    DisplayOptions.getSmallSeparator +
      "Main Menu:\n" +
      DisplayOptions.getSmallSeparator +
      s"$OPTION_0 - $OPTION_0_TEXT\n" +
      s"$OPTION_1 - $OPTION_1_TEXT\n" +
      s"$OPTION_2 - $OPTION_2_TEXT\n" +
      s"$OPTION_3 - $OPTION_3_TEXT\n" +
      DisplayOptions.getSeparator + "\n"

  override def executeOption(option: String): Boolean = {
    option match {
      case OPTION_0 => false;
      case OPTION_1 => updateUser(showViewMenu()); println(DisplayOptions.getSeparator); true
      case OPTION_2 => updateUser(showFilesMenu()); println(DisplayOptions.getSeparator); true
      case OPTION_3 => updateUser(showMeasurementMenu()); println(DisplayOptions.getSeparator); true
      case _ => println("Invalid Option; please pick again"); true
    }
  }

  private def showViewMenu(): User = {
    ViewMenu.loopExecution(user)
  }

  private def showFilesMenu(): User = {
    FilesMenu.loopExecution(user)
  }

  private def showMeasurementMenu(): User = {
    MeasurementMenu.loopExecution(user)
  }
}
