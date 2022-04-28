package org.springframework.data.redis.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Slf4j
public class LettuceTimeSeriesOperations<K, V> extends LettuceCommandsAbstractOperations<K, V, TimeSeriesCommands> implements TimeSeriesOperations<K, V> {

    public LettuceTimeSeriesOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    @Override
    public void create(K key, TimeSeriesOptions options) {
        Object[] objects = options.create();
        byte[] rawKey = rawKey(key);

        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.create(new String(rawKey), objects);
            return null;
        });
    }

    @Override
    public Long del(K key, long from, long to) {
        byte[] rawKey = rawKey(key);

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.del(new String(rawKey), from < 0 ? "-" : from, to < 0 ? "+" : to);
        });
    }

    @Override
    public void alter(K key, TimeSeriesOptions options) {
        byte[] rawKey = rawKey(key);
        Object[] objects = options.alter();
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.alter(new String(rawKey), objects);
            return null;
        });
    }

    @Override
    public Long add(KeyedValue keyedValue) {
        return add(keyedValue, new TimeSeriesOptions().duplicatePolicy(DuplicatePolicy.SUM));
    }

    @Override
    public Long add(KeyedValue keyedValue, TimeSeriesOptions options) {
        byte[] rawKey = rawKey(keyedValue.getKey());
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.add();

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.add(new String(rawKey), keyedValue.getTimestamp(), keyedValue.getValue(), objects);
        });
    }

    @Override
    public List<Long> mAdd(KeyedValue... keyedValues) {
        if (Objects.isNull(keyedValues) || keyedValues.length <= 0) return null;
        List<Object> objects = new ArrayList<>();
        for (KeyedValue keyedValue : keyedValues) {
            objects.add(rawKey(keyedValue.getKey()));
            objects.add(keyedValue.getTimestamp());
            objects.add(rawValue(keyedValue.getValue()));
        }
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mAdd(objects.toArray());
        });
    }

    @Override
    public Long incrby(K key, V value) {
        return incrby(key, value, null);
    }

    @Override
    public Long incrby(K key, V value, Long timestamp) {
        return incrby(key, value, timestamp, new TimeSeriesOptions().duplicatePolicy(DuplicatePolicy.SUM));
    }

    @Override
    public Long incrby(K key, V value, Long timestamp, TimeSeriesOptions options) {
        options.timestamp(timestamp);
        Object[] opts = options.incrby();
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.incrby(new String(rawKey(key)), Double.parseDouble(value.toString()), opts);
        });
    }

    @Override
    public Long decrby(K key, V value) {
        return decrby(key, value, null);
    }

    @Override
    public Long decrby(K key, V value, Long timestamp) {
        return decrby(key, value, timestamp, new TimeSeriesOptions());
    }

    @Override
    public Long decrby(K key, V value, Long timestamp, TimeSeriesOptions options) {
        options.timestamp(timestamp);
        Object[] opts = options.incrby();
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.decrby(new String(rawKey(key)), Double.parseDouble(value.toString()), opts);
        });
    }

    @Override
    public void createRule(K sourceKey, K destKey, Aggregation aggregationType, long timeBucket) {
        List<Object> opts = new ArrayList<>();
        opts.add(Keywords.AGGREGATION.name());
        opts.add(aggregationType.getKey());
        opts.add(timeBucket);

        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.createRule(new String(rawKey(sourceKey)), new String(rawKey(destKey)), opts.toArray());
            return null;
        });

    }

    @Override
    public void deleteRule(K sourceKey, K destKey) {
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.deleteRule(new String(rawKey(sourceKey)), new String(rawKey(destKey)));
            return null;
        });
    }

    @Override
    public List<Value> range(K key) {
        return range(key, -1, -1);
    }

    @Override
    public List<Value> range(K key, RangeOptions options) {
        return range(key, -1, -1, null);
    }

    @Override
    public List<Value> range(K key, long from, long to) {
        return range(key, from, to, null);
    }

    @Override
    public List<Value> range(K key, long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.range();
        String sKey = new String(rawKey(key));
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.range(sKey, from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        });
    }

    @Override
    public List<Value> revRange(K key) {
        return revRange(key, -1, -1);
    }

    @Override
    public List<Value> revRange(K key, RangeOptions options) {
        return revRange(key, -1, -1, options);
    }

    @Override
    public List<Value> revRange(K key, long from, long to) {
        return revRange(key, from, to, null);
    }

    @Override
    public List<Value> revRange(K key, long from, long to, RangeOptions options) {
        String sKey = new String(rawKey(key));
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.range();
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.revRange(sKey, from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        });
    }

    @Override
    public List<Range> mRange() {
        return mRange(null);
    }

    @Override
    public List<Range> mRange(RangeOptions options) {
        return mRange(-1, -1, options);
    }

    @Override
    public List<Range> mRange(long from, long to) {
        return mRange(from, to, null);
    }

    @Override
    public List<Range> mRange(long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.mRange();

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mRange(from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        });
    }

    @Override
    public List<Range> mRevRange() {
        return mRevRange(null);
    }

    @Override
    public List<Range> mRevRange(RangeOptions option) {
        return mRevRange(-1, -1, option);
    }

    @Override
    public List<Range> mRevRange(long from, long to) {
        return mRevRange(from, to, null);
    }

    @Override
    public List<Range> mRevRange(long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.mRange();

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mRevRange(from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        });
    }

    @Override
    public Value get(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.get(new String(rawKey));
        });
    }

    @Override
    public Range mGet(RangeOptions options) {
        Object[] opts = options.mGet();
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mGet(opts);
        });
    }

    @Override
    public Info info(K key) {
        return info(key, false);
    }

    @Override
    public Info info(K key, boolean debug) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> {
            Object[] objects = debug ? new Object[]{Keywords.DEBUG.name()} : new Object[]{};
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.info(new String(rawKey), objects);

        });
    }

    @Override
    public List queryIndex(Label... filters) {
        if (Objects.isNull(filters) || filters.length == 0) return null;
        List<Object> opts = new ArrayList<>(filters.length);

        for (Label filter : filters) {
            opts.add(filter.getKey() + filter.getOperator().getCode() + filter.getValue());
        }

        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.queryIndex(opts.toArray());
        });

        return (List) value.stream()
                .map(o -> deserializeString((byte[]) o))
                .collect(Collectors.toList());
    }
}
