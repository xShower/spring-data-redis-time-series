package org.springframework.data.redis.core.protocol;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public enum Aggregation {
    AVG("avg"),
    SUM("sum"),
    MIN("min"),
    MAX("max"),
    RANGE("range"),
    COUNT("count"),
    FIRST("first"),
    LAST("last"),
    STD_P("std.p"),
    STD_S("std.s"),
    VAR_P("var.p"),
    VAR_S("var.s");

    private String key;

    private Aggregation(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
