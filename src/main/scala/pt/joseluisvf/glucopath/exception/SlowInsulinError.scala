package pt.joseluisvf.glucopath.exception

import pt.joseluisvf.glucopath.domain.day.SlowInsulin

sealed trait SlowInsulinError extends GlucopathError

final case class AmountOutsideReasonableBoundsError(amount: Int) extends SlowInsulinError {
  override val reason: String = s"The provided amount <$amount]> should be within reasonable bounds [${SlowInsulin.MINIMUM_AMOUNT} - ${SlowInsulin.MAXIMUM_AMOUNT}]"
}