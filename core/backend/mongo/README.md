# Mongo Backend


The access to DB is configurable via jobs properties:
Configuration: 
1. `mongoUrl` - Mongo URL
2. `db` - DB Name
3. `collection` - Collection name

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
