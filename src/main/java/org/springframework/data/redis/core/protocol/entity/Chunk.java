package org.springframework.data.redis.core.protocol.entity;

import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author syh
 * @date 2022/4/28
 * @description:
 */
@Data
public class Chunk extends LinkedHashMap<String, Object> {
}
