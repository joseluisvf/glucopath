package pt.joseluisvf.glucopath.service.mapper

import measurement.SlowInsulinProto
import pt.joseluisvf.glucopath.domain.day.SlowInsulin

trait SlowInsulinMapper extends EntityMapper[SlowInsulinProto, SlowInsulin]
