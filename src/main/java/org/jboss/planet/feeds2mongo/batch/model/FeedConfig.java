package org.jboss.planet.feeds2mongo.batch.model;

/**
 * Model class for feed configuration
 */
public class FeedConfig {

    String code;

    String url;

    String group;

    String author;

    public FeedConfig(String group, String code, String url, String author) {
        this.code = code;
        this.url = url;
        this.group = group;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
