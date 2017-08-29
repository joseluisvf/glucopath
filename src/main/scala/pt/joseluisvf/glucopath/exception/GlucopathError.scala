package pt.joseluisvf.glucopath.exception

trait GlucopathError {
  val reason: String

  override def toString: String = reason

  override def equals(obj: scala.Any): Boolean = {
    if(!obj.isInstanceOf[GlucopathError]) false
    else {
      obj.asInstanceOf[GlucopathError].reason == reason
    }
  }
}