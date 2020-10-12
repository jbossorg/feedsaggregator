package org.jboss.feedsagg.feedreader;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.feedsagg.common.JobUtils;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * Read the feed from url and pass individual feed posts
 */
public class FeedReader implements ItemReader {

    private static final String USER_AGENT = "Java/11 planet.jboss.org";

    @Inject
    JobContext jobContext;

    XmlReader reader;

    protected SyndFeed feed;

    List<SyndEntry> entries;

    protected int rowNumber;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        Properties jobProperties = JobUtils.getJobParameter(jobContext);

        String feedUrl = jobProperties.getProperty("url");
        if (feedUrl == null) {
            throw new BatchRuntimeException("job parameter `url` must be defined");
        }
        int connectTimeoutMs = Integer.parseInt(jobProperties.getProperty("connectTimeout", "5")) * 1000;
        int readTimeoutMs = Integer.parseInt(jobProperties.getProperty("readTimeoutMs", "15")) * 1000;
        String userAgent = jobProperties.getProperty("userAgent", USER_AGENT);

        SyndFeedInput input = new SyndFeedInput();
        if (feedUrl.startsWith("/")) {
            reader = new XmlReader(FeedReader.class.getResourceAsStream(feedUrl));
        } else {
            reader = new XmlReader(getConnection(feedUrl, connectTimeoutMs, readTimeoutMs, userAgent).getInputStream());
        }
        try {
            feed = input.build(reader);
            entries = feed.getEntries();
        } catch(Exception e) {
            throw new BatchRuntimeException("Cannot parse feed. feedUrl=" + feedUrl, e);
        }

        if (checkpoint != null) {
            rowNumber = (Integer) checkpoint;
        } else {
            rowNumber = 0;
        }
    }

    protected URLConnection getConnection(String link, int connectTimeoutMs, int readTimeoutMs, String userAgent) throws IOException {
        URLConnection conn = new URL(link).openConnection();
        conn.setConnectTimeout(connectTimeoutMs);
        conn.setReadTimeout(readTimeoutMs);
        conn.setRequestProperty("User-Agent", userAgent);
        conn.connect();

        return conn;
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
