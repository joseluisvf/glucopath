package pt.joseluisvf.glucopath.domain.user

import java.time.LocalTime

import pt.joseluisvf.glucopath.domain.user.UserStatistics.DayStatus.DayStatus

import scala.collection.mutable.ListBuffer

object UserStatistics {

  sealed case class PartialMeasurement(time: LocalTime, glucose: Int, carbohydratesEatenInGrams: Int)

  sealed case class PartialMeasurements
  (
    partialMeasurements: ListBuffer[PartialMeasurement] = ListBuffer.empty[PartialMeasurement],
    var totalPartialMeasurements: Int = 0,
    var totalGlucose: Int = 0,
    var totalCarbohydratesEatenInGrams: Int = 0
  ) {

    def add(toAdd: PartialMeasurement): Unit = {
      partialMeasurements += toAdd
      totalPartialMeasurements += 1
      totalGlucose += toAdd.glucose
      totalCarbohydratesEatenInGrams += toAdd.carbohydratesEatenInGrams
    }

    def getGlucoseAverage: Float =
      if (totalGlucose == 0) 0
      else totalGlucose.asInstanceOf[Float] / totalPartialMeasurements


    def getCarbohydratesAverage: Float =
      if (totalCarbohydratesEatenInGrams == 0) 0
      else totalCarbohydratesEatenInGrams.asInstanceOf[Float] / totalPartialMeasurements
  }

  sealed case class Metric
  (
    dayStatus: DayStatus,
    glucoseAverage: Float,
    carbohydratesAverage: Float,
    hipoglicemiasCount: Int,
    hiperglicemiasCount: Int,
    measurementsInRangeCount: Int
  )

  sealed case class Metrics(metrics: ListBuffer[Metric] = ListBuffer.empty[Metric])

  object DayStatus extends Enumeration {
    type DayStatus = Value
    val HIGH, LOW, NORMAL, NO_MEASUREMENTS = Value
  }

  def showMetricsPerTimePeriod(user: User): String = {
    val metrics: Array[Metric] = calculateMetrics(user)
    metricsToString(metrics)
  }

  private def calculateMetrics(user: User): Array[Metric] = {

    val partialMeasurements: ListBuffer[PartialMeasurement] =
      user.getAllMeasurements
        .measurements
        .map(measurement => PartialMeasurement(measurement.date.toLocalTime, measurement.glucose, measurement.carbohydratesEatenInGrams))
    val partitionedMeasurements: Array[PartialMeasurements] = partitionMeasurementsByTime(partialMeasurements)

    partitionedMeasurements.map(takeMetricFromPartialMeasurements(user, _))
  }

  def getMetricsAsCsv(user: User): String = {
    val metrics: Array[Metric] = calculateMetrics(user)
    "from,to,status,glucoseAverage,carbohydratesAverage,totalMeasurements,hipoglicemias,hiperglicemias,measurementsInRange\n" +
    metricsToCsv(metrics)
  }

  def metricsToCsv(metrics: Array[Metric]): String = {
    // schema is as follows
    // from,to,status,glucoseAverage,carbohydratesAverage,totalMeasurements,hipoglicemias,hiperglicemias,measurementsInRange
    var result = ""

    var currentMetric = metrics(0)
    result += s"00:01,03:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(1)
    result += s"03:01,06:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(2)
    result += s"06:01,09:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(3)
    result += s"09:01,12:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(4)
    result += s"12:01,15:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(5)
    result += s"15:01,18:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(6)
    result += s"18:01,21:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"
    result += "\n"

    currentMetric = metrics(7)
    result += s"21:01,00:00,${currentMetric.dayStatus},${currentMetric.glucoseAverage}," +
      s"${currentMetric.carbohydratesAverage}," +
      s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount}," +
      s"${currentMetric.hipoglicemiasCount},${currentMetric.hiperglicemiasCount},${currentMetric.measurementsInRangeCount}"

