---
typora-copy-images-to: img
---

# 1、介绍



## 介绍一下 API 网关吧？

面试官您好，API 网关系统的核心目的是用于解决公司中各类服务的统一出口问题，并且将与业务无关的一些共性功能进行统一封装使用，这包括`鉴权、限流、监控、熔断、切量`，API 网关可以将内部的 RPC 服务以及可扩展的 MQ、SQL、任务等资源，通过 HTTP 对外提供调用，让 APP、WEB、小程序等等有一个统一标准的接入方式

整个项目从架构上被划分为 Netty 通信、服务助手、启动引擎、注册中心、上报接口 sdk 共 5 个模块

**鉴权实现：**

鉴权部分我们通过 `shiro + jwt` 实现，将鉴权操作添加到 Netty 的处理通道中，当请求 API 网关时需要携带 `jwt token`，在鉴权中会通过 shiro 进行认证操作 `subject.login()`，将我们的 `jwt token` 解析操作放在 realm 中，如果 token 解析成功，则鉴权通过；否则鉴权失败

**如何对API网关进行负载均衡：**

通过 nginx 来实现负载均衡，将 API 网关中的`算力模块`（也就是 Netty 核心通信模块）部署多份，通过 nginx 的 upstream 来实现负载均衡，其中负载均衡策略使用最小连接数`least_conn`，即优先选择连接数最少的应用

**如何上报应用的接口信息呢？**

对于服务的 rpc 接口如何暴露出去给网关项目使用呢？其实是我们自己通过 SpringBoot 实现的 starter 即 `上报接口模块` 来实现的，比如此时一个新的 web 应用需要将 rpc 接口给暴露出去，那么就可以引入 `上报接口` 模块，之后通过使用 `上报接口` 模块中定义的注解，标注在需要暴露的方法上即可，就可以将 rpc 接口注册在数据库中去了

**这时我们如何保证 `算力模块` 中保存的 rpc 接口是最新的呢？**

这个通过 Redis 的 `发布/订阅` 功能来实现，即如果有新的 rpc 接口注册进数据库中了，那么我们只需要通过 Redsi 发布一条通知，并在 `算力模块` 中订阅这个通知，当收到通知后，去数据库中拉取新的服务就行，这样就可以保证一直可以调用最新 rpc 接口

**网关接收 HTTP 之后是如何调用到对应的 rpc 服务的呢？**

首先当使用 HTTP 请求 API 网关时，HTTP 请求会由 Netty 通信模块进行解析，解析出 url 以及参数

> - 在启动 Netty 通信模块时，会通过 `注册中心模块` 去数据库中拉取 url 和 rpc 接口的对应关系
> - 在服务助手模块启动时，会实现 ApplicationContextAware 接口，那么在 `setApplicationContext()` 方法中，会增加一些我们自己的额外操作，这里会去注册中心拉取数据库中的 `uri 和 rpc接口名称` 的对应关系，并且创建一个 `uri -> cglib 动态代理对象` 的映射

那么在解析出 url 的路径之后，就可以就会通过 `uri` 的路径获取到对应的 `cglib 动态代理对象`，那么执行 `代理对象` 的方法，就会进入到拦截器中，会在拦截器中通过 Dubbo 连接获取 Dubbo 的`泛化调用接口`，再通过在 `泛化调用接口` 中传入需要调用的方法以及参数信息，就可以完成 rpc 调用



## API 网关的亮点在哪里呢？

亮点是将 HTTP 协议给转换成 RPC 协议，通过 Netty 对 HTTP 进行 url、参数解析，并结合动态代理，将 HTTP 调用转换成对应的 RPC 调用

未完！！！



## 为什么不用SpringMVC?

在互联网分布式架构下，SpringMVC 有以下缺点：

- 无法实现服务发现和负载均衡
- 缺少统一认证和鉴权
- 无法处理大量的请求，即性能不如 API 网关


## 为什么需要 API 网关？

在微服务之间进行通信，如果仍然使用 Http 进行通信，就会很慢，因此就出现了 rpc 架构（socket通信），微服务之间通过 Dubbo 进行通信。但是在 Web、小程序、H5 中使用的是 `HTTP`  协议，如果对每一个微服务都编写一套转发（将 http 调用映射到 rpc 调用上），那么项目将会变得冗余且复杂，因此需要 `API网关` 进行统一的协议转换，对外暴露出 Http 接口提供服务。

