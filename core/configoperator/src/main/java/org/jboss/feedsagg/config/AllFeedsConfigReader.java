package org.jboss.feedsagg.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.yaml.snakeyaml.Yaml;

/**
 * Config reader for all feeds job. Reads configuration yaml file and converts it to List of {@link FeedConfig}
 */
public class AllFeedsConfigReader implements ItemReader {

    @Inject
    JobContext jobContext;

    List<FeedConfig> feeds;

    private int index;

    public static List<FeedConfig> getConfig(InputStream is) throws IOException, FeedsConfigException {
        Yaml config = new Yaml();
        List<Map<String, List<Map>>> confs = config.load(is);

        Set<FeedConfig> uniqueConfigs = new HashSet<>();
        for (Map<String, List<Map>> group : confs) {
            for (Map.Entry<String, List<Map>> groupEntry : group.entrySet()) {
                for (Map feed : groupEntry.getValue()) {
                    FeedConfig c = new FeedConfig(groupEntry.getKey(), (String) feed.get("code"), (String) feed.get("url"), (String) feed.get("author"));
                    boolean added = uniqueConfigs.add(c);
                    if (!added) {
                        throw new FeedsConfigException("duplicate feed configuration", c);
                    }
                }
            }
        }
        return new ArrayList<>(uniqueConfigs);
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);

        String configUrl = jobProperties.getProperty("configUrl");
        if (configUrl == null) {
            throw new BatchRuntimeException("job parameter `configUrl` must be defined");
        }

        try (InputStream is = new URL(configUrl).openStream()) {
            feeds = getConfig(is);
        }

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
