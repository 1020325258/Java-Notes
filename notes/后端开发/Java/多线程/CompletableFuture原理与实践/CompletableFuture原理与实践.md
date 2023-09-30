---
typora-copy-images-to: imgs
---

# 1、CompletableFuture 使用



**为什么要使用 CompletableFuture?**

一个接口可能需要调用 N 个其他服务的接口，这在项目开发中还是挺常见的。举个例子：用户请求获取订单信息，可能需要调用用户信息、商品详情、物流信息、商品推荐等接口，最后再汇总数据统一返回。

如果是串行（按顺序依次执行每个任务）执行的话，接口的响应速度会非常慢。考虑到这些接口之间有大部分都是 **无前后顺序关联** 的，可以 **并行执行** ，就比如说调用获取商品详情的时候，可以同时调用获取物流信息。通过并行执行多个任务的方式，接口的响应速度会得到大幅优化.





**为什么不适用Future?**

`Future` 对结果的获取不是很友好，只能通过阻塞或轮询的方式得到任务的结果

因此，Java 8 才被引入的 `CompletableFuture` 可以帮助我们来做多个任务的编排，功能非常强大。



以下简称 CompletableFuture 为 CF

## CF 的创建

1. 通过 new 关键字
2. 基于静态工厂方法：：`runAsync()`、`supplyAsync()` 。


> 使用 new 关键字创建

`CompletableFuture<String> cf = new CompletableFuture<>();`

如果已经知道结果，可以直接将结果复制给 CF

`CompletableFuture<String> future = CompletableFuture.completedFuture("result value");`



> 使用静态工厂方法创建

这里在创建任务的时候，推荐使用自定义的线程池，可以让我们清楚的知道任务运行在哪个线程之上，并且可以根据实际情况做线程池的隔离。

静态工厂方法有两个：`runAsync()：无返回值`、`supplyAsync()：有返回值`

```java
public void staticFactoryMethod() throws ExecutionException, InterruptedException {
    CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
        System.out.println("runAsync创建，无返回值");
    });
    CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
        System.out.println("supplyAsync创建，有返回值");
        return "result";
    });
    System.out.println(cf1.get());
    System.out.println(cf2.get());
    /**
     * 输出结果：
     * runAsync创建，无返回值
     * supplyAsync创建，有返回值
     * null
     * result
     */
}
```



## CF处理计算结果

当我们获取到异步计算的结果之后，还可以对其进行进一步的处理，比较常用的方法有下面几个：

- `thenApply()`：
- `thenAccept()`
- `thenRun()`
- `whenComplete()`



```java
// 沿用上一个任务的线程池
public <U> CompletableFuture<U> thenApply(
    Function<? super T,? extends U> fn) {
    return uniApplyStage(null, fn);
}

//使用默认的 ForkJoinPool 线程池（不推荐）
public <U> CompletableFuture<U> thenApplyAsync(
    Function<? super T,? extends U> fn) {
    return uniApplyStage(defaultExecutor(), fn);
}
// 使用自定义线程池(推荐)
public <U> CompletableFuture<U> thenApplyAsync(
    Function<? super T,? extends U> fn, Executor executor) {
    return uniApplyStage(screenExecutor(executor), fn);
}
```



- `thenApply()`

```java
public void thenApply() throws ExecutionException, InterruptedException {
    CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
        System.out.println("supplyAsync创建，有返回值");
        return "result";
    });
    CompletableFuture<String> cf2 = cf1.thenApply(res -> res + "调用thenApply");
    System.out.println(cf2.get());
    /**
     * 调用结果：
     * result调用thenApply
     */
}
```



- `thenAccept()`和`thenRun()`：`thenAccept` 接收 Consumer 接口，`thenRun` 接收线程

```java
CompletableFuture.completedFuture("hello!")
        .thenApply(s -> s + "world!").thenApply(s -> s + "nice!").thenAccept(System.out::println);//hello!world!nice!

CompletableFuture.completedFuture("hello!")
        .thenApply(s -> s + "world!").thenApply(s -> s + "nice!").thenRun(() -> System.out.println("hello!"));//hello!
```



