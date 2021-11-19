package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.RedisClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.LettuceTimeSeriesOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeSeriesOperations;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
@Configuration
@ConditionalOnBean(RedisTemplate.class)
public class TimeSeriesConfiguration {

    @ConditionalOnClass({RedisClient.class})
    static class LettuceTimeSeriesConfiguration {

        @Bean
        @ConditionalOnMissingBean(TimeSeriesOperations.class)
        public TimeSeriesOperations timeSeriesOperations(RedisTemplate redisTemplate) {
            return new LettuceTimeSeriesOperations(redisTemplate);
        }
    }
}
