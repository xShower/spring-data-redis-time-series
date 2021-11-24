# 工程简介
spring-data-redis-time-series是一个基于springboot、lettuce的快速集成redis-timeseries插件的启动器。

目前仅测试过redis单机模式

支持jdk8

# 使用方式
在根目录想执行
```shell
mvn clean install -Dmaven.test.skip=true
```
然后在项目中引入
```xml
    <dependency>
        <groupId>org.springframework.data</groupId>
        <artifactId>spring-data-redis-time-series</artifactId>
        <version>1.1.1-SNAPSHOT</version>
    </dependency>
```


代码中使用
```java
@Test
public void add() throws Exception {
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
public void range() {
    List<Value> resultList = operations
        .range(key,
        new RangeOptions().aggregationType(Aggregation.COUNT, 1000));
    resultList.forEach(v -> System.out.println("{\"timestamp\":"+ v.getTimestamp() +",\"value\":"+ v.getValue() +"}"));

    System.out.println(resultList.stream().mapToDouble(Value::getValue).sum());
}

```

