package com.eurowings.data.sparkservice

import com.typesafe.scalalogging.LazyLogging
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{col, sum}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Encoders, SaveMode, SparkSession}

object SparkService extends App with LazyLogging {

  Logger.getLogger("org").setLevel(Level.ERROR)

  logger.info("starting SparkService...")
  val spark = SparkSession.builder()
    .appName("SparkService")
    .master("local[*]")
    .getOrCreate()

  case class Searches(visitor_id: String, date_time: String, flight_date_outbound: String, origin_out: String,
                      destination_out: String, flight_date_inbound: String, origin_ret: String, destination_ret: String,
                      segments: Int)

  val searchesSchema = Encoders.product[Searches].schema

  case class Visitors(visitor_id: String, visit_start: String, countPerday: Int, country: String, first_hit_pageName: String,
                      hits_avg: Long, logged_in: Int, region: String, registered: Boolean, visits: Int)

  val visitorsSchema = Encoders.product[Visitors].schema

  val searchesDF = spark.readStream.schema(searchesSchema).json("input/searches/")

  val visitorsDF = spark.readStream.schema(visitorsSchema).json("input/visitors/")

  import org.apache.spark.sql.catalyst.ScalaReflection

  val schema = ScalaReflection.schemaFor[Searches].dataType.asInstanceOf[StructType]


  def visitorWriterFunction(batchDF: DataFrame, batchID: Long): Unit = {
    batchDF.persist()
    if (!batchDF.rdd.isEmpty()) {
      batchDF.write.mode("append").parquet("DataLake/visitors")
    }
    batchDF.unpersist()
  }

  def searchesWriterFunction(batchDF: DataFrame, batchID: Long): Unit = {
    batchDF.persist()
    if (!batchDF.rdd.isEmpty()) {
      batchDF.write.mode("append").parquet("DataLake/searches")
    }
    batchDF.unpersist()
  }

  val searchesOutput = searchesDF.writeStream.foreachBatch(searchesWriterFunction _)
    .option("checkpointLocation", "checkpointDir/searches")
    .outputMode("append")
    .trigger(Trigger.ProcessingTime("120 seconds"))
    .start()

  val visitorsOutput = visitorsDF.writeStream.foreachBatch(visitorWriterFunction _)
    .option("checkpointLocation", "checkpointDir/visitors")
    .outputMode("append")
    .trigger(Trigger.ProcessingTime("120 seconds"))
    .start()

  spark.streams.awaitAnyTermination()

  sys.ShutdownHookThread {
    logger.info("Gracefully stopping Spark Streaming Application")
    searchesOutput.stop()
    visitorsOutput.stop()
    spark.stop()
    logger.info("Application stopped")
  }
}
