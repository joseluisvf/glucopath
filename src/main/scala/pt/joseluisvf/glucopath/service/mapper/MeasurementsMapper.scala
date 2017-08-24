package pt.joseluisvf.glucopath.service.mapper

import measurement.MeasurementsProto
import pt.joseluisvf.glucopath.domain.measurement.Measurements

trait MeasurementsMapper extends EntityMapper[MeasurementsProto, Measurements]
