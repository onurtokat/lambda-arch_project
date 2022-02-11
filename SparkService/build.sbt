
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.eurowings.data",
      version := "0.1.0",
      scalaVersion := "2.12.2",
      assemblyJarName in assembly := "SparkService.jar"
    )),
    name := "SparkService",
    libraryDependencies ++= List(
      "org.scalatest" %% "scalatest" % "3.1.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "org.apache.spark" %% "spark-sql" % "3.2.0",
      "org.apache.kafka"           % "kafka-clients"            % "1.1.1",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.2.0"
      //"org.apache.kafka"           % "kafka-streams"            % "2.5.0",
      //"org.apache.kafka"           %% "kafka-streams-scala"     % "2.5.0"
    )
  )
assemblyMergeStrategy in assembly := {
  case PathList("META-INF","services",xs @ _*) => MergeStrategy.filterDistinctLines
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}