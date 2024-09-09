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
    val resultDF = streamDF
      .selectExpr(
        "cast(value as string) as log"
      )

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
