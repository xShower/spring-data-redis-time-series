package org.springframework.data.redis.core.protocol.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.CommandOutput;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.Chunk;
import org.springframework.data.redis.core.protocol.entity.Info;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Rule;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author syh
 * @date 2022/4/28
 * @description:
 */
public class InfoOutput<K, V> extends CommandOutput<K, V, Info> {

    boolean initialized = false;
    String key = null;
    Rule rule;
    Label label;
    Chunk chunk = new Chunk();
    String chunkKey = null;
    int ruleCount = 0;
    int labelCount = 0;
    int chunkCount = 0;

    public InfoOutput(RedisCodec<K, V> codec) {
        this(codec, null);
    }

    /**
     * Initialize a new instance that encodes and decodes keys and values using the supplied codec.
     *
     * @param codec  Codec used to encode/decode keys and values, must not be {@code null}.
     * @param output Initial value of output.
     */
    public InfoOutput(RedisCodec<K, V> codec, Info output) {
        super(codec, output);
    }

    @Override
    public void set(ByteBuffer bytes) {
        String content = decodeAscii(bytes);
        if (key == null) {
            key = content;
            return;
        } else if (chunkCount > 0) {
            if (chunkKey == null) {
                chunkKey = content;
            }
            return;
        } else if (ruleCount > 0) {
            if (rule == null) {
                rule = new Rule(content);
            } else {
                rule.setAggregation(content);
                output.getRules().add(rule);
                rule = null;
                ruleCount--;
                if (ruleCount == 0) {
                    output.put(key, output.getRules());
                    output.setRules(null);
                    key = null;
                }
            }
            return;
        } else if (labelCount > 0) {
            if (label != null) {
                final Label l = label;
                l.setValue(content);
                output.getLabels().add(l);
                label = null;
                labelCount--;
                if (labelCount == 0) {
                    output.put(key, output.getLabels());
                    output.setLabels(null);
                    key = null;
                }
            } else {
                label = Label.just(content, null, null);
            }
            return;
        }

        output.put(key, content);
        key = null;
    }

    @Override
    public void set(long integer) {
        if (rule != null) {
            rule.setDuration(integer);
            return;
        }
        if (chunkKey != null) {
            chunk.put(chunkKey, integer);
            chunkKey = null;
            return;
        }

        output.put(key, integer);
        key = null;
    }

    @Override
    public void set(double number) {
        if (chunkKey != null) {
            chunk.put(chunkKey, number);
            final Chunk c = chunk;
            output.getChunks().add(c);
            chunk = new Chunk();
            chunkKey = null;
            return;
        }
        output.put(key, number);
        key = null;
    }

    @Override
    public void multi(int count) {
        if (!initialized) {
            output = new Info(count);
            initialized = true;
            return;
        }

        if (Keywords.LABELS.name().equalsIgnoreCase(key)) {
            if (count > 0) {
                if (output.getLabels() == null) {
                    output.setLabels(new ArrayList<>(count));
                    labelCount = count;
                }
            } else {
                key = null;
            }
        }

        if ("rules".equalsIgnoreCase(key)) {
            if (count > 0) {
                if (output.getRules() == null) {
                    output.setRules(new ArrayList<>(count));
                    ruleCount = count;
                }
            } else {
                key = null;
            }
        }
        if ("chunks".equalsIgnoreCase(key)) {
            if (count > 0) {
                if (output.getChunks() == null) {
                    output.setChunks(new ArrayList<>(count));
                    chunkCount = count;
                }
            } else {
                key = null;
            }
        }
    }

    @Override
    public void complete(int depth) {
        if (depth == 0) {
            output.put("Chunks", output.getChunks());
            output.setChunks(null);
        }
    }
}
