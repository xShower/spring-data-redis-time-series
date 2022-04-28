package org.springframework.data.redis.core;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.RedisCommandFactory;
import io.lettuce.core.dynamic.output.CommandOutputFactoryResolver;
import io.lettuce.core.dynamic.output.OutputRegistry;
import io.lettuce.core.dynamic.output.OutputRegistryCommandOutputFactoryResolver;
import io.lettuce.core.output.IntegerListOutput;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.protocol.output.*;

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
            redisCommandFactory.setCommandOutputFactoryResolver(commandOutputFactoryResolver());
            return redisCommandFactory.getCommands(clazz);
        });

        return commands;
    }

    private CommandOutputFactoryResolver commandOutputFactoryResolver() {
        OutputRegistry outputRegistry = new OutputRegistry();
        outputRegistry.register(ListValueOutput.class, ListValueOutput::new);
        outputRegistry.register(ValueOutput.class, ValueOutput::new);
        outputRegistry.register(IntegerListOutput.class, IntegerListOutput::new);
        outputRegistry.register(RangeOutput.class, RangeOutput::new);
        outputRegistry.register(ListRangeOutput.class, ListRangeOutput::new);
        outputRegistry.register(InfoOutput.class, InfoOutput::new);
        return new OutputRegistryCommandOutputFactoryResolver(outputRegistry);
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
