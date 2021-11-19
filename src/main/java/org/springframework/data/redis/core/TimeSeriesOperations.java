package org.springframework.data.redis.core;

import io.lettuce.core.KeyValue;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
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

    List<Value> range(K key, long from, long to);

    List<Value> range(K key, long from, long to, RangeOptions options);

    List<Value> revRange(K key, long from, long to);

    List<Value> revRange(K key, long from, long to, RangeOptions options);

    void mRange(K key, long from, long to, Object[] options);

    void mRevRange(K key, long from, long to, Object[] options);

    Sample get(K key);

    List<TimeSeries> mGet(boolean withLabels, KeyValue ...filters);

    Map info(K key);

    List queryIndex(KeyValue ...filters);
}
