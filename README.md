# feeds2mongo

Application stores RSS/ATOM feeds to mongo DB.

## Jobs

### 1. process-feed.xml

Configuration:
1. `url` - URL of the feed to index
2. `feed` - Code of the feed
2. `mongoUrl` - Mongo URL
3. `db` - DB Name
4. `collection` - Collection name

### 2. process-all-feeds.xml

Read feeds configuration file and schedule for each feed the `process-feed` job

Configuration: 
1. `configUrl` - URL of configuration. e.g. file:///app/config/feeds-config.yaml
2. `mongoUrl` - Mongo URL
3. `db` - DB Name
4. `collection` - Collection name

The configuration stored in `config/feeds-config.yaml` can be tested as part of junit test with in memory mongoDBe

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

3. Process individual feed 

```
java -jar target/feeds2mongo.jar process-feed.xml \
     url=https://quarkus.io/feed.xml \
     feed=quarkus mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```

4. Process feed based on config 

```
java -jar target/feeds2mongo.jar process-all-feeds.xml \
     configUrl=file://$HOME/git/feeds2mongo/config/feeds-config.yaml \
     mongoUrl=mongodb://localhost:27017 \
     db=feeds2mongo \
     collection=post
```
