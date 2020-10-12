# Feeds Aggregator Distribution: feeds2mongo

The distribution pack core components and use mongo as backend.

## Jobs

These jobs are preconfigured:

### 1. process-feed.xml

Parses the given `url` and store all posts to DB

Configuration:
1. `url` - URL of the feed to index
3. `feed` - Code of the feed
4. `group` - Code of the feed group (Optional)
5. `mongoUrl` - Mongo URL
6. `db` - DB Name
7. `collection` - Collection name

### 2. process-all-feeds.xml

Read feeds configuration file and schedule for each feed the `process-feed` job.

Configuration: 
1. `configUrl` - URL of configuration. e.g. file:///app/config/feeds-config.yaml
2. `mongoUrl` - Mongo URL
3. `db` - DB Name
4. `collection` - Collection name

## Deployment

### How to run locally

1. Start mongo

```
docker run -it --rm -p 27017:27017 mongo:3.6
```

2. Process individual feed (process-feed.xml job)

```
java -jar target/feeds2mongo.jar process-feed.xml \
     url=https://quarkus.io/feed.xml \
     feed=quarkus \
     mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```

3. Process multiple feeds based on configuration (process-all-feeds.xml job)

```
java -jar target/feeds2mongo.jar process-all-feeds.xml \
     configUrl=file://$HOME/git/feeds2mongo/config/feeds-config.yaml \
     mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```

### How to run on Openshift

TODO

### How to run on Kubernetes

TODO

## How to test feeds-config.yaml as junit test

The configuration stored in `/config/feeds-config.yaml` can be tested as part of junit test which stores data into in-memory mongoDB.

Simply run

```
mvn clean test -Pconfig-test-included
```
