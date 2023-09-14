---
typora-copy-images-to: image
---



# Message

## Message Channel

![1660119628157](image/1660119628157.png)

Message Channel特性：

1. 可以很方便的拦截和监听Message

2. Message Channel是否允许在队列中缓冲数据？

   轮询的Message Channel允许。

   缓冲数据的好处是可以对入站的message进行截流，从而防止Consumer超载

   但缓冲数据同时增加了复杂性，当使用轮询模式的话，消费者只能从这个Message Channel中获取信息，换句话说就是消费者是message驱动型的。

   ​



## Message Endpoint





Service Activator

https://docs.spring.io/spring-integration/docs/current/reference/html/service-activator.html#service-activator



`MessagingGateway` 

https://docs.spring.io/spring-integration/docs/current/reference/html/gateway.html#gateway