package org.springframework.data.redis.core.protocol.entity;

import lombok.Data;

import java.util.List;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Data
public class Range {
    private String key;
    private List<Label> labels;
    private List<Value> values;

    public Range(String key) {
        this.key = key;
    }

    public Range labels(List<Label> labels) {
        this.labels = labels;
        return this;
    }

    public Range results(List<Value> values) {
        this.values = values;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public List<Value> getValues() {
        return this.values;
    }
}
