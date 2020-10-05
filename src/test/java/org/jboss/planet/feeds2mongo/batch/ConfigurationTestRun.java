package org.jboss.planet.feeds2mongo.batch;

import org.apache.commons.lang3.StringUtils;

public class ConfigurationTestRun extends ProcessAllFeedsTest {

    @Override
    protected String getConfigPath() throws Exception {
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
