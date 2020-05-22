package com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast;

public enum AutoIncrementType
{
    GROUP("GROUP"), SIMPLE("SIMPLE"),
    SIMPLE_CACHE("SIMPLE WITH CACHE"), TIME("TIME");

    private final String keyword;

    public String getKeyword() {
        return this.keyword;
    }

    AutoIncrementType(String keyword){
        this.keyword = keyword;
    }


}
