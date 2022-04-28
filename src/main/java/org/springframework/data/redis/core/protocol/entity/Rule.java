package org.springframework.data.redis.core.protocol.entity;

import lombok.Data;
import org.springframework.data.redis.core.protocol.Aggregation;

/**
 * @author syh
 * @date 2022/4/28
 * @description:
 */
@Data
public class Rule {
    String sourceKey;
    String destKey;
    String aggregation;
    long duration;

    public Rule() {}

    public Rule(String destKey) {
        this.destKey = destKey;
    }
}
