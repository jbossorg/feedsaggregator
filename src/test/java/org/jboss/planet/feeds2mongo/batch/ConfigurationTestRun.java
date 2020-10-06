package org.jboss.planet.feeds2mongo.batch;

import org.apache.commons.lang3.StringUtils;

/**
 * test for running {@link ProcessAllFeedsTest} but with no DB test. Config URL is taken from system property
 * 'configUrl'
 */
public class ConfigurationTestRun extends ProcessAllFeedsTest {

    @Override
    protected String getConfigUrl() throws Exception {
        String cPath = System.getProperty("configUrl");
        if (StringUtils.isBlank(cPath)) {
            throw new Exception("Variable `configUrl` not configured");
        }
        return cPath;
    }

    @Override
    protected void testDB() {
        // do nothing
    }
}