- `whenComplete()`：接收2个输入对象进行消费

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello!")
        .whenComplete((res, ex) -> {
            // res 代表返回的结果
            // ex 的类型为 Throwable ，代表抛出的异常
            System.out.println(res);
            // 这里没有抛出异常所有为 null
            assertNull(ex);
        });
assertEquals("hello!", future.get());

```



## 异常处理

- 使用 `handle` 处理异常

```java
public void handleExe() throws Exception {
    CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
        if (true) {
            throw new RuntimeException("抛出异常！");
        }
        return "result";
    }).handle((res, ex) -> {
        System.out.println("res:" + res);
        System.out.println("ex:" + ex);
        return res;
    });
    System.out.println(cf1.get());
    /**
     * 输出结果：
     * res:null
     * ex:java.util.concurrent.CompletionException: java.lang.RuntimeException: 抛出异常！
     * null
     */
}
```



- 使用 `exceptionally` 处理异常

由于异步执行的任务在其他线程上执行，而异常信息存储在线程栈中，因此当前线程除非阻塞等待返回结果，否则无法通过try\catch捕获异常。CompletableFuture提供了异常捕获回调exceptionally，相当于同步调用中的try\catch。使用方法如下所示：

```java
@Autowired
private WmOrderAdditionInfoThriftService wmOrderAdditionInfoThriftService;//内部接口
public CompletableFuture<Integer> getCancelTypeAsync(long orderId) {
    CompletableFuture<WmOrderOpRemarkResult> remarkResultFuture = wmOrderAdditionInfoThriftService.findOrderCancelledRemarkByOrderIdAsync(orderId);//业务方法，内部会发起异步rpc调用
    return remarkResultFuture
      .exceptionally(err -> {//通过exceptionally 捕获异常，打印日志并返回默认值
         log.error("WmOrderRemarkService.getCancelTypeAsync Exception orderId={}", orderId, err);
         return 0;
      });
}

```

有一点需要注意，CompletableFuture在回调方法中对异常进行了包装。大部分异常会封装成CompletionException后抛出，真正的异常存储在cause属性中，因此如果调用链中经过了回调方法处理那么就需要用Throwable.getCause()方法提取真正的异常。但是，有些情况下会直接返回真正的异常（[Stack Overflow的讨论**](https://stackoverflow.com/questions/49230980/does-completionstage-always-wrap-exceptions-in-completionexception)），最好使用工具类提取异常，如下代码所示：

```java
@Autowired
private WmOrderAdditionInfoThriftService wmOrderAdditionInfoThriftService;//内部接口
public CompletableFuture<Integer> getCancelTypeAsync(long orderId) {
    CompletableFuture<WmOrderOpRemarkResult> remarkResultFuture = wmOrderAdditionInfoThriftService.findOrderCancelledRemarkByOrderIdAsync(orderId);//业务方法，内部会发起异步rpc调用
    return remarkResultFuture
          .thenApply(result -> {//这里增加了一个回调方法thenApply，如果发生异常thenApply内部会通过new CompletionException(throwable) 对异常进行包装
      //这里是一些业务操作
        })
      .exceptionally(err -> {//通过exceptionally 捕获异常，这里的err已经被thenApply包装过，因此需要通过Throwable.getCause()提取异常
         log.error("WmOrderRemarkService.getCancelTypeAsync Exception orderId={}", orderId, ExceptionUtils.extractRealException(err));
         return 0;
      });
}

