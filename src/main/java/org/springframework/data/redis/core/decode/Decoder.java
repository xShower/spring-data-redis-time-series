package org.springframework.data.redis.core.decode;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public interface Decoder<K, V> {

    V decode(K key, Object value);
}
