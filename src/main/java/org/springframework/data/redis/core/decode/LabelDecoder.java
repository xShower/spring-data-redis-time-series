package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Sample;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class LabelDecoder implements Decoder<String, Label> {
    @Override
    public Label decode(String key, Object value) {
        if (value instanceof List) {
            return null;
        } else {
            return null;
        }
    }
}
