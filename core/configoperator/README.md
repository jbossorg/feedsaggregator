# Configuration operator

Reads and parse the configuration file and trigger processing jobs for each feed.

1. [AllFeedsConfigReader](src/main/java/org/jboss/feedsagg/config/AllFeedsConfigReader.java) - Reads the configuration and creates for each feed the FeedConfig object
2. [AllFeedsWriter](src/main/java/org/jboss/feedsagg/config/AllFeedsWriter.java) - triggers a new job for each FeedConfig.
   The job name is configurable by `PROCESS_JOB_NAME` property and job timeout by `PROCESS_JOB_TIMEOUT_SEC` where -1 is no timeout. 

## Job Configuration

1. `configUrl` - URL of configuration. e.g. file:///app/config/feeds-config.yaml

To customize the name of job that is triggered define it in the writer property
```xml
    <writer ref="allFeedsWriter">
        <properties>
            <!-- Optional property defining the name of job to trigger. Default is process-feed.xml -->
            <property name="PROCESS_JOB_NAME" value="process-feed.xml"/>
            <!-- Optional property defining the timeout of triggered job. Default is 60 seconds. -1 is infinite -->
            <property name="PROCESS_JOB_TIMEOUT_SEC" value="60"/>
        </properties>
    </writer>
```

## Configuration file schema

```yaml
- group1:
 - code: feedcode1
   title: Feed title (Optional)
   author: Name of Author used if the original Feed author not present (Optional)
   url: http://example.com/atom.xml
 - code: feedcode2
   url: http://example.com/atom2.xml
- group2:
 - code: feedcode3
   url: http://example.com/atom3.xml
 - code: feedcode4
   url: http://example.com/atom4.xml
```

## Example Usage

See [feeds2mongo example](../../dist/feeds2mongo/src/main/resources/META-INF/batch-jobs/process-all-feeds.xml)
See [Config example](../../config/feeds-config.yaml)
