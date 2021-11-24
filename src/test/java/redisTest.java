import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.KeyValue;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.LettuceTimeSeriesOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeSeriesOperations;
import org.springframework.data.redis.core.options.RangeOptions;
import org.springframework.data.redis.core.options.TimeSeriesOptions;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.DuplicatePolicy;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.data.redis.core.protocol.entity.Sample;
import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class redisTest {

    private RedisTemplate<String, Object> template;
    private TimeSeriesOperations operations;
    private static final String host = "47.111.95.91";
    private static final int port = 6389;
    private static final String password = "ylz#yhkj..2020";
    private static final String key = "temperature:x:32";

    List<String> keys = new ArrayList<String>(){{
        add(key);
        add("cpu:2:32");
        add("io:2:32");
        add("hd:2:32");
        add("ps:2:32");
    }};
    long start = 0l;
    long end = System.currentTimeMillis();

    @Before
    public void init() throws Exception {
        LettuceConnectionFactory factory = lettuceConnectionFactory();
        factory.afterPropertiesSet();


        template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);



        //Json序列化配置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator());
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //解决序列化问题
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        //value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);

        //hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        operations = new LettuceTimeSeriesOperations(template);
    }

    private LettuceConnectionFactory lettuceConnectionFactory() throws Exception {

//        List<String> clusterNodes = redisProperties.getCluster().getNodes();
//        Set<RedisNode> nodes = new HashSet<>();
//        clusterNodes.forEach(address -> nodes.add(new RedisNode(address.split(":")[0].trim(), Integer.parseInt(address.split(":")[1]))));
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//        clusterConfiguration.setClusterNodes(nodes);
//        clusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
//        clusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());

        //我使用的是单机redis，集群使用上面注释的代码

        RedisStandaloneConfiguration redisStandaloneConfiguration=new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPassword(password);
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setPort(port);

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxTotal(15);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, getLettuceClientConfiguration(poolConfig));
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(GenericObjectPoolConfig genericObjectPoolConfig) {
        /*
        ClusterTopologyRefreshOptions配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常！
         */
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //开启自适应刷新
                //.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                //开启所有自适应刷新，MOVED，ASK，PERSISTENT都会触发
                .enableAllAdaptiveRefreshTriggers()
                // 自适应刷新超时时间(默认30秒)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(25)) //默认关闭开启后时间为30秒
                // 开周期刷新
                .enablePeriodicRefresh(Duration.ofSeconds(20))  // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                .build();
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig)
                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
                //将appID传入连接，方便Redis监控中查看
                //.clientName(appName + "_lettuce")
                .build();
    }

    @Test
    public void create() {
        operations.create(key, new TimeSeriesOptions()
                .retention(2*24*68*68*1000)
                        .compressed(true)
                        .chunkSize(8192)
                        .duplicatePolicy(DuplicatePolicy.SUM)
                .labels(Label.just("app_id", "350301"), Label.just("year", "2021")));
    }

    @Test
    public void delKey() {
        template.delete(key);
    }

    @Test
    public void del() {
        operations.del(key, start, end);
    }

    @Test
    public void alter() {
        operations.alter(key, new TimeSeriesOptions().retention(1*24*60*60*1000).labels(Label.just("area", "350302")));
    }

    @Test
    public void add() throws Exception {
        // delKey();
        int count = 10;
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            end = System.currentTimeMillis();
            operations.add(Sample.just(key).put(end, 1),
                    new TimeSeriesOptions()
                            .retention(2*24*60*60*1000)
                            .compressed(true)
                            .chunkSize(4096)
                            .duplicatePolicy(DuplicatePolicy.SUM)
                            .labels(Label.just("id_type", "1")));
        }

    }

    @Test
    public void mAdd() {
    }

    @Test
    public void incrby() throws Exception {
        int count = 10;
        for (int i = 0; i < count; i++) {
            long timestamp = System.currentTimeMillis();
            start = i == 0 ? timestamp : start;
            end = timestamp;
            operations.incrby(key+":sub", 1, timestamp, new TimeSeriesOptions()
                    .retention(2*24*60*60*1000)
                    .compressed(false)
                    .chunkSize(4096)
                    .labels(Label.just("id_type", "1"))
            );
        }

        // range();
        // revRange();
        // mGet();
    }

    @Test
    public void decrby() {
        operations.decrby(key, 12, System.currentTimeMillis());
    }

    @Test
    public void createRule() {
        operations.createRule(key, key+":sub", Aggregation.COUNT, 1000);
    }

    @Test
    public void deleteRule() {
        operations.deleteRule(key, key+":sub");
    }

    @Test
    public void range() {
        List<Value> resultList = operations
                .range(key,
                        new RangeOptions().aggregationType(Aggregation.COUNT, 1000));
        resultList.forEach(v -> System.out.println("{\"timestamp\":"+ v.getTimestamp() +",\"value\":"+ v.getValue() +"}"));

        System.out.println(resultList.stream().mapToDouble(Value::getValue).sum());
    }

    @Test
    public void revRange() {

        List<Value> resultList = operations
                .revRange(key, new RangeOptions()
                        .tsFilters(1637651385435l, 1637651655361l)
                        .vlsFilter(true)
                        .count(5)
                        .align(1)
                        .aggregationType(Aggregation.COUNT, 5000));

        resultList.forEach(v -> System.out.println("{\"timestamp\":"+ v.getTimestamp() +",\"value\":"+ v.getValue() +"}"));
    }

    @Test
    public void mRange() {
        List<TimeSeries> resultList = operations
                .mRange(new RangeOptions()
                        .tsFilters(1637651385435l, 1637651655361l)
                        .vlsFilter(true)
                        .withLabels()
                        .count(5)
                        .align(1)
                        .aggregationType(Aggregation.COUNT, 5000)
                        .filters(Label.just("area", "350302"))
                );
        System.out.println(resultList);
        // resultList.forEach(v -> System.out.println("{\"timestamp\":"+ v.getTimestamp() +",\"value\":"+ v.getValue() +".0}"));
    }

    @Test
    public void mRevRange() {
        List<TimeSeries> resultList = operations
                .mRevRange(new RangeOptions()
                        .tsFilters(1637651385435l, 1637651655361l)
                        .vlsFilter(true)
                        .withLabels()
                        .count(5)
                        .align(1)
                        .aggregationType(Aggregation.COUNT, 1000)
                        .filters(Label.just("area", "350302"))
                );
        System.out.println(resultList);
    }

    @Test
    public void get() {
        Sample sample = operations.get(key);
        System.out.println(sample);
    }

    @Test
    public void mGet() {
        List<Sample> samples = operations.mGet(new RangeOptions()
                .withLabels().filters(Label.just("area", "350302")));
        System.out.println(samples);
    }

    @Test
    public void info() {
        Map info = operations.info(key);
        System.out.println(info);
    }

    @Test
    public void queryIndex() {
        List info = operations.queryIndex(Label.just("area", "350302"));
        System.out.println(info);
    }

}
