package org.jboss.planet.feeds2mongo.batch;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.yaml.snakeyaml.Yaml;

public class AllFeedsConfigReader implements ItemReader {

    private Logger log = Logger.getLogger(AllFeedsConfigReader.class);

    @Inject
    JobContext jobContext;

    List<Map<Object, Object>> feeds;

    private int index;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = FeedReader.getJobParameter(jobContext);

        log.infof("Opening job with job properties %s", jobProperties);

        String configPath = jobProperties.getProperty("configPath");
        if (configPath == null) {
            throw new BatchRuntimeException("job parameter `configPath` must be defined");
        }

        InputStream is = new URL(configPath).openStream();
        Yaml config = new Yaml();
        feeds = config.load(is);
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
