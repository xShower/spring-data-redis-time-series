package org.springframework.data.redis.core.protocol.entity;

import java.util.List;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class TimeSeries {
    private String key;
    private List<Label> labels;
    private List<Value> results;

    public TimeSeries(String key) {
        this.key = key;
    }

    public TimeSeries labels(List<Label> labels) {
        this.labels = labels;
        return this;
    }

    public TimeSeries results(List<Value> results) {
        this.results = results;
        return this;
    }

    public String getKey() {
        return this.key;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public List<Value> getResults() {
        return this.results;
    }
}
