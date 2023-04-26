import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.functions.{col, explode, from_json, to_date}
import schemas.BusinessSchema
object Main {

  def transform(kafkaTopic: String): Unit = {

    val spark = SparkSession.builder
      .appName("MyApp")
      .config("spark.sql.streaming.forceDeleteTempCheckpointLocation", "true")
      .getOrCreate()

    val customers = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("s3a://demo-data/Customers.csv")

    val customersFilteredNotNull =
      customers.where(col("specialized_industries").isNotNull)

    val customersExploded =
      customersFilteredNotNull
        .select(
          col("customer_id"),
          functions
            .split(col("specialized_industries"), ";")
            .as("specialized_industries")
        )
        .select(
          col("customer_id"),
          explode(col("specialized_industries")).as("industry")
        )

    val kafkaRecordsOrders = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("subscribe", kafkaTopic)
      .load()

    val streamRecordsWithSchema =
      kafkaRecordsOrders.select(
        from_json(col("value").cast("string"), BusinessSchema.orderSchema)
          .alias("value")
      )

    val ordersImportantInformation = streamRecordsWithSchema.select(
      col("value.order_id"),
      col("value.customer_id"),
      col("value.amount"),
      col("value.timestamp"),
      to_date(col("value.timestamp")).as("date")
    )

    val ordersEnhancedWithIndustry =
      ordersImportantInformation.join(
        customersExploded,
        usingColumns = Seq("customer_id"),
        joinType = "left_outer"
      )

    val ordersNotMatched =
      ordersEnhancedWithIndustry.where(col("industry").isNull)

    val ordersMatched =
      ordersEnhancedWithIndustry.where(col("industry").isNotNull)

    val sink = ordersMatched.writeStream
      .format("parquet")
      .option("path", "s3a://dat/orders_industry/")
      .option("checkpointLocation", "s3a://dat/orders_industry_checkpoint/")
      .partitionBy("date")
      .start()

    sink.awaitTermination()
  }

  def main(arg: Array[String]): Unit = {
    transform("orders")
  }

}
