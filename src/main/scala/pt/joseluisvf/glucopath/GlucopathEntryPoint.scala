package pt.joseluisvf.glucopath

import pt.joseluisvf.glucopath.domain.day.Days
import pt.joseluisvf.glucopath.domain.user.{DiabeticProfile, User}
import pt.joseluisvf.glucopath.persistence.GlucopathIO
import pt.joseluisvf.glucopath.presentation.util.{ErrorHandler, MainMenu}


object GlucopathEntryPoint extends App {
  var adam: User = loadUserOrCreateNewOne()
  var optionSelected = ""

  println(s"Welcome to Glucopath ${adam.name}\n\n")

  MainMenu.loopExecution(adam)


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
    val name = requestChoiceFromUser("Your name?")
    val days = Days()
    val diabeticProfile = createDiabeticProfile()
    new User(name, days, diabeticProfile)
  }

  def createDiabeticProfile(): DiabeticProfile = {
    println("Glucose mitigation per insulin unit (in mg/dl)?\ne.g. 50mg/dl per 1 unit of insulin")
    val glucoseMitigation = getNonNegativeMeasurement("glucose mitigation")
    val idealGlucoseRangeMinimum = getNonNegativeMeasurement("minimum glucose?")
    val idealGlucoseRangeMaximum = getNonNegativeMeasurement("maximum glucose?")
    println("Carbohydrate mitigation per insulin unit (in grams)?\ne.g. 1 unit of insulin mitigates 12 gr of carbohydrates")
    val carbohydrateMitigation = getNonNegativeMeasurement("carbohydrate mitigation?")
    DiabeticProfile(glucoseMitigation, (idealGlucoseRangeMinimum, idealGlucoseRangeMaximum), carbohydrateMitigation)
  }

  def updateUser(newUser: User): Unit = {
    adam = newUser
  }

  final private def requestChoiceFromUser(prompt: String): String = {
    println(prompt)
    scala.io.StdIn.readLine()

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
          ErrorHandler.displayErrorMessage(s"$kind value must be non-negative")
        }
      } catch {
        case _: NumberFormatException => ErrorHandler.displayErrorMessage(s"Invalid $kind. Please try again.")
      }
    }
    glucose
  }
}