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

    public void open(final Serializable checkpoint) throws Exception {
        SyndFeedInput input = new SyndFeedInput();
        Properties jobProperties = getJobParameter();

        log.infof("Opening job with job properties %s", jobProperties);

        feedUrl = jobProperties.getProperty("url");
        if (feedUrl == null) {
            throw new BatchRuntimeException("job parameter `url` must be defined");
        }

        feedCode = jobProperties.getProperty("code");

        reader = new XmlReader(new URL(feedUrl));
        feed = input.build(reader);
        entries = feed.getEntries();

        if (checkpoint != null) {
            rowNumber = (Integer) checkpoint;
        } else {
            rowNumber = 0;
        }
    }

    public Properties getJobParameter() {
        long executionId = jobContext.getExecutionId();
        JobExecution jobExecution = BatchRuntime.getJobOperator().getJobExecution(executionId);
        return jobExecution.getJobParameters();
    }

    public Object readItem() {
        if (rowNumber >= entries.size()) {
            return null;
        }
        return entries.get(rowNumber++);
    }

    public Serializable checkpointInfo() {
        return rowNumber;
    }

    public void close() throws Exception {
        reader.close();
    }

}
