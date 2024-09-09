import Dependencies._

/* project settings */
ThisBuild / scalaVersion := "2.12.13"
ThisBuild / name := "consumer"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = project
    .in(file("."))
    .settings(
        name := (ThisBuild / name).value,
        libraryDependencies ++= {
            Seq(
                spark_sql.value,
                spark_core.value,
            )
        }
    )
