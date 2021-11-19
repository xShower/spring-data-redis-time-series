package org.springframework.data.redis.core;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class LettuceCommandsAbstractOperations<K,V,C extends Commands> extends AbstractOperations<K, V>{
    private ConcurrentHashMap<Integer,C> cached = new ConcurrentHashMap<>();

    LettuceCommandsAbstractOperations(RedisTemplate<K, V> template) {
        super(template);
    }

    protected C getCommands(RedisConnection connection, Class<C> clazz){

        Object nativeConnection = connection.getNativeConnection();

        C commands = cached.computeIfAbsent(System.identityHashCode(nativeConnection), integer -> {
            StatefulConnection statefulConnection = getStatefulConnection(nativeConnection);
            RedisCommandFactory redisCommandFactory = new RedisCommandFactory(statefulConnection);
            return redisCommandFactory.getCommands(clazz);
        });

        return commands;
    }

    private StatefulConnection getStatefulConnection(Object nativeConnection){
        if (nativeConnection instanceof RedisAsyncCommands){
            RedisAsyncCommands redisAsyncCommands = (RedisAsyncCommands) nativeConnection;
            return redisAsyncCommands.getStatefulConnection();
        }else if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands){
            RedisAdvancedClusterAsyncCommands redisAdvancedClusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
            return redisAdvancedClusterAsyncCommands.getStatefulConnection();
        }
        return null;
    }
}