在分布式架构下，每一个 web 应用都会有一个共性的需求，`鉴权、限流、监控、熔断、切量`，会导致维护成本增加，那么如果通过 API 网关来对所有的 web 应用进行统一管理，就可以将这些共性的需求融入到 API 网关中，不需要在每一个 web 应用都做一套，提高了研发的效率



# 2、实现





## 2.1、api-core 模块

从 Http 请求到 rpc 的调用，我们使用 Netty 进行协议的转换，一次 Http 的请求就是一次会话，包括了：建立连接、协议转换、方法映射、泛化调用、返回结果等一系列操作。

因此要单独抽取出来一个 `api-gateway-core` 工程模块，`core` 模块并不和 SpringBoot 服务写在一起，而是作为一个组件抽取出来，可以独立使用。



### 解析 http 请求参数

根据 request 的类型，是 post 还是 get 进行参数解析

- 如果是 get 请求，使用 netty 中的 QueryStringDecoder 来解析 get 中的参数
- 如果是 post 请求
  - 如果 contentType 是 multipart/form-data ，则使用 netty 中的 HttpPostRequestDecoder 来解析 post 中的参数
  - 如果 contentType 是 application/json ，则直接取出 request 的 content 中的数据即可

将解析好的数据传入 rpc 调用即可



### rpc 的泛化调用

为了让 Http 请求与 rpc 调用建立连接，我们需要一个配置类，来建立 `Http -> rpc` 的映射关系 

![1695369055189](img/1695369055189.png)



在实现 rpc 泛化调用这部分，主要包含两个部分：

- 如何手动实现 rpc 泛化调用？
- 如何避免硬编码，对每个 Http 请求都编写一套 rpc 泛化调用逻辑？

对于 rpc 泛化调用，只需要知道接口的方法名、参数信息就可以调用到对应的 rpc 接口服务，可以参考官方文档：https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/advanced-features-and-usage/service/generic-reference/  （在 3.1 有具体的代码示例，可以参考。）



对于避免硬编码，为每一个 Http 请求都编写 rpc 的泛化调用逻辑不利于扩展且非常繁琐，这里使用了 `代理` 来对 rpc 的泛化调用进行统一地处理，我们选用 `cglib` 来实现代理，`cglib` 可以让一个代理类有多个接口（其实需要让代理类有两个父接口，一个是标准的描述即是固定的，用于我们进行调用，因为只有调用了，才可以通过代理进行拦截，另一个是远程调用的 rpc 的描述接口，这里我们是通过生成字节码来实现的，代码可参考3.5）。

> 在动态代理中，创建 rpc 的描述接口其实并没有用到，只是为了定义一个标准出来。

如下图，泛化调用执行流程为： `http -> 执行cglib动态代理对象方法 -> cglib拦截 -> 执行泛化调用 -> 返回结果`

![1695389934364](img/1695389934364.png)



### 模块重构

#### 封装数据源

我们将 Dubbo 的 rpc 远程调用服务抽象为数据源，将获取 Dubbo 连接抽象为数据源的连接方法，可以很方便的扩展新的连接方式如其他厂商的 rpc 框架以及 http 服务，还可以对 Dubbo 连接进行池化。

![1695397233182](img/1695397233182.png)



#### 封装执行器

将远程泛化调用的执行过程给提取出来，放入到执行器中

在执行其中就是在其中拿到 Dubbo 的数据源连接，获取 Dubbo 的数据源连接，获取远程的泛化调用接口，执行泛化调用接口这个操作



![1695398525960](img/1695398525960.png)



### 贯穿整个会话流程的配置类以及会话类

#### 配置类 Configuration

该配置类在整个会话流程中都需要使用，那么肯定是作为单例存在的，之前也已经说了，core 模块我们希望是可以做成组件一样，可以进行独立使用的，所以说不会引入 SpringBoot 依赖，那么这些配置类的单例模式我们是借助创建一个新的 SpringBoot 项目（assist 模块），在 assist 模块中，引入 core 组件，再通过 SpringBoot 的自动配置注解将 Configuration 类给注入到 Spring 的容器中，如下图（assist 模块中的自动配置类）：

```java
@Configuration
public class GatewayAutoConfig {
    @Bean
    public cn.bugstack.gateway.core.session.Configuration gatewayCoreConfiguration(GatewayServiceProperties properties) {
        cn.bugstack.gateway.core.session.Configuration configuration = new cn.bugstack.gateway.core.session.Configuration();
        String[] split = properties.getGatewayAddress().split(":");
        configuration.setHostName(split[0].trim());
        configuration.setPort(Integer.parseInt(split[1].trim()));
        return configuration;
    }
}
```



