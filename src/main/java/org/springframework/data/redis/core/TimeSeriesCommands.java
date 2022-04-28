package org.springframework.data.redis.core;

import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;
import org.springframework.data.redis.core.protocol.entity.Info;
import org.springframework.data.redis.core.protocol.entity.Range;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.util.List;
import java.util.Map;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public interface TimeSeriesCommands extends Commands {

    @Command("TS.CREATE")
    void create(String key, Object[] options);

    @Command("TS.DEL")
    Long del(String key, Object fromTimestamp, Object toTimestamp);

    @Command("TS.ALTER")
    void alter(String key, Object[] options);

    @Command("TS.ADD")
    Long add(String key, Long timestamp, Double value, Object[] options);

    @Command("TS.MADD")
    List<Long> mAdd(Object[] item);

    @Command("TS.INCRBY")
    Long incrby(String key, Double value, Object[] options);

    @Command("TS.DECRBY")
    Long decrby(String key, Double value, Object[] options);

    @Command("TS.CREATERULE")
    void createRule(String sourceKey, String destKey, Object[] options);

    @Command("TS.DELETERULE")
    void deleteRule(String sourceKey, String destKey);

    @Command("TS.RANGE")
    List<Value> range(String key, Object fromTimestamp, Object toTimestamp, Object[] options);

    @Command("TS.REVRANGE")
    List<Value> revRange(String key, Object fromTimestamp, Object toTimestamp, Object[] options);

    @Command("TS.MRANGE")
    List<Range> mRange(Object fromTimestamp, Object toTimestamp, Object[] options);

    @Command("TS.MREVRANGE")
    List<Range> mRevRange(Object fromTimestamp, Object toTimestamp, Object[] options);

    @Command("TS.GET")
    Value get(String key);

    @Command("TS.MGET")
    Range mGet(Object[] options);

    @Command("TS.INFO")
    Info info(String key, Object[] options);

    @Command("TS.QUERYINDEX")
    List<Object> queryIndex(Object[] options);
}
