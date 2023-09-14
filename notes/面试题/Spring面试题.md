---
typora-copy-images-to: imgs
---

### 1、Spring

#### 1.1 Spring获取Bean的几种方式

- 在初始化时保存**ApplicationContext**对象：

  ```java
  ApplicationContext ac = new FileSystemXmlApplicationContext("applicationContext.xml");
  ac.getBean("bean");

  ApplicationContext beanFactory = new ClassPathXmlApplicationContext("springDemo01.xml");
  System.out.println(beanFactory);  
  Demo demo = (Demo) beanFactory.getBean("demo");
  demo.test();
  ```

  ​

- **实现接口ApplicationContextAware（推荐）**

  创建一个类实现ApplicationContextAware，之后这个类就可以获得ApplicationContext中的所有bean

  ```java
  import org.springframework.beans.BeansException;
  import org.springframework.context.ApplicationContext;
  import org.springframework.context.ApplicationContextAware;

  public class SpringContextUtil implements ApplicationContextAware {

      // Spring应用上下文环境
      private static ApplicationContext applicationContext;

      /**
       * 实现ApplicationContextAware接口的回调方法，设置上下文环境
       * 
       * @param applicationContext
       */
      public void setApplicationContext(ApplicationContext applicationContext) {
          SpringContextUtil.applicationContext = applicationContext;
      }

      /**
       * @return ApplicationContext
       */
      public static ApplicationContext getApplicationContext() {
          return applicationContext;
      }

      /**
       * 获取对象
       * @param name
       * @param <T>
       * @return
       * @throws BeansException
       */
      @SuppressWarnings("unchecked")
      public static <T> T getBean(String name) throws BeansException {
          if (applicationContext == null){
              return null;
          }
          return (T)applicationContext.getBean(name);
      }
  }
  ```

  ​

#### 1.2 BeanFactory和ApplicationContext

是Spring的两大接口，都可以作为Bean的容器。

（1）

BeanFactory是Spring最底层接口，是IOC的核心，定义了IOC的基本功能，包含了各种Bean的定义、加载、实例化，依赖注入和生命周期管理。

ApplicationContext接口作为BeanFactory的子类，除了提供BeanFactory所具有的功能外，还提供了更完整的框架功能。

（2）

BeanFactory采用延迟加载来注入Bean。

ApplicationContext在容器启动时，一次性创建了所有的Bean。

#### 1.3 Bean的作用域

（1）singleton：默认作用域，单例bean，每个容器中只有一个bean的实例。

（2）prototype：为每一个bean请求创建一个实例。

（3）request：为每一个request请求创建一个实例，在请求完成以后，bean会失效并被垃圾回收器回收。

（4）session：与request范围类似，同一个session会话共享一个实例，不同会话使用不同的实例。

（5）global-session：全局作用域，所有会话共享一个实例。如果想要声明让所有会话共享的存储变量的话，那么这全局变量需要存储在global-session中。

#### 1.4 Spring的Bean是否线程安全

Spring的bean不是线程安全的

（1）对于prototype（多例bean）作用域的Bean，每次都会创建一个新对象，所以不存在线程不安全的问题

（2）对于singleton（单例bean）作用域的Bean，所有线程共享一个Bean，因此存在线程不安全的问题。

如果Bean是无状态的，就是安全的，例如Controller、Service、Dao

如果Bean是有状态的就是不安全的。

**保证线程安全的方法：**

（1）将有状态的Bean的作用域改为prototyp

（2）采用TreadLocal解决线程安全问题，为每个线程提供一个独立的变量副本。

#### 1.5 注入bean

（1）Spring基于xml注入bean的几种方式

- set()方法注入；
- 构造器注入：①通过index设置参数的位置；②通过type设置参数类型；
- 静态工厂注入；

```java
// 静态工厂
public class DaoFactory { 
  public static final FactoryDao getStaticFactoryDaoImpl(){ 
      return new StaticFacotryDaoImpl(); 
  } 
}

// Spring对象
public class SpringAction { 
    //注入对象 
    private FactoryDao staticFactoryDao;

    public void staticFactoryOk(){ 
        staticFactoryDao.saveFactory(); 
    } 
    //注入对象的set方法 
    public void setStaticFactoryDao(FactoryDao staticFactoryDao) { 
        this.staticFactoryDao = staticFactoryDao; 
    } 
} 
// xml文件
<!--配置bean,配置后该类由spring管理--> 
<bean name="springAction" class="com.bless.springdemo.action.SpringAction" > 
<!--(3)使用静态工厂的方法注入对象,对应下面的配置文件(3)--> 
<property name="staticFactoryDao" ref="staticFactoryDao"></property> 
</property> 
</bean> 
<!--(3)此处获取对象的方式是从工厂类中获取静态方法--> 
<bean name="staticFactoryDao" class="com.bless.springdemo.factory.DaoFactory" factory-method="getStaticFactoryDaoImpl"></bean> 
```

#### 1.6 Spring常见的设计模式

（1）工厂模式：Spring使用工厂模式，通过BeanFactory和ApplicationContext来创建对象

（2）单例模式：Bean默认为单例模式

