# Introduction

So, I had few problems with setting up the project itself that lead me to give up on trying to adjust for time constraints.

My Spark Job inside `Main.scala` didn't work for me, it seems I couldn't let connect somehow correctly to HDFS.
The Issue that was raised all the time is

```console
Exception in thread "main" org.apache.hadoop.ipc.RpcException: RPC response exceeds maximum data length
```

Even If I tried to fixed for a while, I gave up to be able at least to "finish" the project.

What you will find

### Scala Streaming 
A Scala Streaming App that:

1. Read from Minio the Customers.csv and exploded
2. Read from Kafka Topic orders
3. Joins the Dataframe and sink the result as a parquet

### SQL Queries

SQL Queries that "should" being able to answer the question of the assignment.
It's a Should because again, not being able to load the data in Minio, I was not able to actually query via Trino/Hive
You will find the sql queries under `resources/sql/business.sql`

## Decisions
My idea was to create a initial streaming pipeline from kafka enhancing the orders with the information about the industry.
This would let me be able to query the dataset after simply enough for what we are looking for.

At the moment I provided only SQL Queries for the results itself but I've not automated any process on it.

A good idea here would be having a new scheduled job that runs every day.
This job would UPSERT the average for the last 30 days into MariaDB or Minio.

In this scenario we have already the "historical data" grouped easily enough (in terms of size) to be able to perform the top 3 industries in last 24hours metrics.

So back again, I would create a new pipeline or just a normal scheduled query (Airflow could be a good orchestrator here), to generate the grouped dataset for historical data
and push it to MariaDB or again on Minio.

Now since I don't know what is the business core value of the TOP 3 Industries assignment, I could think about two different scenario again:

1. we need to update a dashboard daily --> a daily scheduled job of last 24hours records to compare to what it was saved on MariaDB/Minio and save on Minio (late records could affect the final result)
2. We need to answer this via a REST API --> A scheduled 2/3 minutes job that collect last 24 hours and compare to MariaDB/Minio and save on MariaDB

What I'm trying to achieve is being able to differentiate if the business value is update as soon as new orders arrive (in a "streaming" near-real time mode) the top 3 industries
or we can live with having a daily update.

## Some thoughts

There are different approach to answer some of this decision, for example, we could use Kafka Streaming with a GlobalKTable instead of Spark for enhancing orders with industry.
The fact that Customers.csv can be update once every 12 hours, it means we could just send a new record to the GlobalKTable so Kafka Streaming can do what I did here on Spark.

I didn't have to think too much about Customer.csv can be updated every 12 hours, since Spark Streaming will "re-read" the file when it's in join with a Streaming Dataframe.
Plus in the code I also saved as a `val` orders that are not matched, just in case we need to understand why.

I also filtered out from the Customers.csv the company_id that doesn't have any industry (it seems this do not bring any value to this case).

## More thoughts

I spent way too much time try to fix some of the internal issue over writing the assignment (that I couldn't eventually fix).
Unfortunately this brings to a not very well nice and done job in terms of what was provided.

In terms of README provided, I would have actually mentioned that spark config is loaded automatically, and that to run the project most likely we should use the `bash` script provided.
It took me a while to realize that some of these stuff were part of the docker-compose file.

>What about unit testing? Yes they are missing. Unfortunately. Time constraint again, and not being able to make the project work as intended.

## Final Final Thoughts
I suppose that if I didn't encounter so many issue to being able to run the project, I would have achieved more.
Like I see that part of the scala build.sbt dependencies is `scopt` and of course I would add some config as parameter via it, like the kafka topic to read, and most likely location where to write, location where to read, etc..

Also I would have splitted the `transform` function into more unit testable smaller function.

Generally Speaking I liked to have a fully boostrap project, it avoided me to do some extra jobs, even so probably I over-fixated myself into using what was provided, like minio and apache iceberg (that I think somehow brought to some of the issue I mentioned above).

I am not proud of my end up project, but I decided to spend some of the time writing the Readme explaining what was wrong and how I would have worked on.