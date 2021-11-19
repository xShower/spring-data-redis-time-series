package org.springframework.data.redis.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum TimeSeriesCommand {

    CREATE("TS.CREATE"),
    DEL("TS.DEL"),
    ALTER("TS.ALTER"),
    ADD("TS.ADD"),
    MADD("TS.MADD"),
    INCRBY("TS.INCRBY"),
    DECRBY("TS.DECRBY"),
    CREATERULE("TS.CREATERULE"),
    DELETERULE("TS.DELETERULE"),
    RANGE("TS.RANGE"),
    REVRANGE("TS.REVRANGE"),
    MRANGE("TS.MRANGE"),
    MREVRANGE("TS.MREVRANGE"),
    GET("TS.GET"),
    MGET("TS.MGET"),
    INFO("TS.INFO"),
    QUERYINDEX("TS.QUERYINDEX");

    private String command;
}
