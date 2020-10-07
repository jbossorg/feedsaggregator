# feeds2mongo

Simple Java application which stores RSS/ATOM feed blog posts to mongo DB.

The access to DB is configurable including mongo URL, DB name and collection name.

The stored Blog post in Mongo looks like this:

```json
{
    "_id" : "auto generated object id",
    "feed" : "quarkus",
    "group" : "quarkus",
    "title" : "Emitter - Bridging the imperative and the reactive worlds",
    "code" : "emitter_bridging_the_imperative_and_the_reactive_worlds",
    "url" : "https://quarkus.io/blog/reactive-messaging-emitter/",
    "author" : "The Blogger",
    "published" : "2020-10-06T00:00:00.000Z",
    "updated" : "2020-10-06T00:00:00.000Z",
    "tags" : [ 
        "quarkus", 
        "kafka"
    ],
    "content" : "\n                In a previous blog post about Kafka and Avro, we used an emitter to send Kafka messages. In this post, we are going look at this emitter construct a little bit more closely. Injecting an Emitter Injecting an emitter is straightforward. You indicate the targeted channel, i.e., where do you...\n            "
}
```

## Jobs

### 1. process-feed.xml

Parses the given url and store all posts to DB

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

The configuration stored in `config/feeds-config.yaml` can be tested as part of junit test which stores data into in-memory mongoDB.

Simply run

```
mvn clean test -Pconfig-test-included
```

## How to build app

Run maven command to create uberjar
```
mvn clean package -Puberjar
```

## How to run locally

1. Start mongo

```
docker run -it --rm -p 27017:27017 mongo:3.6
```

2. Build app as uberjar
```
mvn clean package -Puberjar
```

3. Process individual feed (process-feed.xml job)

```
java -jar target/feeds2mongo.jar process-feed.xml \
     url=https://quarkus.io/feed.xml \
     feed=quarkus \
     mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```

4. Process multiple feeds based on configuration (process-all-feeds.xml job)

```
java -jar target/feeds2mongo.jar process-all-feeds.xml \
     configUrl=file://$HOME/git/feeds2mongo/config/feeds-config.yaml \
     mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```
