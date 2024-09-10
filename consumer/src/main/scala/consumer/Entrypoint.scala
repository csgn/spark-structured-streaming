package consumer

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger

object Entrypoint extends App {
  override def main(args: Array[String]): Unit = {
    ///////////////////////
    // • Spark Setup    //
    /////////////////////
    val spark = SparkSession.builder
      .appName("Spark Streaming - Consumer App")
      .master("local[3]")
      .getOrCreate()

    import spark.implicits._
    val sc = spark.sparkContext

    //////////////////////
    // • Input Source  //
    ////////////////////
    val streamDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "raw-apache-logs")
      .load()

    /////////////////////////////
    // • Data Transformation  //
    ///////////////////////////

    val logRegex =
      raw"""(\S+) (\S+) (\S+) \[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{6})\] (\S+) (\S+) (\S+) (\S+) (\S+)""".r

    val extractedDF = streamDF
      .withColumn("value", regexp_extract(col("value"), logRegex.toString, 4))
      .withColumn(
        "date",
        to_timestamp(col("value"), "yyyy-MM-dd HH:mm:ss.SSSSSS")
      )

    val resultDF = extractedDF
      .withColumn("day", date_format(col("date"), "yyyy-MM-dd"))
      .groupBy("day")
      .count()

    ///////////////////////
    // • Output Sink    //
    /////////////////////
    val outputDir = "./outputs/"
    val checkpointDir = "./checkpoints/"

    val writer = resultDF.writeStream
      .format("console")
      .trigger(Trigger.ProcessingTime("1 second"))
      .outputMode("complete")
      .option("checkpointLocation", checkpointDir)

    val streamingQuery = writer.start()

    ///////////////////
    // • Tidy up    //
    /////////////////
    streamingQuery.awaitTermination()
    streamingQuery.stop()
    spark.stop()
  }
}
