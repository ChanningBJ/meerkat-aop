# meerkat-apo

meerkat-apo 为 meerkat提供了注解的方式进行监控和降级的定义

# 初始化配置

meerkat-apo 是使用 spring + aspectj 实现的切面，需要在Spring配置中添加扫描的目录 com.meerkat.aop， 例如：

```java
@Configuration
@ComponentScan({"com.meerkat.aop"})
@EnableAspectJAutoProxy
public static class Config {
}
```

# 使用方式

最简当的使用方式是在函数上添加 MeerkatCommand 注解，可以完成对函数调用的成功率等数据的监控

```java
@Component
public class Logic {

    private Integer num = 0;
    
    @MeerkatCommand
    Integer exec10PercentNull(){
        num+=1;
        if(num%10==0){
            return null;
        } else {
            return num;
        }
    }
}
```