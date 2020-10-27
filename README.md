# Feeds Aggregator

[![GitHub Actions Status](<https://img.shields.io/github/workflow/status/jbossorg/feedsaggregator/Java CI with Maven?logo=GitHub&style=for-the-badge>)](https://github.com/jbossorg/feedsaggregator/actions?query=workflow%3A%22Java+CI+with+Maven%22)
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
* [Standalone java app](https://github.com/jbossorg/feedsaggregator/packages/439303/versions).
* [Docker](https://hub.docker.com/repository/docker/jbossorg/feedsaggregator_feeds2mongo)

### Service API
1. [api-mongo](api/api-mongo) - REST API backed by mongo DB.
* [Standalone java app](https://github.com/jbossorg/feedsaggregator/packages/470473/versions).
* [Docker](https://hub.docker.com/repository/docker/jbossorg/feedsaggregator_api-mongo)

## Deployment

1. [Kubernetes](deployment/k8s)
