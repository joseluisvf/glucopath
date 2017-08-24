package pt.joseluisvf.glucopath.service.mapper
import measurement.DiabeticProfileProto
import pt.joseluisvf.glucopath.domain.user.DiabeticProfile

import scala.collection.mutable.ListBuffer


object DiabeticProfileMapperImpl extends DiabeticProfileMapper {
  override def toEntity(proto: DiabeticProfileProto): DiabeticProfile = {
    val minimumRange = proto.idealGlucoseRangeMinimum
    val maximumRange = proto.idealGlucoseRangeMaximum

    DiabeticProfile(
      proto.glucoseMitigationPerInsulinUnit,
      (minimumRange, maximumRange),
      proto.carbohydrateMitigationPerInsulinUnit)
  }

  override def toProto(entity: DiabeticProfile): DiabeticProfileProto = {
    DiabeticProfileProto(
      entity.glucoseMitigationPerInsulinUnit,
      entity.idealGlucoseRange._1,
      entity.idealGlucoseRange._2,
      entity.carbohydrateMitigationPerInsulinUnit
    )
  }

  override def toEntities(protos: ListBuffer[DiabeticProfileProto]): ListBuffer[DiabeticProfile] = {
    val entities = scala.collection.mutable.ListBuffer.empty[DiabeticProfile]

    for (proto <- protos) {
        entities += toEntity(proto)
    }

    entities
  }

  override def toProtos(entities: ListBuffer[DiabeticProfile]): ListBuffer[DiabeticProfileProto] = {
    val protos = scala.collection.mutable.ListBuffer.empty[DiabeticProfileProto]

    for (entity <- entities) {
      protos += toProto(entity)
    }

    protos
  }
}
