package pt.joseluisvf.glucopath.service.mapper

import scala.collection.mutable.ListBuffer

trait EntityMapper[D, E] {
  def toEntity(proto: D): E
  def toProto(entity: E): D
  def toEntities(protos: ListBuffer[D]): ListBuffer[E]
  def toProtos(entities: ListBuffer[E]): ListBuffer[D]
}
