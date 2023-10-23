---
typora-copy-images-to: imgs
---



参考文章：https://juejin.cn/post/7230646780455059515

> 待完善

Spring Event 其实是一个观察者设计模式，当一个 Bean 处理完任务之后，希望通知其它 Bean 或者说一个 Bean 想观察监听另一个Bean 的行为。



Spring Event 扩展使用：

- 项目增加异步线程池进行处理，提高主流程的响应时间，比如启动类上面增加 `@EnableAsync` 启动
- 定义某个方法上增加 `@EventListener` 注解定义为事件监听器去监听某个事件，更方便



> 项目中基于 Spring Event 的事件封装



1. 定义事件基础类

```java
/**
 * 商城事件基础类，基于Spring Event
 * @date 2023-01-04
 */
public class BasicMallEvent extends ApplicationEvent implements MallEvent {

    public BasicMallEvent(Object source) {
        super(source);
    }

}
```



2. 定义抽象事件监听器

```java
/**
 * 商城事件抽象配置的统一处理的监听器
 * @date 2023-01-04
 */
public abstract class AbstractMallEventListener implements ApplicationListener<BasicMallEvent> {

    public AbstractMallEventListener() {
    }

    @Override
    public void onApplicationEvent(BasicMallEvent event) {
        if(supportsEventType(event.getClass())){
            this.doMallEvent(event);
        }
    }

    /**
     * 判断子类的事件类型是否匹配
     * @param eventType 事件类型Class
     * @return 是否支持
     */
    public abstract boolean supportsEventType(Class<? extends BasicMallEvent> eventType);

    /**
     * 为子类暴露新的事件方法
     * @param event 事件对象
     */
    protected abstract void doMallEvent(BasicMallEvent event);


}
```





3. 定义事件管理器

```java
/**
 * 商城事件发布管理器
 * @date 2023-01-04
 */
public class MallEventManager{

    /**
     * 单例类
     */
    private static final MallEventManager INSTANCE = new MallEventManager();

    private MallEventManager(){
    }

    /**
     * 全局的单例的访问方法
     * @return 返回事件管理器的实例
     */
    public static MallEventManager getInstance() {
        return INSTANCE;
    }


    /**
     * 统一发布事件
     * @param mallEvent 商城事件 需继承自BasicMallEvent
     */
    public void publishEvent(BasicMallEvent mallEvent){
        SpringContextUtil.getContext().publishEvent(mallEvent);
    }

}
```



