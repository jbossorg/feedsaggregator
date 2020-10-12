# Feed Reader

FeedReader reads from defined URL and pass individual entries further.

## Job Configuration

1. `url` - URL of the feed to index
2. `connectTimeout` - connection timeout in seconds. Default 5 seconds.
3. `readTimeout` - read timeout in seconds. Default 15 seconds.
4. `userAgent` - User Agent. Default `Java/11 planet.jboss.org`


## Example Usage

See [feeds2mongo example](../../dist/feeds2mongo/src/main/resources/META-INF/batch-jobs/process-feed.xml)
