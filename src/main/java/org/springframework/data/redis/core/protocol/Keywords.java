package org.springframework.data.redis.core.protocol;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public enum Keywords {
    RETENTION,
    UNCOMPRESSED,
    COMPRESSED,
    LABELS,
    ENCODING,
    CHUNK_SIZE,
    TIMESTAMP,
    AGGREGATION,
    COUNT,
    WITHLABELS,
    FILTER,
    DUPLICATE_POLICY,
    ON_DUPLICATE,
    GROUPBY,
    FILTER_BY_TS,
    FILTER_BY_VALUE,
    REDUCE,
    ALIGN,
    SELECTED_LABELS;
}
