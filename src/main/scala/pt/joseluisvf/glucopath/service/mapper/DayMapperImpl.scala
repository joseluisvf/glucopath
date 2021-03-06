package pt.joseluisvf.glucopath.service.mapper
import java.util.UUID

import measurement.DayProto
import pt.joseluisvf.glucopath.domain.day.Day
import pt.joseluisvf.glucopath.domain.util.DateParser

import scala.collection.mutable.ListBuffer

object DayMapperImpl extends DayMapper {
  override def toEntity(proto: DayProto): Day = {
    val dayStatistics = DayStatisticsMapperImpl.toEntity(proto.dayStatistics)
    val measurements = MeasurementsMapperImpl.toEntity(proto.measurements)
    val date = DateParser.toLocalDate(proto.date)
    val id = UUID.fromString(proto.id)

    val result: Day = Day(dayStatistics, measurements, date, id)

    proto.slowInsulin match {
      case Some(slowProto) => result.slowInsulin_(SlowInsulinMapperImpl.toEntity(slowProto))
      case _ => result
    }
  }

  override def toProto(entity: Day): DayProto = {
    val dayStatistics = DayStatisticsMapperImpl.toProto(entity.dayStatistics)
    val measurements = MeasurementsMapperImpl.toProto(entity.measurements)
    val date = DateParser.localDateToString(entity.date)
    val id = entity.id.toString

    val result: DayProto = DayProto(dayStatistics, measurements, date, id)

    if (!entity.slowInsulin.isDefault) {
      val slowInsulinProto = SlowInsulinMapperImpl.toProto(entity.slowInsulin)
      result.withSlowInsulin(slowInsulinProto)
    } else {
      result
    }
  }

  override def toEntities(protos: ListBuffer[DayProto]): ListBuffer[Day] = {
    val result = scala.collection.mutable.ListBuffer.empty[Day]

    for (proto <- protos) {
      result += toEntity(proto)
    }

    result
  }

  override def toProtos(entities: ListBuffer[Day]): ListBuffer[DayProto] = {
    val result = scala.collection.mutable.ListBuffer.empty[DayProto]

    for (entity <- entities) {
      result += toProto(entity)
    }

    result
  }
}
