package pt.joseluisvf.glucopath.service.mapper
import measurement.{MeasurementProto, MeasurementsProto}
import pt.joseluisvf.glucopath.domain.measurement.Measurements

import scala.collection.mutable.ListBuffer

object MeasurementsMapperImpl extends MeasurementsMapper {
  override def toEntity(proto: MeasurementsProto): Measurements = {
    val measurements = MeasurementMapperImpl.toEntities(proto.measurements.to[ListBuffer])
    Measurements(measurements)
  }

  override def toProto(entity: Measurements): MeasurementsProto = {
    val measurements = scala.collection.mutable.ListBuffer.empty[MeasurementProto]
    for (measurement <- entity.measurements) {
      measurements += MeasurementMapperImpl.toProto(measurement)
    }

    MeasurementsProto(measurements)
  }

  override def toEntities(protos: ListBuffer[MeasurementsProto]): ListBuffer[Measurements] = {
    val measurements = scala.collection.mutable.ListBuffer.empty[Measurements]
    for (proto <- protos) {
      measurements += toEntity(proto)
    }

    measurements
  }

  override def toProtos(entities: ListBuffer[Measurements]): ListBuffer[MeasurementsProto] = {
    val measurements = scala.collection.mutable.ListBuffer.empty[MeasurementsProto]
    for (entity <- entities) {
      measurements += toProto(entity)
    }

    measurements
  }
}
