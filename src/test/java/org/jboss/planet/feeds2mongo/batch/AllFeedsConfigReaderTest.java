package org.jboss.planet.feeds2mongo.batch;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jboss.planet.feeds2mongo.batch.model.FeedConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link AllFeedsConfigReader}
 */
public class AllFeedsConfigReaderTest {

    @Test
    public void getConfig() throws IOException {
        InputStream is = AllFeedsConfigReaderTest.class.getResourceAsStream("/test-feed-config.yaml");
        List<FeedConfig> config = AllFeedsConfigReader.getConfig(is);
        is.close();

        FeedConfig conf1 = config.get(0);
        Assert.assertEquals("test1", conf1.getCode());
        Assert.assertEquals("/test-feed.xml", conf1.getUrl());
        Assert.assertEquals("test-group", conf1.getGroup());

        FeedConfig conf2 = config.get(1);
        Assert.assertEquals("test2", conf2.getCode());
        Assert.assertEquals("/test-feed2.xml", conf2.getUrl());
        Assert.assertEquals("test-group", conf2.getGroup());

    }
}