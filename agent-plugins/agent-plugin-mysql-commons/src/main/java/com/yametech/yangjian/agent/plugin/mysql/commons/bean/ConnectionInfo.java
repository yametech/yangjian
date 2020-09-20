package com.yametech.yangjian.agent.plugin.mysql.commons.bean;

/**
 * @author dengliming
 * @date 2020/5/4
 */
public class ConnectionInfo {

    private String url;
    private String databaseName;

    public ConnectionInfo(String url, String databaseName) {
        this.url = url;
        this.databaseName = databaseName;
    }

    public String getUrl() {
        return url;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
