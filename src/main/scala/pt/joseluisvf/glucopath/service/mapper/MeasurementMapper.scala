package pt.joseluisvf.glucopath.service.mapper

import measurement.MeasurementProto
import pt.joseluisvf.glucopath.domain.measurement.Measurement

trait MeasurementMapper extends EntityMapper[MeasurementProto, Measurement]
