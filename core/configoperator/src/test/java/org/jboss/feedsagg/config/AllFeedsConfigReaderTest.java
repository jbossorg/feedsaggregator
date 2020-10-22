package org.jboss.feedsagg.config;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link AllFeedsConfigReader}
 */
public class AllFeedsConfigReaderTest {

    @Test
    public void testGetConfigs() throws Exception {
        List<FeedConfig> config;
        try (InputStream is = AllFeedsConfigReaderTest.class.getResourceAsStream("/test-feeds-config.yaml")) {
            config = AllFeedsConfigReader.getConfig(is);
        }

        FeedConfig conf1 = config.get(0);
        Assert.assertEquals("test1", conf1.getCode());
        Assert.assertEquals("/test-feed.xml", conf1.getUrl());
        Assert.assertEquals("test-group", conf1.getGroup());

        FeedConfig conf2 = config.get(1);
        Assert.assertEquals("test2", conf2.getCode());
        Assert.assertEquals("/test-feed2.xml", conf2.getUrl());
        Assert.assertEquals("test-group", conf2.getGroup());
    }

    @Test(expected = FeedsConfigException.class)
    public void testGetConfigDuplicities() throws Exception {
        try (InputStream is = AllFeedsConfigReaderTest.class.getResourceAsStream("/test-feeds-config-duplicite-feeds.yaml")) {
            AllFeedsConfigReader.getConfig(is);
        }
    }
}