如下图为 core 模块中，配置类所需要存储的信息：

- Dubbo 连接所需信息：用于连接 Dubbo 获取 rpc 泛化调用对象
- MapperRegistry：存储 Http 请求的 uri 所对应的 cglib 代理对象，对每个 uri 都创建一个对应的代理对象

![1695450101372](img/1695450101372.png)



#### 会话类 GatewaySessionFactory

贯穿整个会话流程的是 `GatewaySessionFactory` 类，该类就是用于创建 `GatewaySession` 的，`GatewaySession` 包含了 `Http请求的uri`、`执行器`、`Configuration`

> 这里需要注意的是 GatewaySessionFactory 只有一份，而 GatewaySession 是每一个 Http 请求的 uri 都会对应一份，也就是说每当有 Http 请求进入 Netty 的处理通道时，都会调用 GatewaySessionFactory 的 `openSession()` 方法创建一个新的 GatewaySession 对象，而该对象里的 Configuration 是单例的，包含了所有的代理对象，因此在 GatewaySession  对象创建完成之后，会通过 GatewaySession  里的 Configuration 拿到代理对象，去执行代理对象中的方法，在 Netty 的 ChannelHandler 中调用会话服务的代码如下：
>
> ```java
>   // 2. 调用会话服务
>   GatewaySession gatewaySession = gatewaySessionFactory.openSession(uri); // 创建一个 GatewaySession
>   IGenericReference reference = gatewaySession.getMapper(); // 创建 GatewaySession 会传入 Configuration，这里回去 Configuration 中拿到此次请求 uri 所对应的代理对象
>   SessionResult result = reference.$invoke(args); // 执行代理对象方法，会被 cglib 拦截去进行 rpc 调用
> ```
>
> 

![1695456041511](img/1695456041511.png)

> 具体的会话处理流程为左侧红色部分，后续还可以添加对 Http 请求的鉴权，也是在 Netty 中添加 ChannelHandler 即可。

![1695455732116](img/1695455732116.png)













### shiro + jwt 鉴权

这里将鉴权操作放入 Netty 的第一个 ChannelHanlder

使用 jwt 生成 token，shiro 中进行认证，即将前端传入的 jwt token 使用 jwt 进行解码，如果解码成功，则认证成功，否则，认证失败

当 http 请求进入 netty 的第一个 handler时，即 `GatewayServerHandler`，在这里我们直接将 http 请求的一些属性给放到管道的属性中（`channel.attr`），并且释放 http 资源（`request.retain()`），在鉴权的 handler 中，取出请求里的 `token` 信息，并且使用 `shiro` 进行认证，如果通过，就放行，否则直接拦截即可

这里鉴权的时候，我们对 `get` 请求就不进行鉴权操作了（如果查询的是隐私数据，可以对 `get` 请求进行鉴权）

> shiro + jwt 认证具体代码参考3.6





### 会话流程总结

> 会话流程中所用类结构如下图所示

- `GatewaySessionFactory`：用于创建 `GatewaySession`，每一个 `Http` 请求进入 `Netty` 之后，都会根据 `uri` 创建一个 `GatewaySession` 对象 
- `GatewaySession`：包含了 `uri、executor、Configuration` 三个属性，`uri` 为他所处理的 `Http` 请求的路径，用于去 `Configuration` 中取出对应的 `HttpStatement`，`GatewaySession`  
- `MapperProxyFactory`：用于创建 cglib 代理对象，里边有一个 `Map`，存储 `uri` 到 `代理对象` 的缓存 
- `MapperProxy`：代理对象的拦截器，里边会执行调用 `GatewaySession` 的 `get()` 方法处理 `Http` 请求，在 `get()` 方法中，会调用 `执行器` 执行远程的 `rpc 接口`  
- `HttpStatement`：存储 rpc 接口的相关信息，接口信息、方法信息、参数类型、调用对应的uri、调用是否需要权限认证、Http请求类型（get、post..）

![1695707054444](img/1695707054444.png)

> Http 请求在 Netty 的 handler 中调用 rpc 接口的流程

![4、api-core模块会话完整流程 (img/4、api-core模块会话完整流程 (1).png)](D:\Edge下载\4、api-core模块会话完整流程 (1).png)







## 2.2、api-gateway-assist

`assist` 模块是一个 `springboot-starter` 

通常在编写一个 `starter` 时，会通过 `META-INF` 文件夹下的 `spring.factories` 文件，配置加载AutoConfigure类。

