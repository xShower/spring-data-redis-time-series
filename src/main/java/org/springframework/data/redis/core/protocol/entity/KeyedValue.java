package org.springframework.data.redis.core.protocol.entity;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class KeyedValue extends Value {

    private final String key;

    private KeyedValue() {
        this.key = null;
    }

    private KeyedValue(String key) {
        this.key = key;
    }

    public static KeyedValue just(String key) {
        return new KeyedValue(key);
    }

    public KeyedValue put(long timestamp, double value) {
        super.put(timestamp, value);
        return this;
    }

    public String getKey() {
        return key;
    }
}
