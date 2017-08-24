package pt.joseluisvf.glucopath.service.mapper

import measurement.DayStatisticsProto
import pt.joseluisvf.glucopath.domain.day.DayStatistics

trait DayStatisticsMapper extends EntityMapper[DayStatisticsProto, DayStatistics]
