package org.springframework.data.redis.core.protocol;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public enum DuplicatePolicy {
    BLOCK,
    FIRST,
    LAST,
    MIN,
    MAX,
    SUM;
}
