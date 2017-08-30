name := "glucopath"

version := "1.0"

scalaVersion := "2.12.1"


// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.12
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP9" % "test"


// scoverage
// https://mvnrepository.com/artifact/org.scoverage/scalac-scoverage-plugin_2.11
libraryDependencies += "org.scoverage" % "scalac-scoverage-plugin_2.12" % "1.3.0" % "provided"
coverageExcludedPackages :=
  """
    |.*GlucopathEntryPoint.*;
    |.*ErrorHandler.*;
    |.*GlucopathMenu.*;
    |.*Menu.*;
    | pt.joseluisvf.glucopath.presentation.util.*;
    | service.mapper.*;exception.*;
    | pt.joseluisvf.glucopath.persistence.*;
    | pt.joseluisvf.glucopath.service.mapper.*;
    | measurement.*;
    | pt.joseluisvf.glucopath.domain.user.UserStatistics.*;
    | """.stripMargin

// jackson
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.0"

// avro
// https://mvnrepository.com/artifact/org.apache.avro/avro
libraryDependencies += "org.apache.avro" % "avro" % "1.8.1"

// https://mvnrepository.com/artifact/com.sksamuel.avro4s/avro4s-core_2.11
libraryDependencies += "com.sksamuel.avro4s" % "avro4s-core_2.11" % "1.7.0"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)




