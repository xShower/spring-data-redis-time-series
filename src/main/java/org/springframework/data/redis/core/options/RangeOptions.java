package org.springframework.data.redis.core.options;

import io.lettuce.core.KeyValue;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.Keywords;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class RangeOptions {
    private int count;
    private int align;
    private Aggregation aggregationType;
    private long timeBucket;
    private boolean withLabels;
    private List<Long> tsFilters;
    private List<Double> vlsFilters;
    private List<String> lables;
    private KeyValue filter;
    private String groupBy;


    public RangeOptions() {
    }

    public RangeOptions max(int count) {
        this.count = count;
        return this;
    }

    public RangeOptions aggregationType(Aggregation aggregationType, long timeBucket) {
        this.aggregationType = aggregationType;
        this.timeBucket = timeBucket;
        return this;
    }

    public RangeOptions withLabels() {
        this.withLabels = true;
        return this;
    }

    public Object[] build() {
        List<Object> options = new ArrayList<>();
        if (this.count > 0) {
            options.add(KeyValue.just(Keywords.COUNT.name(), this.count));
        }

        if (this.aggregationType != null) {
            options.add(Keywords.AGGREGATION.name());
            options.add(this.aggregationType.getKey());
            options.add(this.timeBucket);
        }

        if (this.withLabels) {
            options.add(Keywords.WITHLABELS.name());
        }
        return options.toArray();
    }
}
