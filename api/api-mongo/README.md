# Feeds Aggregator Service API - mongo backend

Feeds Aggregator Service API with mongo backend and in-memory caching.

## REST API

REST API description
* `/rest/v1/search` - get latest blog posts (order by published desc)
    optional request parameters:
  * from=0
  * size=10 - limit of blog posts. Maximum is 100.
  * sort=[asc|desc] (default desc)
  * feed=feed_name - filter posts only for given feed, can be used multiple times to mix more feeds
  * group=group_name - filter posts only for given group, can be used multiple times to mix more groups
  * tag=tag_value - filter posts only for given tag, can be used multiple times to mix more tags
* `/rest/v1/post/{code}` - get blog-post based on its `code` field in mongo
* `/health` - health checks
* `/health/live` - liveness check
* `/health/ready` - readiness check. If MongoDB is down the check is down as well.

Example Search:

* http://localhost:8080/rest/v1/search?size=10&group=keycloak
* http://localhost:8080/rest/v1/post/5f858efcd36cf88d1ff331a4

Related URL's
* `/openapi` - OpenAPI/Swagger documentation of the REST API
* `/openapi?format=json` - OpenAPI/Swagger documentation of the REST API in JSON format
* `/swagger-ui` - Swagger UI for the REST API

## RSS Feeds API
* `/feed/v1` - RSS Atom Feed with blog posts (order by published desc). Optional request parameters:


## GraphQL API
* `/graphql` - GraphQL API
* `/graphql-ui` - GUI for GraphQL API introspection and testing


## Configuration

The app is configurable in Quarkus way. Any of [application.properties](src/main/resources/application.properties) can be overwritten.
The most important are:
* `quarkus.mongodb.connection-string` - mongo db connection string. See [Configuration reference](https://quarkus.io/guides/mongodb#configuration-reference)
* `app.mongo.db` - DB name
* `app.mongo.collection` - Collection name

## How to run app

The application is runnable using `java -jar target/restapi-mongo-runner.jar`.
