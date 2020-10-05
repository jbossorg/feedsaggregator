# feeds2mongo

Application stores RSS/ATOM feeds to mongo DB.


## How to build app

Run maven command to create uberjar
```
mvn clean package -Puberjar
```

## How to run `process-feed` job

### Locally

```
mvn clean package -Puberjar
java -jar target/feeds2mongo.jar process-feed.xml url=https://quarkus.io/feed.xml feedCode=quarkus
```

