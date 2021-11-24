package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.protocol.entity.Sample;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class SampleDecoder extends AbstractDecoder<String, Sample> {
    public SampleDecoder(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    @Override
    public Sample decode(String key, Object value) {
        if (value instanceof List) {
            List<Object> parts = (List<Object>) value;
            if (!Objects.isNull(parts) && !parts.isEmpty()) {
                Object data = parts.get(1);
                Double result = new Double(0);
                if (data instanceof String) {
                    result = Double.parseDouble((String)data);
                } else if (data instanceof byte[]) {
                    result = Double.parseDouble(new String((byte[]) data));
                }
                return Objects.isNull(value) ? null : Sample.just(key).put((Long)parts.get(0), result);
            }
            return Objects.isNull(value) ? null : Sample.just(key).put(0, 0);
        } else {
            return Objects.isNull(value) ? null : Sample.just(key).put(0, 0);
        }
    }
}
