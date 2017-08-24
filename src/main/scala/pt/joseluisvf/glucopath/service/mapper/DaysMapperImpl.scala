package pt.joseluisvf.glucopath.service.mapper
import measurement.DaysProto
import pt.joseluisvf.glucopath.domain.day.Days

import scala.collection.mutable.ListBuffer

object DaysMapperImpl extends DaysMapper {
  override def toEntity(proto: DaysProto): Days = {
    val days = DayMapperImpl.toEntities(proto.days.to[ListBuffer])
    Days(days)
  }

  override def toProto(entity: Days): DaysProto = {
    val daysProto = DayMapperImpl.toProtos(entity.days)
    DaysProto(daysProto)
  }

  override def toEntities(protos: ListBuffer[DaysProto]): ListBuffer[Days] = {
    val result = scala.collection.mutable.ListBuffer.empty[Days]

    for (proto <- protos) {
      result += toEntity(proto)
    }

    result
  }

  override def toProtos(entities: ListBuffer[Days]): ListBuffer[DaysProto] = {
    val result = scala.collection.mutable.ListBuffer.empty[DaysProto]

    for (entity <- entities) {
      result += toProto(entity)
    }

    result
  }
}
