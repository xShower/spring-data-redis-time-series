package org.springframework.data.redis.core;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.core.decode.LabelDecoder;
import org.springframework.data.redis.core.decode.SampleDecoder;
import org.springframework.data.redis.core.decode.TimeSeriesDecoder;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Sample;
import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Slf4j
public class LettuceTimeSeriesOperations<K, V> extends LettuceCommandsAbstractOperations<K, V, TimeSeriesCommands> implements TimeSeriesOperations<K, V> {

    private TimeSeriesDecoder timeSeriesDecoder;
    private SampleDecoder sampleDecoder;


    public LettuceTimeSeriesOperations(RedisTemplate<K, V> template) {
        super(template);

        sampleDecoder = new SampleDecoder(template);

        timeSeriesDecoder = new TimeSeriesDecoder(template);
        timeSeriesDecoder.setSampleDecoder(sampleDecoder);
        timeSeriesDecoder.setLabelDecoder(new LabelDecoder(template));
    }

    @Override
    public void create(K key, TimeSeriesOptions options) {
        Object[] objects = options.create();
        byte[] rawKey = rawKey(key);

        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.create(new String(rawKey), objects);
            return null;
        }, true);
    }

    @Override
    public Long del(K key, long from, long to) {
        /*byte[] rawKey = rawKey(key);

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.del(new String(rawKey), from, to);
            return null;
        }, true);*/
        throw new UnsupportedOperationException("redis-timeseries_v.1.4版本不支持");
    }

    @Override
    public void alter(K key, TimeSeriesOptions options) {
        byte[] rawKey = rawKey(key);
        Object[] objects = options.alter();
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.alter(new String(rawKey), objects);
            return null;
        }, true);
    }

    @Override
    public Long add(Sample sample) {
        return add(sample, new TimeSeriesOptions().duplicatePolicy(DuplicatePolicy.SUM));
    }

    @Override
    public Long add(Sample sample, TimeSeriesOptions options) {
        byte[] rawKey = rawKey(sample.getKey());
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.add();

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.add(new String(rawKey), sample.getTimestamp(), sample.getValue(), objects);
        }, true);
    }

    @Override
    public Object mAdd(Sample... samples) {
        /*if (Objects.isNull(samples) || samples.length <= 0) return null;
        List<Object> objects = new ArrayList<>();
        for (Sample sample : samples) {
            objects.add(rawKey(sample.getKey()));
            objects.add(sample.getTimestamp());
            objects.add(rawValue(sample.getValue()));
        }
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mAdd(objects.toArray());
        }, true);*/
        throw new UnsupportedOperationException("redis-timeseries_v.1.4版本不支持");
    }

    @Override
    public void incrby(K key, V value) {
        incrby(key, value, null);
    }

    @Override
    public void incrby(K key, V value, Long timestamp) {
        incrby(key, value, timestamp, new TimeSeriesOptions().duplicatePolicy(DuplicatePolicy.SUM));
    }

    @Override
    public void incrby(K key, V value, Long timestamp, TimeSeriesOptions options) {
        options.timestamp(timestamp);
        Object[] opts = options.incrby();
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.incrby(new String(rawKey(key)), Double.parseDouble(value.toString()), opts);
            return null;
        }, true);
    }

    @Override
    public void decrby(K key, V value) {
        decrby(key, value, null);
    }

    @Override
    public void decrby(K key, V value, Long timestamp) {
        decrby(key, value, timestamp, new TimeSeriesOptions());
    }

    @Override
    public void decrby(K key, V value, Long timestamp, TimeSeriesOptions options) {
        options.timestamp(timestamp);
        Object[] opts = options.incrby();
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.decrby(new String(rawKey(key)), Double.parseDouble(value.toString()), opts);
            return null;
        }, true);
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
        }, true);

    }

    @Override
    public void deleteRule(K sourceKey, K destKey) {
        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.deleteRule(new String(rawKey(sourceKey)), new String(rawKey(destKey)));
            return null;
        }, true);
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
        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.range(sKey, from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        }, true);

        List<Value> result = new ArrayList<>();
        if (!Objects.isNull(value) && !value.isEmpty()) {
            for (Object v : value) {
                result.add(sampleDecoder.decode(sKey, v));
            }
        }

        return result;
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
        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.revRange(sKey, from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        }, true);


        List<Value> result = new ArrayList<>();
        if (!Objects.isNull(value) && !value.isEmpty()) {
            for (Object v : value) {
                result.add(sampleDecoder.decode(sKey, v));
            }
        }

        return result;
    }

    @Override
    public List<TimeSeries> mRange() {
        return mRange(null);
    }

    @Override
    public List<TimeSeries> mRange(RangeOptions options) {
        return mRange(-1, -1, options);
    }

    @Override
    public List<TimeSeries> mRange(long from, long to) {
        return mRange(from, to, null);
    }

    @Override
    public List<TimeSeries> mRange(long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.mRange();

        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mRange(from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        }, true);


        return timeSeriesDecoder.decode(null, value);
    }

    @Override
    public List<TimeSeries> mRevRange() {
        return mRevRange(null);
    }

    @Override
    public List<TimeSeries> mRevRange(RangeOptions option) {
        return mRevRange(-1, -1, option);
    }

    @Override
    public List<TimeSeries> mRevRange(long from, long to) {
        return mRevRange(from, to, null);
    }

    @Override
    public List<TimeSeries> mRevRange(long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.mRange();

        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mRevRange(from < 0 ? "-" : from, to < 0 ? "+" : to, objects);
        }, true);


        return timeSeriesDecoder.decode(null, value);
    }

    @Override
    public Sample get(K key) {
        byte[] rawKey = rawKey(key);
        List<Object> value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.get(new String(rawKey));
        }, true);

        return sampleDecoder.decode(new String(rawKey), value);
    }

    @Override
    public List<TimeSeries> mGet(RangeOptions options) {
        Object[] opts = options.mGet();
        List<Object> value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mGet(opts);
        }, true);

        return timeSeriesDecoder.decode(null, value);
    }

    @Override
    public Map info(K key) {
        return info(key, false);
    }

    @Override
    public Map info(K key, boolean debug) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> {
            Object[] objects = debug ? new Object[]{"DEBUG"} : new Object[]{};
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.info(new String(rawKey), objects);

        }, true);
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
        }, true);

        return (List) value.stream().map(o -> new String((byte[]) o)).collect(Collectors.toList());
    }
}
