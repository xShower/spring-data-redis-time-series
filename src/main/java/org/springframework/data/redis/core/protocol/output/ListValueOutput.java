package org.springframework.data.redis.core.protocol.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.ListSubscriber;
import io.lettuce.core.output.StreamingOutput;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * @author syh
 * @date 2022/4/27
 * @description:
 */
public class ListValueOutput<K, V> extends CommandOutput<K, V, List<Value>> implements StreamingOutput<Value> {

    Subscriber<Value> subscriber;

    Long timestamp = null;
    boolean initialized = false;

    public ListValueOutput(RedisCodec<K, V> codec, List<Value> output) {
        super(codec, output);
        setSubscriber(ListSubscriber.instance());
    }

    public ListValueOutput(RedisCodec<K, V> codec) {
        this(codec, null);
    }

    @Override
    public void set(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void set(ByteBuffer bytes) {
        double value = (bytes == null) ? 0 : parseDouble(decodeAscii(bytes));
        subscriber.onNext(output, Value.just(String.valueOf(timestamp), String.valueOf(value)));
        this.timestamp = null;
    }

    @Override
    public void multi(int count) {
        if (!initialized) {
            if (count < 1) {
                output = Collections.emptyList();
            } else {
                output = new ArrayList<>(Math.max(1, count));
            }

            initialized = true;
            return;
        }
    }

    @Override
    public void setSubscriber(Subscriber<Value> subscriber) {
        LettuceAssert.notNull(subscriber, "Subscriber must not be null");
        this.subscriber = subscriber;
    }

    @Override
    public Subscriber<Value> getSubscriber() {
        return subscriber;
    }
}