> SpringBoot 在启动时，会去依赖的 starter 包中寻找 `resources/META-INF/spring.factories` 文件，再根据文件中配置的 Jar 包去扫描项目依赖的 Jar 包，类似于 Java 的 SPI 机制

![1696063936596](img/1696063936596.png)

文件内容如下：

```yaml
org.springframework.boot.autoconfigure.EnableAutoConfiguration=cn.bugstack.gateway.assist.config.GatewayAutoConfig
```



启动 `assist` 模块，会做哪些事情？

- 在容器刷新完成之后，会向 center 模块发送 HTTP 请求，拉取数据库中存储的 rpc 远程接口的信息（也就是根据 gatewayid 查询该网关下边有哪些服务），将这些接口信息给注册进 `core` 模块中的 `Configuration` 类中。（创建 Dubbo 连接信息、创建 uri 到代理对象的映射、创建 uri 对应的 HttpStatement）



在 assist 组件中，将网关的注册和拉取配置操作放在 ApplicationContextAware 接口对应的 setApplicationContext 方法中

并且监听容器关闭事件 `ContextClosedEvent`，在容器关闭时，也将 netty 服务关闭掉，避免一直占用端口。





## 2.3、api-gateway-engine

engine 模块作为一个 SpringBoot 项目，引入了 assist 模块，在启动 engine 项目时，就回去扫描 assist 模块 META-INF/spring.factories 中的自动配置类，注入扫描自动配置类。

对 engine 模块打包后，放到 docker 中，在 docker 中 ip 自动分配，因此，需要修改 center 模块中启动 netty 时的代码，原本是

```java
channelFuture = b.bind(new InetSocketAddress(configuration.getHostName(), configuration.getPort())).syncUninterruptibly();
```

修改后为：

```java
channelFuture = b.bind(configuration.getPort()).syncUninterruptibly();
```

我们不需要自己再去指定 ip，这时绑定的 ip 为 `0.0.0.0`

Netty 通过 bind 绑定的 ip，是指服务端能够监听到目的地 IP 为所绑网卡地址的 IP 包。比如主机中有网卡A和网卡B，程序中 `bind(A)` ，那么操作系统会将发往网卡 A 地址的 IP 包数据，从内核态复制到用户态供用户使用。

我们使用上边修改后的写法，绑定的 IP 为 `0.0.0.0`，此时 Netty 服务端可以监听并收到外部发给主机任意一张网卡的数据。

在 Docker 容器内，感受不到宿主机的网卡 IP，只能感受到 Docker 自己的虚拟网卡（eth0），因此 ip 设置为 127.0.0.0 不能正常启动 netty 服务，

engine 中引入了 assist 模块，assist 模块引入了 core 模块，将 engine 模块打包运行。









## 2.4、api-gateway-center

该模块主要用于操作数据库，提供其他模块对 rpc 接口信息进行注册和拉取。

### 唯一索引

在注册 RPC 服务的时候，比如向 application_system 表中注册时，会传入 system_id 字段，为了防止 **重复注册** ，于是将 application_system 表中的 system_id 字段改为 `unique key` 即唯一索引，这样如果重复插入，就会抛出 `DuplicateKeyException` 异常，我们在 Controller 中捕捉到这个异常，提示`发生了重复注册` 即可。

> 唯一索引

![1691331548464](img/1691331548464.png)

> 重复插入抛出异常 DuplicatedKeyException

![1691331590785](img/1691331590785.png)



> Controller 捕捉异常

![1691331610379](img/1691331610379.png)









## 2.5、api-gateway-sdk

sdk 模块主要进行的任务为：

- sdk 模块作为一个依赖，并且定义了注解，在 `被调用模块` 中引入 sdk 模块，使用该模块提供的注解写在 rpc 接口上，之后在启动 `被调用模块` 时，rpc 接口的信息会被扫描，并且调用 center 模块中的方法，注册到数据库中去
- 在 sdk 模块中会去调用 center 模块的方法向数据库中注册 rpc 接口信息，在注册之后，需要使用 `Redis的发布和订阅` 功能保证 assist 模块可以从数据库中拉取到最新的 rpc 接口信息。







## 启动项目

对于最新的完整项目

由于我实在本地 windows 启动，因此需要先将刷新 nginx 配置的内容给注释掉，位于 center 模块的 GatewayConfigMange 的 registerGatewayServerNode 方法中，将刷新 nginx 网关配置的地方注释掉即可。



### 待办

1. 测试接口性能





### 问题

1. engine 模块一直报错  zk localhost:2181 连接不上，暂时没有找到哪里的问题
2. 如果新添加接口，不仅需要重启 test 模块，而且需要重新 engine 模块，刷新 Dubbo 缓存（Redis 订阅发布拉取的是最新的数据库接口）



