# Mongo Backend

Component provides ItemProcessor and ItemWriter for storing Feed Entries into Mongo DB. 

## Job Configuration

The access to DB is configurable via jobs properties:
Configuration: 
1. `mongoUrl` - Mongo URL
2. `db` - DB Name
3. `collection` - Collection name
4. `feed` - Feed Name
5. `group` - Group name (Optional)

## Mongo DB

### Indexes
Indexes are being automatically created on these fields:
1. `url` - unique
2. `code`
3. `published`
4. `feed`
5. `group`
6. `tags`

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
    "content_preview" : "In a previous blog post about Kafka and Avro, we used an emitter to send Kafka messages...",
    "content" : "\n                In a previous blog post about <a href='www.kafka.io'>Kafka</a> and Avro, we used an emitter to send Kafka messages. In this post, we are going look at this emitter construct a little bit more closely. Injecting an Emitter Injecting an emitter is straightforward. You indicate the targeted channel, i.e., where do you...\n            "
}
```

Notes:
* `content` field can be long and can contain html formating from tje original blogpost
* `content_preview` is content cleaned not to contain html formating and shortened so it can be used as an preview

## Job Example Usage

See [feeds2mongo example](../../../dist/feeds2mongo/src/main/resources/META-INF/batch-jobs/process-feed.xml)
