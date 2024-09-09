import sbt._

object Dependencies {
  lazy val munit = Def.setting("org.scalameta" %% "munit" % "1.0.0-M10")
  lazy val spark_sql = Def.setting("org.apache.spark" %% "spark-sql" % "3.5.1")
  lazy val spark_core = Def.setting("org.apache.spark" %% "spark-core" % "3.5.1")
}
