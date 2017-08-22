name := "glucopath"

version := "1.0"

scalaVersion := "2.12.1"


// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.12
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP9" % "test"


// scoverage
// https://mvnrepository.com/artifact/org.scoverage/scalac-scoverage-plugin_2.11
libraryDependencies += "org.scoverage" % "scalac-scoverage-plugin_2.12" % "1.3.0" % "provided"
coverageExcludedPackages := ".*GlucopathEntryPoint.*;.*ErrorHandler.*;.*GlucopathMenu.*; "