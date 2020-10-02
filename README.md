# feeds2mongo

Application stores RSS/ATOM feeds to mongo DB.


## How to build app

Run maven command to create uberjar
```
mvn clean package -Puberjar
```

## How to run `feed-check` job

### Locally

```
mvn clean package -Puberjar
java -jar target/feeds2mongo.jar feed-check.xml url=https://quarkus.io/feed.xml
```