# 业务逻辑

## engine 模块

- engine 模块启动时，首先会加载 assist 模块中的 GatewayApplication

> 在 GatewayApplication 的 setApplicationContext() 中，做两件事：
>
> 1. 向 center 模块注册网关算力服务，也就是将当前启动的 engine 算力模块（group_id, gateway_id, gateway_name, gateway_address） 注册到 gateway_server_detail 表中
> 2. 向 center 模块拉取网关配置（系统、接口、方法）
>    1. 将 system_id, system_registry, interface_id, interface_version 放入 configuration 中
>    2. 将 httpStatement 加入到 configuration 的 mapperRegistry 中去（即添加 uri -> 代理对象的映射）

- 在 assist 模块的 GatewayApplication 中还定义了 Redis 的订阅模块，当有新的接口方法注册到 center 模块中时，会通过 redis 发布事件，在 GatewayApplication 的 receiveMessage() 中进行接收

> 在 receiveMessage 中，topic 为 gatewayId， message 为 systemId，在该方法中调用 addMappers，将 systemId 传入，向 center 模块拉取该 systemId 下的网关配置（和上边的第二步一样）



- 解释一下 addMappers(String systemId) 这个方法：

> 该方法是向 center 模块拉取网关配置，拉取的过滤条件是 gatewayId 和 systemId，gatewayId 存储在 engine 模块的 yml 文件中，是网关的标识，systemId 在 test 模块中，也就是我们进行泛化调用的 rpc 模块的系统标识
>
> 一个网关 gateway_id 对应多个 system_id，这里拉取配置也就是根据当前 engine 模块的 gateway_id 去找到当前网关下的一个或多个 system_id，再去拉取这些 system_id 对应的接口、方法到当前的 engine 模块中







# 3、技术细节



## 3.1、如何调用远程服务？

### 服务提供者

步骤1：首先启动一个服务模块（provider 模块），该模块作为被调用方，使用 Dubbo 将接口服务暴露给消费者进行使用。通过 `@Service` 注解将服务暴露出去

```java
package cn.bugstack.gateway.interfaces;

import cn.bugstack.gateway.rpc.IActivityBooth;
import cn.bugstack.gateway.rpc.dto.XReq;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.0")
public class ActivityBooth implements IActivityBooth {

    @Override
    public String sayHi(String str) {
        System.out.println("invoke sayHi");
        return "hi " + str + " by api-gateway-test-provider";
    }

    @Override
    public String insert(XReq req) {
        return "hi " + JSON.toJSONString(req) + " by api-gateway-test-provider";
    }

    @Override
    public String test(String str, XReq req) {
        return "hi " + str + JSON.toJSONString(req) + " by api-gateway-test-provider";
    }

```



provider 模块的 yaml 配置如下：（需要启动 zookeeper）

```yaml
dubbo:
  application:
    name: api-gateway-test
    version: 1.0.0
  registry:
    #address: N/A 泛化调用不能使用此方式
    address: zookeeper://127.0.0.1:2181
  protocol:
    name: dubbo
    port: 20881
  scan:
    base-packages: cn.bugstack.gateway.rpc
```



引入依赖如下：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.58</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.8</version>
    </dependency>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo</artifactId>
        <version>2.7.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.dubbo</groupId>
        <artifactId>dubbo-spring-boot-starter</artifactId>
        <version>2.7.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.zookeeper</groupId>
        <artifactId>zookeeper</artifactId>
        <version>3.4.13</version>
    </dependency>
    <dependency>
        <groupId>org.apache.curator</groupId>
        <artifactId>curator-framework</artifactId>
        <version>4.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.curator</groupId>
        <artifactId>curator-recipes</artifactId>
        <version>4.0.1</version>
    </dependency>
    <!-- 服务注册组件 -->
    <dependency>
        <groupId>cn.bugstack.gateway</groupId>
        <artifactId>api-gateway-sdk</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```



### 服务消费者

步骤2：新创建一个模块进行 rpc 调用

新创建一个 maven 项目，引入依赖：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo</artifactId>
    <version>2.7.5</version>
</dependency>
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.13</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>4.0.1</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.0.1</version>
</dependency>
```



编写 junit 测试单元进行 rpc 调用：