    result
  }

  private def metricsToString(metrics: Array[Metric]) = {
    var currentMetric = metrics(0)
    var result =
      s"Metrics for the period [00:01 - 03:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}," +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(1)
    result +=
      s"\nMetrics for the period [03:01 - 06:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(2)
    result +=
      s"\nMetrics for the period [06:01 - 09:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(3)
    result +=
      s"\nMetrics for the period [09:01 - 12:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(4)
    result +=
      s"\nMetrics for the period [12:01 - 15:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(5)
    result +=
      s"\nMetrics for the period [15:01 - 18:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(6)
    result +=
      s"\nMetrics for the period [18:01 - 21:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    currentMetric = metrics(7)
    result +=
      s"\nMetrics for the period [21:01 - 00:00]," +
        s"DayStatus:${currentMetric.dayStatus}," +
        s" GlucoseAverage: ${currentMetric.glucoseAverage}," +
        s" CarbohydratesAverage: ${currentMetric.carbohydratesAverage}" +
        s" Total measurements:(Total Hipo Hiper InRange) " +
        s"${currentMetric.hipoglicemiasCount + currentMetric.hiperglicemiasCount + currentMetric.measurementsInRangeCount} " +
        s"${currentMetric.hipoglicemiasCount} " +
        s"${currentMetric.hiperglicemiasCount} " +
        s"${currentMetric.measurementsInRangeCount},"

    result
  }

  private def partitionMeasurementsByTime(partialMeasurements: ListBuffer[PartialMeasurement]) = {
    val result: Array[PartialMeasurements] =
      Array(
        PartialMeasurements(), // 00:01 - 03:00
        PartialMeasurements(), // 03:01 - 06:00
        PartialMeasurements(), // 06:01 - 09:00
        PartialMeasurements(), // 09:01 - 12:00
        PartialMeasurements(), // 12:01 - 15:00
        PartialMeasurements(), // 15:01 - 18:00
        PartialMeasurements(), // 18:01 - 21:00
        PartialMeasurements()) // 21:01 - 00:00

    val midnight = LocalTime.of(0, 0)
    val threeAM = LocalTime.of(3, 0)
    val sixAM = LocalTime.of(6, 0)
    val nineAM = LocalTime.of(9, 0)
    val noon = LocalTime.of(12, 0)
    val threePM = LocalTime.of(15, 0)
    val sixPM = LocalTime.of(18, 0)
    val ninePM = LocalTime.of(21, 0)

    partialMeasurements.foreach {
      case pm if (pm.time isAfter midnight) && (pm.time isBefore threeAM) => result(0).add(pm)
      case pm if (pm.time isAfter threeAM) && (pm.time isBefore sixAM) => result(1).add(pm)
      case pm if (pm.time isAfter sixAM) && (pm.time isBefore nineAM) => result(2).add(pm)
      case pm if (pm.time isAfter nineAM) && (pm.time isBefore noon) => result(3).add(pm)
      case pm if (pm.time isAfter noon) && (pm.time isBefore threePM) => result(4).add(pm)
      case pm if (pm.time isAfter threePM) && (pm.time isBefore sixPM) => result(5).add(pm)
      case pm if (pm.time isAfter sixPM) && (pm.time isBefore ninePM) => result(6).add(pm)
      case pm if pm.time isAfter ninePM => result(7).add(pm)
      case _ => // do nothing; shouldn't have any hits.
    }

    result
  }

  private def takeMetricFromPartialMeasurements(user: User, partialMeasurements: PartialMeasurements): Metric = {
    var hipoglicemiaCount = 0
    var hiperglicemiaCount = 0
    var measurementInRangeCount = 0

    partialMeasurements.partialMeasurements.foreach {
      case pm if user.diabeticProfile.isGlucoseHypoglycemia(pm.glucose) => hipoglicemiaCount += 1
      case pm if user.diabeticProfile.isGlucoseHyperglycemia(pm.glucose) => hiperglicemiaCount += 1
      case _ => measurementInRangeCount += 1
    }

    val highestCount = Math.max(Math.max(hipoglicemiaCount, hiperglicemiaCount), measurementInRangeCount)
    val dayStatus = highestCount match {
      case 0 => DayStatus.NO_MEASUREMENTS
      case c if c == measurementInRangeCount => DayStatus.NORMAL
      case c if c == hiperglicemiaCount => DayStatus.HIGH
      case _ => DayStatus.LOW
    }

    Metric(
      dayStatus,
      partialMeasurements.getGlucoseAverage,
      partialMeasurements.getCarbohydratesAverage,
      hipoglicemiaCount,
      hiperglicemiaCount,
      measurementInRangeCount)
  }
}
