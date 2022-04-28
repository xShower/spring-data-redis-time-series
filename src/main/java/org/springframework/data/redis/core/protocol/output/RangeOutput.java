package org.springframework.data.redis.core.protocol.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.CommandOutput;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Range;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * @author syh
 * @date 2022/4/27
 * @description:
 */
public class RangeOutput<K, V> extends CommandOutput<K, V, Range> {

    String key;
    Value value;
    Label label;
    List<Value> valueList = new ArrayList<>();
    List<Label> labelList = new ArrayList<>();

    public RangeOutput(RedisCodec<K, V> codec) {
        this(codec, null);
    }

    /**
     * Initialize a new instance that encodes and decodes keys and values using the supplied codec.
     *
     * @param codec  Codec used to encode/decode keys and values, must not be {@code null}.
     * @param output Initial value of output.
     */
    public RangeOutput(RedisCodec<K, V> codec, Range output) {
        super(codec, output);
    }

    @Override
    public void set(ByteBuffer bytes) {
        if (key == null) {
            key = decodeAscii(bytes);
        } else if (value != null) {
            final Value val = value;
            double v = (bytes == null) ? 0 : parseDouble(decodeAscii(bytes));
            val.setValue(v);
            valueList.add(val);
            value = null;
            return;
        } else if (label != null) {
            final Label lab = label;
            lab.setValue(decodeAscii(bytes));
            labelList.add(lab);
            label = null;
        } else {
            label = Label.just(decodeAscii(bytes), null, null);
        }
    }

    @Override
    public void set(long timestamp) {
        value = new Value();
        value.setTimestamp(timestamp);
    }

    @Override
    public void complete(int depth) {
        if (depth == 0) {
            Range range = new Range(key);
            range.setLabels(labelList);
            range.setValues(valueList);
            output = range;
        }
    }
}
