# Feeds Aggregator

Powerful and modular Java RSS/ATOM feeds aggregator and indexer.

Allows fetch one or multiple feeds in multithreaded way and store it to chosen/custom backend.

The aggregator is based on Java Batch Processing (JSR 352) framework and ships base logic for:
1. Reading RSS/ATOM feed
2. Storing to backend e.g. mongo
3. Skipping logic for invalid feed entries
4. Job and skip listener for logging

Thanks to the framework it's possible to declaratively define the overall workflow.

The project ships also ready to go distributions which can be used as prototype for writing own combinations.

## Distributions

1. [feeds2mongo](dist/feeds2mongo) - indexer with mongo backend.
