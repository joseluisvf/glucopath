package pt.joseluisvf.glucopath.exception

abstract class GlucopathException(glucopathError: GlucopathError) extends RuntimeException(glucopathError.reason) {
  def getGlucopathError = glucopathError
}
