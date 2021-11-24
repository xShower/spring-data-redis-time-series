package org.springframework.data.redis.core.options;

import io.lettuce.core.KeyValue;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class TimeSeriesOptions {

    private long retention;
    private long timestamp;
    private Boolean compressed;
    private int chunkSize;
    private DuplicatePolicy duplicatePolicy;
    private Label[] labels;

    public TimeSeriesOptions retention(long retention) {
        this.retention = retention;
        return this;
    }

    public TimeSeriesOptions timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public TimeSeriesOptions compressed(boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    public TimeSeriesOptions chunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
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

    public Object[] create() {
        List<Object> options = new ArrayList<>();
        if (this.retention > 0L) {
            options.add(KeyValue.just(Keywords.RETENTION.name(), String.valueOf(this.retention)));
        }

        if (!Objects.isNull(this.compressed)) {
            options.add(Keywords.ENCODING.name());
            options.add(this.compressed ? Keywords.COMPRESSED.name():Keywords.UNCOMPRESSED.name());
        }

        if (this.chunkSize > 0) {
            options.add(Keywords.CHUNK_SIZE.name());
            options.add(this.chunkSize);
        }

        if (this.duplicatePolicy != null) {
            options.add(Keywords.DUPLICATE_POLICY.name());
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

    public Object[] add() {
        List<Object> options = new ArrayList<>();
        if (this.retention > 0L) {
            options.add(KeyValue.just(Keywords.RETENTION.name(), String.valueOf(this.retention)));
        }

        if (!Objects.isNull(this.compressed)) {
            options.add(Keywords.ENCODING.name());
            options.add(this.compressed ? Keywords.COMPRESSED.name():Keywords.UNCOMPRESSED.name());
        }

        if (this.chunkSize > 0) {
            options.add(Keywords.CHUNK_SIZE.name());
            options.add(this.chunkSize);
        }

        if (this.duplicatePolicy != null) {
            options.add(Keywords.ON_DUPLICATE.name());
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

    public Object[] alter() {
        List<Object> options = new ArrayList<>();
        if (this.retention > 0L) {
            options.add(KeyValue.just(Keywords.RETENTION.name(), String.valueOf(this.retention)));
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

    public Object[] incrby() {
        List<Object> options = new ArrayList<>();
        if (this.timestamp > 0l) {
            options.add(Keywords.TIMESTAMP.name());
            options.add(timestamp);
        }

        if (this.retention > 0L) {
            options.add(KeyValue.just(Keywords.RETENTION.name(), String.valueOf(this.retention)));
        }

        if (!Objects.isNull(this.compressed) && !this.compressed) {
            options.add(Keywords.UNCOMPRESSED.name());
        }

        if (this.chunkSize > 0) {
            options.add(Keywords.CHUNK_SIZE.name());
            options.add(this.chunkSize);
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
