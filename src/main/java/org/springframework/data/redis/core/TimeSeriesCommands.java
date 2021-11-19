package org.springframework.data.redis.core;

import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;

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
    void del(String key, long fromTimestamp, long toTimestamp);

    @Command("TS.ALTER")
    void alter(String key, Object[] options);

    @Command("TS.ADD")
    Long add(String key, long timestamp, String value, Object[] options);

    @Command("TS.MADD")
    Long mAdd(Object[] item);

    @Command("TS.INCRBY")
    void incrby(String key, Long value, Object[] options);

    @Command("TS.DECRBY")
    void decrby(String key, Long value, Object[] options);

    @Command("TS.CREATERULE")
    void createRule(String sourceKey, String destKey, Object[] options);

    @Command("TS.DELETERULE")
    void deleteRule(String sourceKey, String destKey);

    @Command("TS.RANGE")
    List<Object> range(String key, long fromTimestamp, long toTimestamp, Object[] options);

    @Command("TS.REVRANGE")
    List<Object> revRange(String key, long fromTimestamp, long toTimestamp, Object[] options);

    @Command("TS.MRANGE")
    void mRange(String key, long fromTimestamp, long toTimestamp, Object[] options);

    @Command("TS.MREVRANG")
    void mRevRange(String key, long fromTimestamp, long toTimestamp, Object[] options);

    @Command("TS.GET")
    List<Object> get(String key);

    @Command("TS.MGET")
    List<Object> mGet(Object[] options);

    @Command("TS.INFO")
    Map info(String key, Object[] options);

    @Command("TS.QUERYINDEX")
    List<Object> queryIndex(Object[] options);
}
