package pt.joseluisvf.glucopath.service.mapper
import measurement.UserProto
import pt.joseluisvf.glucopath.domain.user.User

import scala.collection.mutable.ListBuffer

object UserMapperImpl extends UserMapper {
  override def toEntity(proto: UserProto): User = {
    val days = DaysMapperImpl.toEntity(proto.days)
    val diabeticProfile = DiabeticProfileMapperImpl.toEntity(proto.diabeticProfile)

    new User(proto.name, days, diabeticProfile)
  }

  override def toProto(entity: User): UserProto = {
    val daysProto = DaysMapperImpl.toProto(entity.days)
    val diabeticProfileProto = DiabeticProfileMapperImpl.toProto(entity.diabeticProfile)

    UserProto(entity.name, daysProto, diabeticProfileProto)
  }

  override def toEntities(protos: ListBuffer[UserProto]): ListBuffer[User] = {
    val result = scala.collection.mutable.ListBuffer.empty[User]

    for (proto <- protos) {
      result += toEntity(proto)
    }

    result
  }

  override def toProtos(entities: ListBuffer[User]): ListBuffer[UserProto] = {
    val result = scala.collection.mutable.ListBuffer.empty[UserProto]

    for (entity <- entities) {
      result += toProto(entity)
    }

    result
  }
}
