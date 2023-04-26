ThisBuild / version := "0.1.0-SNAPSHOT"

val appName = "spark-jobs-scala"
val sparkProvided = Option(System.getenv("SPARK_PROVIDED")).getOrElse("false").toBoolean

lazy val commonSettings = List(
  name := appName,
  organization := "com.brenntag",
  scalaVersion := "2.12.15",
  assembly / assemblyJarName := s"$appName-${version.value}.jar",
  assemblyMergeStrategy := {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case "application.conf"                            => MergeStrategy.concat
    case "unwanted.txt"                                => MergeStrategy.discard
    case PathList("application.properties") => MergeStrategy.discard
    case PathList("application.release.properties") => new RenameConfigMergeStrategy()
    case x =>
      val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
      oldStrategy(x)
  }
)

val hadoopVersion = "3.3.1"
val sparkVersion = "3.3.0"

val sparkDependencies = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" % "spark-sql-kafka-0-10_2.12" % sparkVersion,
  "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
  "org.apache.hadoop" % "hadoop-common" % hadoopVersion,
  "com.amazonaws" % "aws-java-sdk-bundle" % "1.12.272"
)

val awsSDKv2Version = "2.17.204"
val icebergVersion = "0.14.0"

val icebergDependencies = Seq(
  "org.apache.iceberg" %% "iceberg-spark-runtime-3.3" % icebergVersion,
  "software.amazon.awssdk" % "bundle" % awsSDKv2Version ,
  "software.amazon.awssdk" % "url-connection-client" % awsSDKv2Version
)

val appDependencies = Seq(
  "com.github.scopt" %% "scopt" % "4.0.1",
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
)

val normalizedProvidedDependencies = if (sparkProvided)
  sparkDependencies.map(d => d % "provided") ++ icebergDependencies.map(d => d % "provided")
else
  sparkDependencies ++ icebergDependencies

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= normalizedProvidedDependencies ++ appDependencies
  )