（3）策略模式：例如Resource的实现类，针对不同的资源文件，实现了不同方式的资源获取策略

（4）代理模式：Spring的AOP功能用到了JDK的动态代理和CGLIB字节码生成技术

（5）模板方法：可以将相同部分的代码放在父类中，而将不同的代码放入不同的子类中，用来解决代码重复的问题。比如RestTemplate, JmsTemplate, JpaTemplate

（6）适配器模式：Spring AOP的增强或通知（Advice）使用到了适配器模式，Spring MVC中也是用到了适配器模式适配Controller

（7）观察者模式：Spring事件驱动模型就是观察者模式的一个经典应用。

（8）桥接模式：可以根据客户的需求能够动态切换不同的数据源。比如我们的项目需要连接多个数据库，客户在每次访问中根据需要会去访问不同的数据库





### 2、Spring

#### 2.1 Spring源码Bean的生命周期

```java
Bean创建的生命后期：
UserService类--->推断构造方法--->获得普通对象--->进行依赖注入(给对象里边的属性进行赋值)--->初始化前--->初始化--->初始化后（AOP）--->获取代理对象--->放入单例池Map（如果有AOP将代理对象放入单例池，否则放普通对象）--->Bean

# 解释
# 推断构造方法：
当Spring创建对象的时候，会去判断选择哪一个构造方法，如果有无参构造方法，会默认选择无参构造方法；如果没有无参构造方法，有多个有参构造方法的话，就会报错，Spring不知道选择哪一个；如果只有一个有参构造方法，就会选择该方法。
当确定构造方法之后，就会确定构造方法的入参，比如下面这个构造方法
public userService(OrderService orderService){}
那么对于OrderService这个参数，Spring会先根据类型（byType）去找到可以匹配到的Bean，如果有一个就直接选择；如果有多个的话，就会再根据名字去匹配Bean（byName），如果仍然没有匹配到的话，就会报错。  


# 依赖注入：
通过反射去获得类的所有字段，判断字段是否有注解@Autowired，有的话进行注入值


# 初始化前：
找到那些有@PostConstruct的方法进行属性的注入


# 初始化： 
判断Service类有没有实现InitializingBean接口，如果实现的话，需要实现接口里边的afterPropertiesSet()方法 ，可以在该方法中将数据库中数据查询出来赋值到Service类的属性中 
初始化的时候，在afterPropertiesSer()方法中可能需要去数据库中查询数据，因为需要在属性赋值之后在执行afterPropertiesSet()方法，因为在数据库中查询需要使用到Mapper，那么必须等到Mapper注入值之后才能通过Mapper去数据库中查询。


#初始化后AOP：
AOP需要生成代理对象，这个代理对象是并没有进行属性注入的，如下：
class UserServiceProxy extends UserService {
  
  /**
      UserService普通对象中是有一个OrderService属性的，在代理对象中orderService并没有值，所以
  在代理对象中没有办法执行test方法，因此就需要注入原来的普通对象，通过普通对象来调用test()方法。
  */
  UserService target; 
  
  public void test(){
    // 先执行@Before切面逻辑
    // 再执行target.test()
  }
  
}

# 放入单例池Map：Spring默认是单例Bean（singleton），要获取单例Bean，需要使用Map<BeanName,Bean>来存储


```



![1650619727766](C:\Users\10203\Desktop\java_back_end_learning_notes\面试题\imgs/1650619727766.png)



#### 3、谈一下你对Spring的理解

总-分来描述

Spring是一个轻量级框架，可以简化开发，重点包含两个模块ioc和aop，使用了ioc之后，由容器帮我们创建对象和管理对象，提高开发效率；aop是面向切面编程，一些与业务无关的操作都可以在aop里边进行操作。



#### 4、Spring的后置处理器

> **扩展：**
>
> Spring框架有许多扩展点，这些扩展点定义了一些接口，在这些接口中默认是不执行操作，留给我们去自己进行扩展操作
>
> postProcessor称为后置处理器，也称为（增强器），主要有两个接口：
>
> （1）BeanFactoryPostProcessor（主要针对BeanFactory）
>
> 调用时机：在BeanFactory标准初始化之后调用，这时所有的bean定义已经保存加载到beanFactory中，但是bean的实例还未创建。
>
> 作用：来定制和修改BeanFactory的内容，如覆盖或添加属性，例如在xml文件中
>
> <property name="username" value="${jdbc.username}"></property>
>
> bean定义中的值为`${jdbc.username}`在执行完BeanFactoryPostProcessor就会将`${jdbc.username}`替换为jdbc.properties文件中的值。
>
> （2）BeanPostProcessor（主要针对Bean）



![1650466652553](C:\Users\10203\Desktop\java_back_end_learning_notes\面试题\imgs/1650466652553.png)

上图解释：

（1）先通过xml文件定义bean，之后将xml文件通过一系列操作变换（读取为io流，再进行读取），就得到了Bean的定义，设置到BeanDefinition中去。

（2）之后再通过BeanFactoryPostProcessor的扩展



![1650454600541](C:\Users\10203\Desktop\java_back_end_learning_notes\面试题\imgs/1650454600541.png)







