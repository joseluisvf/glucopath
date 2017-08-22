package pt.joseluisvf.glucopath.domain.measurement

object WarningLevel extends Enumeration {
  type WarningLevel = Value
  val GREEN, YELLOW, RED = Value

  var defaultWarningLevel = this.GREEN
}
