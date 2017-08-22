package pt.joseluisvf.glucopath.exception

import java.util.UUID

class MeasurementDoesNotExistException(id: UUID)
  extends GlucopathException(s"Could not find a measurement for the provided id $id")
