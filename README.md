# 工程简介
spring-data-redis-time-series是一个基于springboot、lettuce的快速集成redis-timeseries插件的启动器。

目前仅测试过redis单机模式
# 使用方式
在根目录想执行
```shell
mvn install
```
然后在项目中引入
```xml
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis-time-series</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
```


代码中使用
```java
public class TestRedis extends Tester{

    @Autowired
    TimeSeriesOperations timeSeriesOperations;

    @Test
    public void create() {
        String key = "temperature:2:33";

       /* System.out.println("======================create====================");
        timeSeriesOperations.create(key, new TimeSeriesOptions()
                .retention(50000)
                .labels(Label.just("area", "350301"), Label.just("year", "2021")));*/

        System.out.println("======================info====================");
        System.out.println(timeSeriesOperations.info(key));

        long start, end = System.currentTimeMillis();
        System.out.println("======================add====================");
        int count = 1000;
        for (int i = 0; i < count; i++) {
            long timestamp = System.currentTimeMillis();
            end = timestamp;
            System.out.println("{\"timestamp\":"+ timestamp +",\"value\":"+ i +"}");
            timeSeriesOperations.incrby(key, 1, timestamp);
        }

        System.out.println("======================range====================");
        List<Value> resultList = timeSeriesOperations
                .range(key, start, end, new RangeOptions().aggregationType(Aggregation.RANGE, 1000));
        resultList.forEach(v -> System.out.println(JSONObject.toJSONString(v)));

        System.out.println("======================alter====================");
        timeSeriesOperations.alter(key, new TimeSeriesOptions().labels(Label.just("area", "350302")));

        System.out.println("======================mget====================");
        List<Sample> samples = timeSeriesOperations.mGet(false, KeyValue.just("area", "350301"));
        System.out.println(samples);
    }
}

```

