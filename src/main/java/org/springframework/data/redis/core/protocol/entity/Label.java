package org.springframework.data.redis.core.protocol.entity;

import lombok.Data;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Data
public class Label {
    private String key;
    private String value;

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

        OPERATOR(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    @Override
    public String toString() {
        if (operator == null) {
            return String.format("{\"%s\":\"%s\"}", key, value);
        } else {
            return String.format("%s %s %s", key, operator.getCode(), value);
        }
    }
}
