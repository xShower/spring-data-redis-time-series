package org.springframework.data.redis.core.protocol.entity;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class Sample extends Value {

    private final String key;

    private Sample() {
        this.key = null;
    }

    private Sample(String key) {
        this.key = key;
    }

    public static Sample just(String key) {
        return new Sample(key);
    }

    public Sample put(long timestamp, double value) {
        super.put(timestamp, value);
        return this;
    }

    public String getKey() {
        return key;
    }
}