```java
public class RPCTest {

    @Test
    public void test_rpc() {

        ApplicationConfig application = new ApplicationConfig();
        // 创建 ApplicationConfig
        application.setName("api-gateway-test");
        application.setQosEnable(false);
   	    // 创建注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://127.0.0.1:2181");
        registry.setRegister(false);
        // 创建服务引用配置
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface("cn.bugstack.gateway.rpc.IActivityBooth");
        reference.setVersion("1.0.0");
        reference.setGeneric("true");

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(application)
                .registry(registry)
                .reference(reference)
                .start();

        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);

        // 第一个参数为方法名，第二个参数为参数全类名（String数组），第三个参数为需要传入的参数（Object数组）
        Object result = genericService.$invoke("sayHi", new String[]{"java.lang.String"}, new Object[]{"world"});

        System.out.println(result);
    }

}
```





## 3.2、如何接受 http 请求并调用远程服务？

首先使用 Netty 接受 Http 请求，在 Netty 的 handler 中进行鉴权、解析参数、调用服务3个操作。

这里主要说一下调用服务是如何进行：

调用服务是通过 cglib 动态代理来实现的，在 cglib 动态代理的拦截方法中，进行 rpc 调用

```java
package com.example.nginxtest1;

import com.alibaba.fastjson.JSON;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.Test;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * @author 千祎来了
 * @date 2023/9/6 13:03
 */

public class CglibTest implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(CglibTest.class);

    @Test
    public void test() throws Exception {
        // 定义接口
        InterfaceMaker interfaceMaker = new InterfaceMaker();
        interfaceMaker.add(new Signature("sayHi", Type.getType(String.class), new Type[]{Type.getType(String.class)}), null);
        Class<?> interfaceClass = interfaceMaker.create();

        // 创建代理
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Object.class);

        // 将 IGenericReference 也设置为父接口，当调用 IGenericReference 的方法，cglib 会进行拦截
        enhancer.setInterfaces(new Class[]{IGenericReference.class, interfaceClass});
        enhancer.setCallback(this);

        // 创建代理对象
        IGenericReference obj = (IGenericReference) enhancer.create();
        
        HashMap<String, Object> args = new HashMap<>();
        args.put("str", "123");
        
        // 执行方法，args 是用于 rpc 调用的传参
        String res = obj.$invoke(args);
        logger.info("执行结果为:{}, {}", res);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Map<String, Object> rpcArgs = (Map<String, Object>) args[0];
        logger.info("拦截方法:{}", method.getName());
        logger.info("拦截方法参数为:{}", rpcArgs.values().toArray());

        // 这里需要指定 rpc 调用的方法名，参数类型和参数值，这里硬编码，后续应该是从 Http 请求中解析出来，并且存储到变量中，在此处取出
        String result = rpcTest("sayHi", new String[]{"java.lang.String"}, rpcArgs.values().toArray());
        logger.info("rpc远程调用结果为:{}", result);
        return result;
    }

    public String rpcTest(String methodName, String[] parameterTypes, Object[] args) {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("api-gateway-test");
        application.setQosEnable(false);

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://127.0.0.1:2181");
        registry.setRegister(false);

        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface("cn.bugstack.gateway.rpc.IActivityBooth");
        reference.setVersion("1.0.0");
        reference.setGeneric("true");

        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(application)
                .registry(registry)
                .reference(reference)
                .start();

        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        logger.info("远程调用执行方法:{}", methodName);
        Object result = genericService.$invoke(methodName, parameterTypes, args);
        return JSON.toJSONString(result);
    }
}
```



## 3.3、注解如何扫描rpc方法提供方的接口并加入数据库中？

定义注解 `@ApiProducerClazz、@ApiProducerMethod`，定义一个类实现 `BeanPostProcessor`，并且覆盖`postProcessAfterInitialization()` 方法，在这个方法中就可以扫描每个初始化的 Bean，通过解析我们自定义的注解，就可以扫描我们需要注册的 rpc 方法了。

```java
public class GatewaySDKApplication implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ApiProducerClazz apiProducerClazz = bean.getClass().getAnnotation(ApiProducerClazz.class);
        if (null == apiProducerClazz) return bean;
        // 如果不是 null，则说明是我们需要扫描的 rpc 方法，可以将 rpc 方法注册进数据库中
        // ...
        return bean;
    }
}
```



## 3.4、如何动态上线 rpc 方法？

网关算力节点如果需要调用 rpc 方法，就需要去数据库中将需要调用的 rpc 方法名称、接口名、参数类型拉取下来，知道了 rpc 方法的这些信息，才可以进行调用，那么如果新添加了一个 rpc 提供方，如何实现网管算力节点去动态拉取新添加的 rpc 方法的信息呢？

