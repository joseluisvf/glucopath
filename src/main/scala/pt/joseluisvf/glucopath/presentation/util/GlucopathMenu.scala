package pt.joseluisvf.glucopath.presentation.util

import java.time.LocalDate

import pt.joseluisvf.glucopath.domain.user.User
import pt.joseluisvf.glucopath.domain.util.DateParser

trait GlucopathMenu {

  protected val OPTION_0 = "0"
  protected val OPTION_0_TEXT = "Exit"
  protected var user: User = _

  final def loopExecution(user: User): User = {
    var selectedOption: String = ""
    var userWantsToStay: Boolean = true
    updateUser(user)

    do {
      showAvailableOptions()
      selectedOption = requestChoiceFromUser()
      userWantsToStay = executeOption(selectedOption)
    } while (userWantsToStay)

    this.user
  }

  final def requestChoiceFromUser(): String = {
    println("Please pick an option")
    scala.io.StdIn.readLine()
  }

  final def requestChoiceFromUser(prompt: String): String = {
    println(prompt)
    scala.io.StdIn.readLine()

  }

  final def getDate: LocalDate = {
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

  private final def showAvailableOptions(): Unit = println(availableOptions)

  protected final def updateUser(updatedUser: User): Unit = {
    this.user = updatedUser
  }

  protected var availableOptions: String

  protected def executeOption(option: String): Boolean

}