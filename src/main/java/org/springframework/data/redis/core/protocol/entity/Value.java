package org.springframework.data.redis.core.protocol.entity;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class Value {
    private long timestamp;
    private double value;

    protected Value() {
    }

    protected Value(Long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public static Value just(String timestamp, String value) {
        return new Value(Long.parseLong(timestamp), Double.parseDouble(value));
    }

    public Value put(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getValue() {
        return value;
    }
}
