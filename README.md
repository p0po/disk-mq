# disk-mq
一个基于磁盘的消息队列中间件
# 使用方法
在logback.xml中添加如下配置：

```xml
  <appender name="dmqFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${data-dir}${cluster-name}/new/write.data</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.MyFixedWindowRollingPolicy">
            <FileNamePattern>${data-dir}${cluster-name}/to-consume/%i</FileNamePattern>
            <minIndex>1</minIndex>
            <maxIndex>20</maxIndex>
        </rollingPolicy>

        <triggeringPolicy class="net.yongpo.MyTriggeringPolicy">

        </triggeringPolicy>

        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>

    <logger name="disk.writter" level="TRACE" additivity="false">
        <appender-ref ref="dmqFile"/>
    </logger>
```

```java
DiskMq diskMq = new DiskMqImpl("/export/data/", "test");

//添加消息
diskMq.add(msg);

//消费消息
String s = diskMq.peek();

//使用fastjson反序列化对象
E e = JSON.parseObject(s, E.class);
```
