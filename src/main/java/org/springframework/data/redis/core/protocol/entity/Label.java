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

    private final OPERATOR operator;

    private Label(String key, String value) {
        this(key, value, OPERATOR.EQUALS);
    }

    private Label(String key, String value, OPERATOR operator) {
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    public static Label just(String key, String value) {
        return new Label(key, value);
    }
    public static Label just(String key, String value, OPERATOR operator) {
        return new Label(key, value, operator);
    }

    public enum OPERATOR {
        EQUALS("="),
        NOT_EQUALS("!=");

        private String code;
        private OPERATOR(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}
