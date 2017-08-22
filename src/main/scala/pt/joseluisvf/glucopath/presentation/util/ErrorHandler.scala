package pt.joseluisvf.glucopath.presentation.util

object ErrorHandler {
  def displayErrorMessage(message: String): Unit = {
    println(s"<ERROR> $message <ERROR>")
  }
}
