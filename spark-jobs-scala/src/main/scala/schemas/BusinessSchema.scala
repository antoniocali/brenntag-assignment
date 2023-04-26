package schemas

import org.apache.spark.sql.types.{
  StructType,
  ArrayType,
  StructField,
  StringType,
  IntegerType,

  DoubleType,
  TimestampType
}

object BusinessSchema {
  val orderSchema: StructType = StructType(
    Seq(
      StructField("order_id", StringType, nullable = false),
      StructField("customer_id", StringType, nullable = false),
      StructField(
        "order_lines",
        ArrayType(
          StructType(
            Array(
              StructField("product_id", StringType, nullable = false),
              StructField("volume", IntegerType, nullable = false),
              StructField("price", DoubleType, nullable = false)
            )
          )
        )
      ),
      StructField("amount", DoubleType, nullable = false),
      StructField("timestamp", TimestampType, nullable = false)
    )
  )

}