实现方式：Redis订阅/发布



//  压测、上线服务自动拉取、设计模式





http



## 3.5、生成rpc接口的字节码



```java
package cn.bugstack.gateway.core.bind;

import cn.bugstack.gateway.core.mapping.HttpStatement;
import cn.bugstack.gateway.core.session.GatewaySession;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxyFactory implements MethodInterceptor {

    public MapperProxyFactory(){}

    public static void main(String[] args) {
        MapperProxyFactory mapperProxyFactory = new MapperProxyFactory();
        mapperProxyFactory.testCglib();
    }
    public void testCglib() {
        InterfaceMaker interfaceMaker = new InterfaceMaker();
        /**
         * 第一个参数为接口的方法名
         * 第二个参数为参数类型
         * 第三个参数为返回值类型
         */
        interfaceMaker.add(new Signature("test", Type.getType(String.class), new Type[]{Type.getType(String.class)}), null);
        Class<?> interfaceClass = interfaceMaker.create();
        // 代理对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Object.class);
        // IGenericReference 统一泛化调用接口
        // interfaceClass    根据泛化调用注册信息创建的接口，建立 http -> rpc 关联
        enhancer.setInterfaces(new Class[]{IGenericReference.class, interfaceClass});
        // setCallback 设置拦截方法
        enhancer.setCallback(this);
        IGenericReference o = (IGenericReference) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("拦截");
        return null;
    }
}
```





## 3.6、shiro+jwt 认证

### 1、先引入依赖

```xml
<!-- https://mvnrepository.com/artifact/org.apache.shiro/shiro-core -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.3.2</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```



### 2、整合 shiro

目录结构如下：

![1695474790839](img/1695474790839.png)



重写 shiro 的 Token 以及 Realm：

> GatewayAuthorizingRealm.java

```java
package com.example.nginxtest1.authorization;

import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class GatewayAuthorizingRealm extends AuthorizingRealm {

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return GatewayAuthorizingToken.class;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 暂时不需要做授权处理
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        try {
            // 验证解析是否报错
            Claims claims = JwtUtil.decode(((GatewayAuthorizingToken) token).getJwt());
            // 验证签发人是否匹配
            if (!token.getPrincipal().equals(claims.getSubject())) throw new AuthenticationException("无效令牌");
        } catch (Exception e) {
            throw new AuthenticationException("无效令牌");
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), this.getName());
    }

}
```



> GatewayAuthorizingToken

```java
package com.example.nginxtest1.authorization;

import org.apache.shiro.authc.AuthenticationToken;

public class GatewayAuthorizingToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    // 通信管道ID
    private String uId;

    // JSON WEB TOKEN
    private String jwt;

    public GatewayAuthorizingToken() {
    }

    public GatewayAuthorizingToken(String uId, String jwt) {
        this.uId = uId;
        this.jwt = jwt;
    }

    @Override
    public Object getPrincipal() {
        return uId;
    }

    @Override
    public Object getCredentials() {
        return this.jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

}
```



### 3、编写 JWT 工具类

JWT 工具类进行编码和解码

> JwtUtil

```java
package com.example.nginxtest1.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String signingKey = "B*B^5Fe";

    /**
     * 生成 JWT Token 字符串
     *
     * @param issuer    签发人
     * @param ttlMillis 有效期
     * @param claims    额外信息
     * @return Token
     */
    public static String encode(String issuer, long ttlMillis, Map<String, Object> claims) {
        if (null == claims) {
            claims = new HashMap<>();
        }

        // 签发时间（iat）：荷载部分的标准字段之一
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 签发操作
        JwtBuilder builder = Jwts.builder()
                // 荷载部分
                .setClaims(claims)
                // 签发时间
                .setIssuedAt(now)
                // 签发人；类似 userId、userName
                .setSubject(issuer)
                // 设置生成签名的算法和秘钥
                .signWith(SignatureAlgorithm.HS256, signingKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            // 过期时间（exp）：荷载部分的标准字段之一，代表这个 JWT 的有效期。
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Claims decode(String token) {
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(signingKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(token)
                .getBody();
    }

}
```



### 4、shiro 认证服务实现类

在这里进行真正的认证服务

> IAuth

```java
public interface IAuth {

    boolean validate(String id, String token);

}
```



> AuthService

在 `validate()` 中进行 `subject.login` 之后，就会进入到我们自定义的 Realm 的认证逻辑中，即 `GatewayAuthorizingRealm # doGetAuthenticationInfo` ，在这里会解析请求附带的