```

上面代码中用到了一个自定义的工具类ExceptionUtils，用于CompletableFuture的异常提取，在使用CompletableFuture做异步编程时，可以直接使用该工具类处理异常。实现代码如下：

```java
public class ExceptionUtils {
    public static Throwable extractRealException(Throwable throwable) {
          //这里判断异常类型是否为CompletionException、ExecutionException，如果是则进行提取，否则直接返回。
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }
}
```



## 编排任务

- `thenCompose()` 按顺序链接两个 `CompletableFuture` 对象，实现异步的任务链。它的作用是将前一个任务的返回结果作为下一个任务的输入参数，从而形成一个依赖关系。
- `thenCombine()` 会在两个任务都执行完成后，把两个任务的结果合并。两个任务是并行执行的，它们之间并没有先后依赖顺序。
- 如果我们想要实现 task1 和 task2 中的任意一个任务执行完后就执行 task3 的话，可以使用 `acceptEither()`。

```java
public void thenCompose() throws ExecutionException, InterruptedException {
    CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "hello!")
        .thenCompose(res -> CompletableFuture.supplyAsync(() -> res + "world!"));
    System.out.println(cf1.get());
    /**
     * 输出结果：
     * hello!world!
     */
}

public void thenCombine() throws ExecutionException, InterruptedException {
    CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "hello!")
                    .thenCombine(CompletableFuture.supplyAsync(()-> "world"), (res1, res2) -> res1 + res2);
    System.out.println(cf1.get());
    /**
     * 输出结果：
     * hello!world
     */
}
```



## 并行运行多个任务

- `allOf` 可以并行运行多个任务，等到所有任务运行完再返回
- `anyOf` 任意一个任务执行完之后，即可返回

```java
public void paraTask() throws ExecutionException, InterruptedException {
    CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "task1");
    CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "task2");
    CompletableFuture<String> cf3 = CompletableFuture.supplyAsync(() -> "task3");
    CompletableFuture<String> cf4 = CompletableFuture.supplyAsync(() -> "task4");
    CompletableFuture<String> cf5 = CompletableFuture.supplyAsync(() -> "task5");
    CompletableFuture<String> cf6 = CompletableFuture.supplyAsync(() -> "task6");
    CompletableFuture<Void> headerFuture = CompletableFuture.allOf(cf1, cf2, cf3, cf4, cf5,cf6);
    headerFuture.join();

    System.out.println(headerFuture.get());
    System.out.println("完成");
}
```



# 2、CompletableFuture使用中常见问题



## 2.1 使用自定义线程池

如果在使用中，没有传入自定义线程池，将使用默认线程池 ForkJoinPool 中的共用线程池 CommonPool（CommonPool的大小是CPU核数-1，如果是IO密集的应用，线程数可能成为瓶颈）

如果执行两个任务时，传入了自定义的线程池，那么使用 `thenRun` 执行第二个任务时，使用和第一个任务相同的线程池；使用 `thenRunAsync` 执行第二个任务时，则第一个任务使用自己传入的线程池，第二个任务使用 `ForkJoin` 线程池。（`thenAccept、thenApply`同理）

在实际使用时，建议使用自定义的线程池，并且根据实际情况进行线程池隔离。避免核心业务与非核心业务竞争同一个池中的线程，减少不同业务之间相互干扰



## 2.2 线程池循环引用导致死锁

```java
public Object doGet() {
  ExecutorService threadPool1 = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
  CompletableFuture cf1 = CompletableFuture.supplyAsync(() -> {
  //do sth
    return CompletableFuture.supplyAsync(() -> {
        System.out.println("child");
        return "child";
      }, threadPool1).join();//子任务
    }, threadPool1);
  return cf1.join();
}
```

对于上边代码，如果同一时刻有 10 个请求到达，`threadPool1` 被打满，而 `cf1` 的 子任务也需要使用到 `threadPool1` 的线程，从而导致子任务无法执行，而且父任务依赖于子任务，也无法结束，导致死锁。



参考文章：

Java Guide：https://javaguide.cn/java/concurrent/completablefuture-intro.html#%E4%BD%BF%E7%94%A8%E8%87%AA%E5%AE%9A%E4%B9%89%E7%BA%BF%E7%A8%8B%E6%B1%A0

美团技术团队：https://tech.meituan.com/2022/05/12/principles-and-practices-of-completablefuture.html

