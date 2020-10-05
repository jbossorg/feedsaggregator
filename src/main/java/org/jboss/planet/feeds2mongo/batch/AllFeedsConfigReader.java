package org.jboss.planet.feeds2mongo.batch;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jberet.runtime.JobExecutionImpl;
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

        log.infof("Opening job `AllFeedsConfigReader` with job properties %s", jobProperties);

        String configUrl = jobProperties.getProperty("configUrl");
        if (configUrl == null) {
            throw new BatchRuntimeException("job parameter `configUrl` must be defined");
        }

        InputStream is = new URL(configUrl).openStream();
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
        int timeout = Integer.parseInt(System.getProperty("timeout", "10"));
        // Wait on all executions
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<JobInstance> jobInstances = jobOperator.getJobInstances("process-feed", 0, Integer.MAX_VALUE);
        for (JobInstance instance : jobInstances) {
            final JobExecutionImpl exec = (JobExecutionImpl) jobOperator.getJobExecution(instance.getInstanceId());

            log.infof("Waiting for job completion jobInstance=%s timeout=%s", instance.getInstanceId(), timeout);
            exec.awaitTermination(timeout, TimeUnit.MINUTES);
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return index;
    }
}
