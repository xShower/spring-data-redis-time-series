package org.springframework.data.redis.core.protocol.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.ListSubscriber;
import io.lettuce.core.output.StreamingOutput;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Range;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * @author syh
 * @date 2022/4/28
 * @description:
 */
public class ListRangeOutput<K, V> extends CommandOutput<K, V, List<Range>> implements StreamingOutput<Range> {

    boolean initialized = false;
    Boolean withLabels = null;
    int depth = 0;
    // String key;
    Range range;
    Value value;
    Label label;

    Subscriber<Range> subscriber;

    public ListRangeOutput(RedisCodec<K, V> codec) {
        this(codec, null);
    }

    /**
     * Initialize a new instance that encodes and decodes keys and values using the supplied codec.
     *
     * @param codec  Codec used to encode/decode keys and values, must not be {@code null}.
     * @param output Initial value of output.
     */
    public ListRangeOutput(RedisCodec<K, V> codec, List<Range> output) {
        super(codec, output);
        setSubscriber(ListSubscriber.instance());
    }


    @Override
    public void setSubscriber(Subscriber<Range> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public Subscriber<Range> getSubscriber() {
        return subscriber;
    }

    @Override
    public void set(ByteBuffer bytes) {
        String content = decodeAscii(bytes);
        if (range == null) {
            range = new Range(content);
        } else if (value != null) {
            double v = (bytes == null) ? 0 : parseDouble(content);
            final Value val = value;
            val.setValue(v);
            range.getValues().add(val);
            value = null;
        } else if (label != null) {
            final Label lab = label;
            lab.setValue(content);
            range.getLabels().add(lab);
            label = null;
        } else {
            label = Label.just(content, null, null);
            if (range.getLabels() == null) {
                range.setLabels(new ArrayList<>());
            }
        }
    }

    @Override
    public void set(long timestamp) {
        value = new Value();
        value.setTimestamp(timestamp);
        if (range.getValues() == null) {
            range.setValues(new ArrayList<>());
        }
    }

    @Override
    public void multi(int count) {
        if (!initialized) {
            output = new ArrayList<>(Math.max(1, count));
            initialized = true;
            return;
        } else if (withLabels == null) {
            withLabels = count == 3;
        }
    }

    @Override
    public void complete(int depth) {
        this.depth = depth;
        if (depth == 2 && range.getValues() != null && !range.getValues().isEmpty()) {
            final Range r = range;
            subscriber.onNext(output, r);
            range = null;
            label = null;
        }
    }
}
