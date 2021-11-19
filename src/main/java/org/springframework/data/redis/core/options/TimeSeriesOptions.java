package org.springframework.data.redis.core.options;

import io.lettuce.core.KeyValue;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.Label;

import java.util.List;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class TimeSeriesOptions {

    private long retention;
    private boolean compressed = true;
    private DuplicatePolicy duplicatePolicy;
    private Label[] labels;

    public TimeSeriesOptions() {}


    public TimeSeriesOptions retention(long retention) {
        this.retention = retention;
        return this;
    }

    public TimeSeriesOptions compressed(boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    public TimeSeriesOptions duplicatePolicy(DuplicatePolicy duplicatePolicy) {
        this.duplicatePolicy = duplicatePolicy;
        return this;
    }

    public TimeSeriesOptions labels(Label ...labels) {
        this.labels = labels;
        return this;
    }

    public Object[] format(List<Object> options) {
        if (this.retention > 0L) {
            options.add(KeyValue.just(Keywords.RETENTION.name(), String.valueOf(this.retention)));
        }

        if (!this.compressed) {
            options.add(Keywords.UNCOMPRESSED.name());
        }

        if (this.duplicatePolicy != null) {
            // args.add(this.isAdd ? Keywords.ON_DUPLICATE : Keywords.DUPLICATE_POLICY);
            options.add(this.duplicatePolicy.name());
        }

        if (this.labels != null) {
            options.add(Keywords.LABELS.name());

            for(int i = 0; i < this.labels.length; ++i) {
                Label label = this.labels[i];
                options.add(KeyValue.just(label.getKey(), label.getValue()));
            }
        }
        return options.toArray();
    }
}
