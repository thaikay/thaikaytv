package com.thaikaytv.app.models;

import java.io.Serializable;

public class Channel implements Serializable {
    private String name;
    private String url;
    private String logo;
    private String group;

    public Channel(String name, String url) {
        this.name = name;
        this.url = url;
        this.logo = "";
        this.group = "";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public long getId() {
        return name.hashCode() + url.hashCode();
    }
}
