package pt.joseluisvf.glucopath.service.mapper

import measurement.UserProto
import pt.joseluisvf.glucopath.domain.user.User

trait UserMapper extends EntityMapper[UserProto, User]
