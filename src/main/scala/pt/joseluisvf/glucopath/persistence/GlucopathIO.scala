package pt.joseluisvf.glucopath.persistence

import java.io._
import java.nio.file.{Files, Paths}

import com.google.protobuf.InvalidProtocolBufferException
import measurement.UserProto
import pt.joseluisvf.glucopath.domain.user.User
import pt.joseluisvf.glucopath.presentation.util.UserFeedbackHandler
import pt.joseluisvf.glucopath.service.mapper.UserMapperImpl

object GlucopathIO {
  val persistenceFileLocation = "src/main/resources/glucopath_user_data.txt"
  val fileBackupLocation = "src/../../backup_data/glucopath_user_data_backup.txt"
  val measurementsFileLocation = "src/main/resources/measurements.csv"
  val measurementsFileBackupLocation = "src/../../backup_data/measurements.csv"
  val metricsFileLocation = "src/main/resources/metrics.csv"
  val metricsFileBackupLocation = "src/../../backup_data/metrics.csv"

  def saveUserToFile(toSave: User): Unit = {
    val writer = new FileOutputStream(persistenceFileLocation)
    val toWrite = UserMapperImpl.toProto(toSave)
    toWrite.writeTo(writer)
    writer.close()
    try {
      val backupWriter = new FileOutputStream(fileBackupLocation)
      toWrite.writeTo(backupWriter)
      backupWriter.close()
    } catch {
      case e: FileNotFoundException =>
        UserFeedbackHandler.displayErrorMessage("There was an issue with writing to the backup location:" + e.getMessage)
    }
  }

  def loadUserFromFile(): Option[User] = {
    try {
      val reader = new FileInputStream(persistenceFileLocation)
      val userProto = UserProto.parseFrom(reader)
      reader.close()
      Some(UserMapperImpl.toEntity(userProto))
    } catch {
      case _: InvalidProtocolBufferException =>
        Files.deleteIfExists(Paths.get(persistenceFileLocation))
        None
      case _: FileNotFoundException => None
    }
  }

  def saveMeasurementsToFile(measurements: String, pathToFile: String = measurementsFileLocation): Unit = {
    val writer = new PrintWriter(new File(pathToFile))
    val toWrite = "glucose,date,before or after meal,what was eaten,carbohydrates eaten in grams,insulin administered,comments,warning level\n" + measurements
    writer.write(toWrite)
    writer.close()

    try {
      val backupWriter = new PrintWriter(new File(measurementsFileBackupLocation))
      backupWriter.write(toWrite)
      backupWriter.close()
    } catch {
      case e: FileNotFoundException =>
        UserFeedbackHandler.displayErrorMessage("There was an issue with writing to the backup location:" + e.getMessage)
    }
  }

  def saveMetricsToFile(metrics: String, pathToFile: String = metricsFileLocation): Unit = {
    val writer = new PrintWriter(new File(pathToFile))
    writer.write(metrics)
    writer.close()

    try {
      val backupWriter = new PrintWriter(new File(metricsFileBackupLocation))
      backupWriter.write(metrics)
      backupWriter.close()
    } catch {
      case e: FileNotFoundException =>
        UserFeedbackHandler.displayErrorMessage("There was an issue with writing to the backup location:" + e.getMessage)
    }
  }
}
