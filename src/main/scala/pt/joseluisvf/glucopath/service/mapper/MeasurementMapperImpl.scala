package pt.joseluisvf.glucopath.service.mapper

import java.time.LocalDateTime
import java.util.UUID

import measurement.MeasurementProto
import pt.joseluisvf.glucopath.domain.measurement.BeforeOrAfterMeal.BeforeOrAfterMeal
import pt.joseluisvf.glucopath.domain.measurement.WarningLevel.WarningLevel
import pt.joseluisvf.glucopath.domain.measurement.{BeforeOrAfterMeal, Measurement, WarningLevel}
import pt.joseluisvf.glucopath.domain.util.DateParser

import scala.collection.mutable.ListBuffer

object MeasurementMapperImpl extends MeasurementMapper {
  override def toEntity(proto: MeasurementProto): Measurement = {
    def glucose: Int = proto.glucose

    def date: LocalDateTime = DateParser.toLocalDateTime(proto.date)

    def beforeOrAfterMeal: BeforeOrAfterMeal = BeforeOrAfterMeal.withName(proto.beforeOrAfterMeal.toString())

    def whatWasEaten: String = proto.whatWasEaten

    def carbohydratesEatenInGrams: Int = proto.carbohydratesEatenInGrams

    def insulinAdministered: Int = proto.insulinAdministered

    def comments: String = proto.comments.getOrElse("no comments")

    def warningLevel: WarningLevel = WarningLevel.withName(proto.warningLevel.toString())

    def id: UUID = UUID.fromString(proto.id)

    Measurement.makeMeasurementWithId(
      glucose,
      date,
      beforeOrAfterMeal,
      whatWasEaten,
      carbohydratesEatenInGrams,
      insulinAdministered,
      comments,
      warningLevel,
      id)
  }

  override def toProto(entity: Measurement): MeasurementProto = {
    def glucose: Int = entity.glucose

    def date: String = DateParser.localDateTimeToString(entity.date)

    def beforeOrAfterMeal: MeasurementProto.BeforeOrAfterMeal =
      MeasurementProto.BeforeOrAfterMeal.fromName(entity.beforeOrAfterMeal.toString).get

    def whatWasEaten: String = entity.whatWasEaten

    def carbohydratesEatenInGrams: Int = entity.carbohydratesEatenInGrams

    def insulinAdministered: Int = entity.insulinAdministered

    def comments: String = entity.comments

    def warningLevel: MeasurementProto.WarningLevel = {
      val entityWarningLevel = entity.warningLevel.toString

      val maybeWarningLevel = MeasurementProto.WarningLevel.fromName(entityWarningLevel)
      maybeWarningLevel.getOrElse(MeasurementProto.WarningLevel.GREEN)
    }

    def id: String = entity.id.toString

    MeasurementProto(
      glucose,
      date,
      beforeOrAfterMeal,
      whatWasEaten,
      carbohydratesEatenInGrams,
      insulinAdministered,
      Some(comments),
      warningLevel,
      id)
  }

  override def toEntities(protos: ListBuffer[MeasurementProto]): ListBuffer[Measurement] = {
    val result = ListBuffer.empty[Measurement]
    for (proto <- protos) {
      result += toEntity(proto)
    }
    result
  }

  override def toProtos(entities: ListBuffer[Measurement]): ListBuffer[MeasurementProto] = {
    val result = ListBuffer.empty[MeasurementProto]
    for (entity <- entities) {
      result += toProto(entity)
    }
    result
  }
}
