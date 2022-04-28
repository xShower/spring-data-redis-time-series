package org.springframework.data.redis.core.protocol;

/**
 * @author syh
 * @date 2022/4/27
 * @description:
 */
public enum Reduce {
    SUM("sum"),
    MIN("min"),
    MAX("max");

    private String key;

    private Reduce(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
