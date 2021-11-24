package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Sample;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class LabelDecoder extends AbstractDecoder<String, Label> {
    public LabelDecoder(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public Label decode(String key, Object value) {
        if (value instanceof List) {
            List v = (List) value;
            return Label.just(redisTemplate.getKeySerializer().deserialize((byte[])v.get(0)).toString(),
                    redisTemplate.getValueSerializer().deserialize((byte[])v.get(1)).toString());
        } else {
            return null;
        }
    }
}
