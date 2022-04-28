package org.springframework.data.redis.core.protocol.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.CommandOutput;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.nio.ByteBuffer;

/**
 * @author syh
 * @date 2022/4/27
 * @description:
 */
public class ValueOutput<K, V> extends CommandOutput<K, V, Value> {

    public ValueOutput(RedisCodec<K, V> codec, Value output) {
        super(codec, output);
    }

    public ValueOutput(RedisCodec<K, V> codec) {
        this(codec, new Value());
    }

    @Override
    public void set(long timestamp) {
        output.setTimestamp(timestamp);
    }

    @Override
    public void set(ByteBuffer bytes) {
        output.setValue(Double.parseDouble(decodeAscii(bytes)));
    }
}
