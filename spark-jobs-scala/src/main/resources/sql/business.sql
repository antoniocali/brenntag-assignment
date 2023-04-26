-- CREATE HIVE TABLE - NOT TESTED
CREATE EXTERNAL TABLE Orders (
	order_id STRING,
	customer_id STRING,
	`timestamp` timestamp,
	`date` date,
	amount DOUBLE,
	industry STRING
) STORED AS PARQUET LOCATION s3a://del/orders_industry/ PARTITION BY `DATE`;


-- SQL 30 DAYS -- not tested on HIVE
select industry, avg(amount)
from Orders
where `date` >= date_sub(CURRENT_DATE, 30)

-- SQL 24 hours -- not tested on HIVE
select industry, avg(amount)
from Orders
where `timestamp` >= from_unixtime(current_timestamp - (3600 * 24));

-- FULL SQL QUERY TOP 3 -- not tested
SELECT history.industry, abs(history.amount_avg - daily.amount_avg) as difference
FROM (select industry, avg(amount) as amount_avg
      from Orders
      where `date` >= date_sub(CURRENT_DATE, 30)) as HISTORY
JOIN (select industry, avg(amount) as amount_avg
      from Orders
      where `timestamp` >= from_unixtime(current_timestamp - (3600 * 24))) as DAILY
ON HISTORY.industry = DAILY.industry
order by difference desc
limit 3;


