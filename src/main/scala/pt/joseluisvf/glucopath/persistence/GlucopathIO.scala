package pt.joseluisvf.glucopath.persistence

import java.io._
import java.nio.file.{Files, Paths}

import com.google.protobuf.InvalidProtocolBufferException
import measurement.UserProto
import pt.joseluisvf.glucopath.domain.user.User
import pt.joseluisvf.glucopath.service.mapper.UserMapperImpl

object GlucopathIO {
  private val persistenceFileLocation = "src/main/resources/glucopath_user_data.txt"
  private val fileBackupLocation = "src/main/resources/glucopath_user_data_backup.txt"
  private val measurementsFileLocation = "src/main/resources/measurements.csv"

  def saveUserToFile(toSave: User): Unit = {
    val writer = new FileOutputStream(persistenceFileLocation)
    val toWrite = UserMapperImpl.toProto(toSave)
    toWrite.writeTo(writer)
    writer.close()

    val backupWriter = new FileOutputStream(fileBackupLocation)
    toWrite.writeTo(backupWriter)
    backupWriter.close()
  }

  def loadUserFromFile(): Option[User] = {
    try {
      val reader = new FileInputStream(persistenceFileLocation)
      val userProto = UserProto.parseFrom(reader)
      reader.close()
      Some(UserMapperImpl.toEntity(userProto))
    } catch {
      case _ : InvalidProtocolBufferException =>
        Files.deleteIfExists(Paths.get(persistenceFileLocation))
        None
      case _: FileNotFoundException => None
    }
  }

  def saveMeasurementsToFile(measurements: String): Unit = {
    val writer = new PrintWriter(new File(measurementsFileLocation))
    writer.write(measurements)
    writer.close()
  }
}
