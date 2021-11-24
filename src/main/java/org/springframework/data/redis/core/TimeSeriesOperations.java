package org.springframework.data.redis.core;

import io.lettuce.core.KeyValue;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Sample;
import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;

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

    Long add(Sample sample);

    Long add(Sample sample, TimeSeriesOptions options);

    Object mAdd(Sample...sample);

    void incrby(K key, V value);

    void incrby(K key, V value, Long timestamp);

    void incrby(K key, V value, Long timestamp, TimeSeriesOptions options);

    void decrby(K key, V value);

    void decrby(K key, V value, Long timestamp);

    void decrby(K key, V value, Long timestamp, TimeSeriesOptions options);

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

    List<TimeSeries> mRange();

    List<TimeSeries> mRange(RangeOptions options);

    List<TimeSeries> mRange(long from, long to);

    List<TimeSeries> mRange(long from, long to, RangeOptions options);

    List<TimeSeries> mRevRange();

    List<TimeSeries> mRevRange(RangeOptions option);

    List<TimeSeries> mRevRange(long from, long to);

    List<TimeSeries> mRevRange(long from, long to, RangeOptions option);

    Sample get(K key);

    List<TimeSeries> mGet(RangeOptions options);

    Map info(K key);

    Map info(K key, boolean debug);

    List queryIndex(Label...filters);
}
