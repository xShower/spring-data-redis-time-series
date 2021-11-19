package org.springframework.data.redis.core;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.core.decode.SampleDecoder;
import org.springframework.data.redis.core.decode.TimeSeriesDecoder;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.entity.Sample;
import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;

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
        Object[] objects = options.format(new ArrayList<>(), false);
        byte[] rawKey = rawKey(key);

        execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.create(new String(rawKey), objects);
            return null;
        }, true);
    }

    @Override
    public Long del(K key, long from, long to) {
        byte[] rawKey = rawKey(key);

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            commands.del(new String(rawKey), from, to);
            return null;
        }, true);
    }

    @Override
    public void alter(K key, TimeSeriesOptions options) {
        byte[] rawKey = rawKey(key);
        Object[] objects = options.format(new ArrayList<>(), false);
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
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.format(new ArrayList<>(), true);

        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.add(new String(rawKey), sample.getTimestamp(), sample.getValue(), objects);
        }, true);
    }

    @Override
    public Object mAdd(Sample... samples) {
        if (Objects.isNull(samples) || samples.length <= 0) return null;
        List<Object> objects = new ArrayList<>();
        for (Sample sample : samples) {
            objects.add(rawKey(sample.getKey()));
            objects.add(sample.getTimestamp());
            objects.add(rawValue(sample.getValue()));
        }
        return execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mAdd(objects.toArray());
        }, true);
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
        List<Object> objects = new ArrayList<>();
        if (!Objects.isNull(timestamp)) {
            objects.add(Keywords.TIMESTAMP.name());
            objects.add(timestamp);
        }
        Object[] opts = options.format(objects, false);
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
        List<Object> objects = new ArrayList<>();
        if (!Objects.isNull(timestamp)) {
            objects.add(Keywords.TIMESTAMP.name());
            objects.add(timestamp);
        }
        Object[] opts = options.format(objects, false);
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
    public List<Value> range(K key, long from, long to) {
        return range(key, from, to, null);
    }

    @Override
    public List<Value> range(K key, long from, long to, RangeOptions options) {
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.build();
        String sKey = new String(rawKey(key));
        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.range(sKey, from, to, objects);
        }, true);

        List<Value> result = new ArrayList<>();
        SampleDecoder decoder = new SampleDecoder();
        if (!Objects.isNull(value) && !value.isEmpty()) {
            for (Object v : value) {
                result.add(decoder.decode(sKey, v));
            }
        }

        return result;
    }

    @Override
    public List<Value> revRange(K key, long from, long to) {
        return revRange(key, from, to, null);
    }

    @Override
    public List<Value> revRange(K key, long from, long to, RangeOptions options) {
        String sKey = new String(rawKey(key));
        Object[] objects = Objects.isNull(options) ? new Object[]{} : options.build();
        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.revRange(sKey, from, to, objects);
        }, true);


        List<Value> result = new ArrayList<>();
        SampleDecoder decoder = new SampleDecoder();
        if (!Objects.isNull(value) && !value.isEmpty()) {
            for (Object v : value) {
                result.add(decoder.decode(sKey, v));
            }
        }

        return result;
    }

    @Override
    public void mRange(K key, long from, long to, Object[] options) {
        throw new UnsupportedOperationException("开发中");
    }

    @Override
    public void mRevRange(K key, long from, long to, Object[] options) {
        throw new UnsupportedOperationException("开发中");
    }

    @Override
    public Sample get(K key) {
        byte[] rawKey = rawKey(key);
        List<Object> value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.get(new String(rawKey));
        }, true);

        return new SampleDecoder().decode(new String(rawKey), value);
    }

    @Override
    public List<TimeSeries> mGet(boolean withLabels, KeyValue... filters) {
        int len = (Objects.isNull(filters) || filters.length == 0) ? 0 : filters.length;
        List<Object> opts = new ArrayList<>(len + (withLabels ? 2 : 1));
        if (withLabels) {
            opts.add(Keywords.WITHLABELS.name());
        }
        if (len > 0) {
            opts.add(Keywords.FILTER.name());
            for (KeyValue filter : filters) {
                opts.add(filter.getKey() + "=" + filter.getValue());
            }
        }
        List<Object> value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.mGet(opts.toArray());
        }, true);

        return new TimeSeriesDecoder().decode(null, value);
    }

    @Override
    public Map info(K key) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> {

            try {
                TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
                Map info = commands.info(new String(rawKey), new Object[]{});
                return info;
            } catch (InvalidDataAccessApiUsageException | RedisCommandExecutionException e) {
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }, true);
    }

    @Override
    public List queryIndex(KeyValue... filters) {
        if (Objects.isNull(filters) || filters.length == 0) return null;
        List<Object> opts = new ArrayList<>(filters.length);

        for (KeyValue filter : filters) {
            opts.add(filter.getKey() + "=" + filter.getValue());
        }
        List value = execute(connection -> {
            TimeSeriesCommands commands = getCommands(connection, TimeSeriesCommands.class);
            return commands.queryIndex(opts.toArray());
        }, true);

        return (List) value.stream().map(o -> new String((byte[]) o)).collect(Collectors.toList());
    }
}