```java
public class AuthService implements IAuth {

    private Subject subject;

    public AuthService() {
        // 1. 获取 SecurityManager 工厂，此处使用 shiro.ini 配置文件初始化 SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        // 2. 得到 SecurityManager 实例 并绑定给 SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        // 3. 得到 Subject 及 Token
        this.subject = SecurityUtils.getSubject();
    }

    @Override
    public boolean validate(String id, String token) {
        try {
            // 身份验证
            subject.login(new GatewayAuthorizingToken(id, token));
            // 返回结果
            return subject.isAuthenticated();
        } finally {
            // 退出
            subject.logout();
        }
    }
}
```



### 5、测试

下边测试类引入了 `junit` 

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```



```java
public class ShiroTest {

    private final static Logger logger = LoggerFactory.getLogger(ShiroTest.class);

    @Test
    public void test_auth_service() {
        IAuth auth = new AuthService();
        boolean validate = auth.validate("xiaofuge", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4aWFvZnVnZSIsImV4cCI6MTY2NjQwNDAxMiwiaWF0IjoxNjY1Nzk5MjEyLCJrZXkiOiJ4aWFvZnVnZSJ9.Vs-ObO5OF2pYr7jkt0N4goq0hErOZNdyqfacHzbkfHM");
        System.out.println(validate ? "验证成功" : "验证失败");
    }

    // 在这里生成新的 token，再使用上边的方法测试 token
    @Test
    public void test_jwt() {
        String issuer = "xiaofuge";
        long ttlMillis = 7 * 24 * 60 * 60 * 1000L;
        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "xiaofuge");

        // 编码
        String token = JwtUtil.encode(issuer, ttlMillis, claims);
        System.out.println(token);

        // 解码
        Claims parser = JwtUtil.decode(token);
        System.out.println(parser.getSubject());
    }

}
```



### 6、shiro + jwt认证常见问题

> 1. 捕获 jwt 解析异常，因为前端传入的 jwt token 可能是上次登陆时存储的，解析时可能报错，如果我们不自己捕获到，则前端会爆500错误

这里只贴出部分关键代码，主要是捕获token超时和不合法的异常。

```java
 if (!StringUtils.isBlank(token)) {
      try {
          JwtParser parser = Jwts.parser();
          // 解析token的SigningKey必须和生成token时设置密码一致
          parser.setSigningKey("JavaGPT");
          /**
           * 如果token检验通过（密码正确，有效期内）则正常执行，否则抛出异常
           * 前端浏览器携带的 token 可能是上次登陆时存储下来的，因此需要捕获到异常，并抛出
           */
          Jws<Claims> claimsJws = parser.parseClaimsJws(token);
          Integer userId = (Integer) claimsJws.getBody().get("userId");
          if (userId == null) {
              filterChain.doFilter(request, response);
          }
          UsernamePasswordAuthenticationToken authenticationToken =
                  new UsernamePasswordAuthenticationToken(userId,null,null);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      } catch (ExpiredJwtException e) {
          ResultBody resultVO = new ResultBody(402, "登录过期，请重新登录！", null);
          doResponse(response, resultVO);
      } catch (UnsupportedJwtException e) {
          ResultBody resultVO = new ResultBody(401, "Token不合法，请自重！", null);
          doResponse(response, resultVO);
      } catch (Exception e) {
          ResultBody resultVO = new ResultBody(401, "请先登录！", null);
          doResponse(response, resultVO);
      }
}

    /**
     * 没带token或者检验失败响应给前端
     *
     * @param response
     * @param resultVO
     * @throws IOException
     */
    private void doResponse(HttpServletResponse response, ResultBody resultVO) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(resultVO);
        out.print(s);
        out.flush();
        out.close();
    }

```





## 3.7 nginx 负载均衡完整配置文件

```bash

user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;

    # 设定负载均衡的服务器列表 命令：docker exec Nginx nginx -s reload
	upstream 10001 {
		least_conn;

		server 192.168.1.102:7398;
		server 192.168.1.102:7397;
		server 192.168.1.102:7399;
	}


    # HTTP服务器
    server {
        # 监听80端口，用于HTTP协议
        listen  80;

        # 定义使用IP/域名访问
        server_name 192.168.1.102;

        # 首页
        index index.html;

        # 反向代理的路径（upstream绑定），location 后面设置映射的路径
        # location / {
        #    proxy_pass http://192.168.1.102:9001;
        # }

		location /10001/ {
			rewrite ^/10001/(.*)$ /$1 break;
			proxy_pass http://10001;
		}

    }
}
```

