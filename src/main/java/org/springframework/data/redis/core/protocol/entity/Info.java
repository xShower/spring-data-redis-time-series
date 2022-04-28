package org.springframework.data.redis.core.protocol.entity;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author syh
 * @date 2022/4/28
 * @description:
 */
@Data
public class Info extends LinkedHashMap<String, Object> {

    private List<Label> labels;
    private List<Rule> rules;
    private List<Chunk> chunks;

    public Info(int count) {
        super(count);
    }
}
