package pt.joseluisvf.glucopath.service.mapper
import measurement.DayStatisticsProto
import pt.joseluisvf.glucopath.domain.day.DayStatistics

import scala.collection.mutable.ListBuffer

object DayStatisticsMapperImpl extends DayStatisticsMapper {
  override def toEntity(proto: DayStatisticsProto): DayStatistics = {
    DayStatistics(proto.numberOfMeasurements, proto.insulinTotal, proto.glucoseTotal, proto.carbohydratesTotal)
  }

  override def toProto(entity: DayStatistics): DayStatisticsProto = {
    DayStatisticsProto(entity.numberOfMeasurements, entity.insulinTotal, entity.glucoseTotal, entity.carboHydratesTotal)
  }

  override def toEntities(protos: ListBuffer[DayStatisticsProto]): ListBuffer[DayStatistics] = {
    val result = scala.collection.mutable.ListBuffer.empty[DayStatistics]

    for (proto <- protos) {
      result += toEntity(proto)
    }

    result
  }

  override def toProtos(entities: ListBuffer[DayStatistics]): ListBuffer[DayStatisticsProto] = {
    val result = scala.collection.mutable.ListBuffer.empty[DayStatisticsProto]

    for (entity <- entities) {
      result += toProto(entity)
    }

    result
  }
}
