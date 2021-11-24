package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Auther: syh
 * @Date: 2021/11/23
 * @Description:
 */
public abstract class AbstractDecoder<K, V> implements Decoder<K, V>{

    protected RedisTemplate redisTemplate;

    public AbstractDecoder(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
