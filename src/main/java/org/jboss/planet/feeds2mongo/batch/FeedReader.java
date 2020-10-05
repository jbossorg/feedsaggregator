package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Named
public class FeedReader implements ItemReader {

    private Logger log = Logger.getLogger(FeedReader.class);

    @Inject
    JobContext jobContext;

    XmlReader reader;

    String feedUrl;

    String feedCode;

    protected SyndFeed feed;

    List<SyndEntry> entries;

    protected int rowNumber;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        Properties jobProperties = getJobParameter(jobContext);

        log.infof("Opening job with job properties %s", jobProperties);

        feedUrl = jobProperties.getProperty("url");
        if (feedUrl == null) {
            throw new BatchRuntimeException("job parameter `url` must be defined");
        }

        feedCode = jobProperties.getProperty("code");

        SyndFeedInput input = new SyndFeedInput();
        reader = new XmlReader(new URL(feedUrl));
        feed = input.build(reader);
        entries = feed.getEntries();

        if (checkpoint != null) {
            rowNumber = (Integer) checkpoint;
        } else {
            rowNumber = 0;
        }
    }

    public static Properties getJobParameter(JobContext jobContext) {
        long executionId = jobContext.getExecutionId();
        JobExecution jobExecution = BatchRuntime.getJobOperator().getJobExecution(executionId);
        return jobExecution.getJobParameters();
    }

    @Override
    public Object readItem() {
        if (rowNumber >= entries.size()) {
            return null;
        }
        return entries.get(rowNumber++);
    }

    @Override
    public Serializable checkpointInfo() {
        return rowNumber;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }

}
