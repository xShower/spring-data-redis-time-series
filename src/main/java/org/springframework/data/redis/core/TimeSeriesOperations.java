package org.springframework.data.redis.core;

import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.entity.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public interface TimeSeriesOperations<K, V> {

    void create(K key, TimeSeriesOptions options);

    Long del(K key, long from, long to);

    void alter(K key, TimeSeriesOptions options);

    Long add(KeyedValue keyedValue);

    Long add(KeyedValue keyedValue, TimeSeriesOptions options);

    List<Long> mAdd(KeyedValue...keyedValues);

    Long incrby(K key, V value);

    Long incrby(K key, V value, Long timestamp);

    Long incrby(K key, V value, Long timestamp, TimeSeriesOptions options);

    Long decrby(K key, V value);

    Long decrby(K key, V value, Long timestamp);

    Long decrby(K key, V value, Long timestamp, TimeSeriesOptions options);

    void createRule(K sourceKey, K destKey, Aggregation aggregationType, long timeBucket);

    void deleteRule(K sourceKey, K destKey);

    List<Value> range(K key);

    List<Value> range(K key, RangeOptions options);

    List<Value> range(K key, long from, long to);

    List<Value> range(K key, long from, long to, RangeOptions options);

    List<Value> revRange(K key);

    List<Value> revRange(K key, RangeOptions options);

    List<Value> revRange(K key, long from, long to);

    List<Value> revRange(K key, long from, long to, RangeOptions options);

    List<Range> mRange();

    List<Range> mRange(RangeOptions options);

    List<Range> mRange(long from, long to);

    List<Range> mRange(long from, long to, RangeOptions options);

    List<Range> mRevRange();

    List<Range> mRevRange(RangeOptions option);

    List<Range> mRevRange(long from, long to);

    List<Range> mRevRange(long from, long to, RangeOptions option);

    Value get(K key);

    Range mGet(RangeOptions options);

    Info info(K key);

    Info info(K key, boolean debug);

    List queryIndex(Label...filters);
}
