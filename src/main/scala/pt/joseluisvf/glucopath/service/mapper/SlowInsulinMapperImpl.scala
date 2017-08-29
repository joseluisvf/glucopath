package pt.joseluisvf.glucopath.service.mapper
import java.time.LocalTime

import measurement.SlowInsulinProto
import pt.joseluisvf.glucopath.domain.day.SlowInsulin
import pt.joseluisvf.glucopath.domain.util.DateParser

import scala.collection.mutable.ListBuffer

object SlowInsulinMapperImpl extends SlowInsulinMapper{
  override def toEntity(proto: SlowInsulinProto): SlowInsulin = {
    val time: LocalTime = DateParser.toLocalTime(proto.date)
    val amount: Int = proto.amount

    SlowInsulin(time, amount)
  }

  override def toProto(entity: SlowInsulin): SlowInsulinProto = {
    SlowInsulinProto(entity.time.toString, entity.amount)
  }

  override def toEntities(protos: ListBuffer[SlowInsulinProto]): ListBuffer[SlowInsulin] = {
    val entities = scala.collection.mutable.ListBuffer.empty[SlowInsulin]

    for (proto <- protos) {
      entities += toEntity(proto)
    }

    entities
  }

  override def toProtos(entities: ListBuffer[SlowInsulin]): ListBuffer[SlowInsulinProto] = {
    val protos = scala.collection.mutable.ListBuffer.empty[SlowInsulinProto]

    for (entity <- entities) {
      protos += toProto(entity)
    }

    protos
  }
}
