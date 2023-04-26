# Interview Assignment - Data Engineering

In this repository you can find the infrastructure setup and instructions necessary to solve the assignment described under [The Assignment](#the-assignment) section.

The purpose of this assignment is to show your data engineering and coding skills to assess the problem solving skill set, creativity, and demonstrate your experience with the tools which you choose. 

It is important to convince your future teammates with your competences in:

- Python 3 or Scala
- Data structures and algorithms
- Distributed data processing frameworks
- Distributed messaging system
- Git
- (optional) SQL
- An insightful, readable, well-structured README.md; (We don't expect extensive/fancy documentation or diagrams)

We try and make sure that the docker compose file works as expected, but it could be that you run into some issues.  
If you do, please feel free to report them (or fix them) so we can improve the experience.

## Existing state and setup
What can you find in this repo?  

To make solving the assignment time efficient, we are providing you with a docker compose file that sets up all the infrastructure necessary to complete the assignment as well as some startup files for various ways of working with Spark.  
You can chose one of scala app, pyspark app or notebook as the way to solve the assignment.

- For scala and python, you can find an empty project and some helper scripts in `./spark-jobs-scala` and `./spark-jobs-python` respectively.  
- Notebooks are accessible on `http://localhost:8888` and are stored in `./notebooks`

From the provided infrastructure, you will find a Hive catalog to store the table metadata, MinIO as a replacement for S3, Kafka (+zookeeper) for streaming, Trino to run interactive SQL queries, Spark for the ETL and Jupyter notebooks as the IDE to build your solution.  

Spark is set up to use the Hive catalog, it has support for Iceberg tables and support for Kafka structured streaming so you don't have to mess around with setting those up.

In addition to the above tools, we are providing a `order-generator` application that will generate a stream of orders and push them to the "orders" kafka topic in a json format.  
You should also use `order-generator` to generate recent historical order data.

We provide custom containers for `order-generator` and `kafka` that should work with M1 macs.  
After you generated some historical orders and made any modifications you want, you should be able to just run `docker compose up` to get everything up and running.

This entire setup needs reasonable amount of memory.  
If you are running a Mac and have Colima (or Docker Desktop or something similar) installed, make sure that the VM use by Docker has at least 8GB of memory.

Here are the important URLs that should get your started:

- Trino: http://localhost:9090
- Jupyter: http://localhost:8888
- MinIO: http://localhost:9001

Others you can find in the docker compose file.

### Data and buckets
Assignment requires some initial data for you to be able to work on it effectively.  
In the `data` folder, you can find the demo data that will be placed in the `demo-data` bucket in minio.  
In the same folder you can find `copy_to_minio.sh` script.  
That script will run on startup and create the following buckets:

- demo-data
- dal
- temp

Feel free to change the script and add any buckets you need for your solution.  

> Note:  DAL is a shortcut for Data Access Layer. We use it in various places.

### Databases and tables
Your solution should define databases and tables for the data being ingested and processed.   
Hive comes preconfigured with iceberg connector and it exposes a single catalog called `dal`.

If you want, you can change the containers and use some other table format.  
We leave that up to you. It is not necessary for the solution to be complete.

## The Assignment

You have to develop a data engineering solution to answer the following question:

> Which top 3 industries are showing biggest change in the last 24 hours compare to the past 30 days average?

To answer that you need to work with a stream of orders, a batch data set of customers, and a value object of industries.

| Orders (stream) | [order_id, customer_id, order_line[product_id, volume, price], amount, timestamp] |
| --- | --- |
| Customers (batch) | [customer_id, company_name, specialised_industries] |
| Industries | Agriculture, Colours, Cleaning, Construction, Cosmetics, Food, Lubricants, Oil&Gas, Pharmaceuticals, Polymer |

### Notes

<aside>
👉 New orders are coming as a stream; You are free to choose any messaging solution you prefer, but we got you started with Kafka.
</aside>

<aside>
👉 Assume that Customers data set is coming in batch with any arbitrary frequency (e.g. every 12 hours).
</aside>

<aside>
👉 A customer can be specialised in multiple industries (e.g. Cosmetics & Pharmaceuticals).
</aside>

<aside>
🎯 Based on the aforementioned data sets, you should be able to calculate which top 3 industries fluctuated the most (whether positive or negative) in the last 24 hours of orders comparing to the historical data of the last 30 days.
</aside><br>

We have provided you with an environment to do the assignment, but you are free to change any part of it.  
If you do change it, we would like to know what and why!  
Please provide some notes for your reasoning.

### Considerations

1. When loading data from batch data sets, make sure you have de-duplication logic in place.
2. Think about what and how to test.
3. Please create tables for the data sets.
4. Please keep the code in the private repo in GitHub/GitLab/Bitbucket and provide access to the reviewers or alternatively send us a zip of your local Git repo.

## __Have fun and best of luck!__
