package pt.joseluisvf.glucopath.domain.day

import java.time.LocalTime

import pt.joseluisvf.glucopath.exception.{AmountOutsideReasonableBoundsError, SlowInsulinException}

case class SlowInsulin(time: LocalTime, amount: Int) {
  def isDefault: Boolean = SlowInsulin.isDefault(this)

  require(isAmountWithinBounds, throw new SlowInsulinException(AmountOutsideReasonableBoundsError(amount)))

  private def isAmountWithinBounds =
    SlowInsulin.MINIMUM_AMOUNT <= amount && amount <= SlowInsulin.MAXIMUM_AMOUNT
}

case object SlowInsulin {
  val MINIMUM_AMOUNT: Int = 0
  val MAXIMUM_AMOUNT: Int = 60
  val DEFAULT_TIME: LocalTime = LocalTime.of(0, 0)
  val DEFAULT_AMOUNT: Int = 0

  def isDefault(slowInsulin: SlowInsulin): Boolean = slowInsulin.amount == DEFAULT_AMOUNT && slowInsulin.time == DEFAULT_TIME

  def apply(): SlowInsulin = new SlowInsulin(DEFAULT_TIME, DEFAULT_AMOUNT)
}
