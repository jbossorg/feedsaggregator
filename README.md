# Feeds Aggregator

![Java CI with Maven](https://github.com/jbossorg/feedsaggregator/workflows/Java%20CI%20with%20Maven/badge.svg)
[![License](https://img.shields.io/github/license/jbossorg/feedsaggregator?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)

Powerful and modular Java RSS/ATOM feeds aggregator and indexer.

Allows fetching one or multiple feeds in multithreaded way and store it to chosen/custom backend.

The aggregator is based on Java Batch Processing (JSR 352) framework and ships base logic for:
1. [Reading RSS/ATOM feed](core/feedreader)
2. Storing to backend e.g. [mongo](core/backend/mongo)
3. [Common logic](core/common) for logging, skip invalid posts etc.
4. [Parsing multiple feeds](core/configoperator) and triggering appropriate feed reader jobs

Thanks to the framework it's possible to declaratively define the overall workflow.

Maven multi-module layout offers picking exactly what is needed to produce microservice rather than combining all combinations together.

The project ships also ready to go [distributions](dist) which can be used as well as for creating own combinations.

## Distributions

1. [feeds2mongo](dist/feeds2mongo) - indexer with mongo backend.

## How to build

Simply run maven - it produces core components as well as builds distributions

```
mvn clean package
```
