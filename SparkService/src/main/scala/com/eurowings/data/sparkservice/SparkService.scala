package com.eurowings.data.sparkservice

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession

object SparkService extends App with LazyLogging{

  logger.info("starting SparkService...")
  val spark = SparkSession.builder().appName("SparkService").master("local[*]").getOrCreate()

  spark.sql("select 1").show()
}
