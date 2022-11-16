# Description

This project aims build an OS resource monitoring application. Specifically CPU, Disk and Memory Usage.

These metrics are pinged every 10 seconds and are persisted to database

The polling frequency period is configurable from `application.properties` file

## Architecture
Because of time constraints, I used existing java frameworks and libraries to simplify the task in hand.
[Quarkus](https://quarkus.io/), as Java Framework and [OSHI](https://github.com/oshi/oshi) library's API to fetch required metrics.

On a high level, I have 3 scheduled jobs (one for each CPU, Disk and Memory) that are invoked periodically.
Every time metric is fetched, it is persisted to postgres database - Ideally we should use time series database, please read future enhancements section

### High Availability and Scalability
[Quartz Scheduler](http://www.quartz-scheduler.org/) is used for scheduling. The main reasons for choosing this is ability to run
scheduling in cluster mode. Clustering essentially works by having each node of the cluster share the same database.

Load-balancing occurs automatically, with each node of the cluster firing jobs as quickly as it can. 
When a trigger’s firing time occurs, the first node to acquire it (by placing a lock on it) is the node that will fire it.

By default, Quartz uses thread pool with 25 threads and can be configurable

This is all abstracted out to us by using Quartz Library.

[Useful Reading on Quartz Clustering](http://www.quartz-scheduler.org/documentation/quartz-1.8.6/configuration/ConfigJDBCJobStoreClustering.html)

### DB Scripts
DB Scripts are located in `resources/db/migration/V2.0.0__QuarkusQuartzTasks.sql`
Quarkus uses Flyway to handle DB Migrations. Main tables are CPU, Disk and Memory. The rest of other tables are provided
by Quarkus/Quartz add on to enable clustering functionality

I have included partitioning on the all 3 main tables based on metric collected date.
Partition key is set to be metric collected date and should be efficient for range queries.

Postgres automatically creates logical partition for us based on the declarative partitioning provided in sql files

## Running the application

The application can be packaged using:
```shell script
./gradlew build
```

Build docker image:
```shell script
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/quartz-resource-monitor-jvm .
```

Run docker-compose. Below command deploys 2 instances of our application
```shell script
docker-compose up --scale tasks=2 --detach
```

The application is also runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

## Future Enhancements


Ideally, below are the changes I would implement given enough time

- Use Time Series Database like OpenTSDB/Apache Pinot to store metric information
- Use Kafka for fault tolerance. In such case the app becomes producer, pushing metrics to Kafka topic and we can setup
apache pinot to automatically consume data from kafka into realtime ingestion tables
- TSDB will automatically take care of rollup's based on configuration. 
Ex: We can set TSDB to roll up daily data to hourly for metric that is older than a week.
- Schedule Job to automatically move older metric data to cold storage or different tables (currently partitioning tables are
created manually)

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- Flyway ([guide](https://quarkus.io/guides/flyway)): Handle your database schema migrations
- Quartz ([guide](https://quarkus.io/guides/quartz)): Schedule clustered tasks with Quartz
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
