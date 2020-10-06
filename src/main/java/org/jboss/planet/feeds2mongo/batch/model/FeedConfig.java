package org.jboss.planet.feeds2mongo.batch.model;

/**
 * Model class for feed configuration
 */
public class FeedConfig {

    String code;

    String url;

    String group;

    public FeedConfig(String group, String code, String url) {
        this.code = code;
        this.url = url;
        this.group = group;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
