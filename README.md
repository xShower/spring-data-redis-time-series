# 工程简介
spring-data-redis-time-series是一个基于springboot、lettuce的快速集成redis-timeseries插件的启动器。

目前仅测试过redis单机模式和predixy代理的redis集群。

redis需要导入redis time-series模块。

# redis部署
1.6以下版本不支持madd/mrange等命令，所以建议升级高版本redis-series插件。

docker部署redis-timeseries环境
```shell
### 该镜像的redis-timeseries版本为1.6，支持命令比较全面。
docker pull redistimeseries:edge
docker run -p 6379:6379 redistimeseries:edge
```

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
        <version>1.2.0-SNAPSHOT</version>
    </dependency>
```


项目中使用

```java
import org.springframework.beans.factory.annotation.Autowired;

@Autowired
private TimeSeriesOperations operations;

// add
operatioins.add("demo:redis:series", 1, new TimeSeriesOptions().labels(Label.just("os", "linux"), Label.just("app", "demo")));

// range: from=-1(-), to=-1(+)
operations.range("demo:redis:series", -1, -1, Aggregation.COUNT, 5 * 1000);

List<KeyedValue> keyedValueList = LettuceLists.newList(
        KeyedValue.just("demo:redis:series:key1").put(System.currentTimeMillis(), 10),
        KeyedValue.just("demo:redis:series:key2").put(System.currentTimeMillis(), 20)
        );
// madd
operations.madd(keyedValueList);

// mrange
operations.mRange(-1, -1, new RangeOptions()
        .withLabels()
        .filters(Label.just("os", "linux"))
        .groupBy("app", Reduce.SUM))

```

# 更新日志
> v1.2.0-SNAPSHOT  
+ 适配redis-timeseiresV1.6+命令
+ 去除decoder接口。使用原生CommandOutput解析返回数据。
    
