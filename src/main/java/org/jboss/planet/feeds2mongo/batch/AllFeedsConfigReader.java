package org.jboss.planet.feeds2mongo.batch;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.planet.feeds2mongo.batch.model.FeedConfig;
import org.yaml.snakeyaml.Yaml;

/**
 * Config reader for all feeds job. Reads configuration yaml file and converts it to List of {@link FeedConfig}
 */
public class AllFeedsConfigReader implements ItemReader {

    @Inject
    JobContext jobContext;

    List<FeedConfig> feeds;

    private int index;

    public static List<FeedConfig> getConfig(InputStream is) {
        Yaml config = new Yaml();
        List<Map<String, List<Map>>> confs = config.load(is);

        List<FeedConfig> allFeeds = new ArrayList<>();
        for (Map<String, List<Map>> group : confs) {
            group.forEach((key, value) -> {
                for (Map feed : value) {
                    allFeeds.add(new FeedConfig(key, feed.get("code").toString(), feed.get("url").toString()));
                }
            });
        }
        return allFeeds;
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = FeedReader.getJobParameter(jobContext);

        String configUrl = jobProperties.getProperty("configUrl");
        if (configUrl == null) {
            throw new BatchRuntimeException("job parameter `configUrl` must be defined");
        }

        InputStream is = new URL(configUrl).openStream();
        feeds = getConfig(is);
        is.close();

        if (checkpoint != null) {
            index = (Integer) checkpoint;
        } else {
            index = 0;
        }
    }

    @Override
    public Object readItem() throws Exception {
        if (index < feeds.size()) {
            return feeds.get(index++);
        }
        return null;
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return index;
    }
}
