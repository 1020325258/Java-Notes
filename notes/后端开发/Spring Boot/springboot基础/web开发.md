# web开发

#### 1. 在springboot中，我们可以使用一下方式处理静态资源

- webjars   localhost:8080/webjars/
- public , static , /** , resources  /localhost:8080
  - public和static 都放在resources目录下
- 优先级： resource  >  static (默认) > public

#### 2. 所有的使用，静态资源的目录，都在WebMvcConfiguration.java中寻找

#### 3. 使用controller访问首页

**ThymleafProperties.java文件中，就定义了前缀后缀**

**所以页面放在templates目录下**

```xml
public static final String DEFAULT_PREFIX = "classpath:/templates/";
public static final String DEFAULT_SUFFIX = ".html";
```



- 首先需要先创建首页的html，将html文件放在templates文件下，该目录下的文件专门用于controller进行做跳转
- 使用controller进行跳转页面，必须引入模板引擎thymleaf，在pom文件中引入依赖

thymleaf需要使用3.x版本的

```java
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <thymeleaf.version>3.0.11.RELEASE</thymeleaf.version>
        <thymeleaf-layout-dialect.version>2.1.1</thymeleaf-layout-dialect.version>

```

- 创建controller继续跳转

```java
@RequestMapping("/in")
    public String index(){
        return "index";
    }
```

#### 



