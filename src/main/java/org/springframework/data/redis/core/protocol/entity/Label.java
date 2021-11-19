package org.springframework.data.redis.core.protocol.entity;

import lombok.Getter;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Getter
public class Label {
    private final String key;
    private final String value;

    private Label(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Label just(String key, String value) {
        return new Label(key, value);
    }
}
