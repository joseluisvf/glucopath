package pt.joseluisvf.glucopath.exception

sealed trait UserError extends GlucopathError

final case class DiabeticProfileNotDefinedError() extends UserError {
  override val reason: String = "Cannot create a user without having defined its diabetic profile."
}
