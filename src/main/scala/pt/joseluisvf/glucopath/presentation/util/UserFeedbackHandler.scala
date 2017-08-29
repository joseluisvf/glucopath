package pt.joseluisvf.glucopath.presentation.util

object UserFeedbackHandler {
  def displaySuccessMessage(message: String): Unit = {
    println(s"<SUCCESS> $message <SUCCESS>")
  }

  def displayErrorMessage(message: String): Unit = {
    println(s"<ERROR> $message <ERROR>")
  }

  def displayInformationalMessage(message: String): Unit = {
    println(s"$message")
  }
}
