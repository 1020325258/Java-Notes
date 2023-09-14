
# 1、Spring框架概述



1、Spring是==轻量级==  的==开源== 的JavaEE==框架== 

2、Spring可以解决企业应用开发的复杂性

3、Spring有两个核心部分：`IOC`和 `Aop`

（1）IOC：控制反转，把创建对象过程交给Spring管理

（2）Aop：面向切面，不修改源代码进行功能增强

4、Spring特点

（1）方便解耦，简化开发

（2）Aop编程支持

（3）方便程序测试 

（4）方便和其他框架进行整合

（5）方便进行事务操作

（6）降低API开发难度

5、选取Spring版本5.x学习

# 2、Spring框架下载

Spring官网：https://spring.io/projects/spring-framework#learn

下载网址：https://repo.spring.io/release/org/springframework/spring/

5.2.6RELEASE版本

spring-5.2.6.RELEASE-dist.zip



# 3、案例

### 3.1 创建一个普通的java项目

导入4个jar包

spring-beans-5.2.6.RELEASE

spring-context-5.2.6.RELEASE

spring-core-5.2.6.RELEASE

spring-expression-5.2.6.RELEASE

commons-logging-1.1.1 //还要导入这个日志包，否则会报错

### 3.2 在项目中添加依赖

### 3.3 在项目中创建一个普通的类

```java
public class User {
    public void add(){
        System.out.println("add.....");
    }
}
```

### 3.4 创建 Spring 配置文件，在配置文件中配置创建的对象

（1）Spring配置文件使用xml

bean有两个属性，id是自己起的名字，class是类所在路径

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="user" class="com.qy.spring5.User"></bean>
    
</beans>
```

### 3.5 进行测试代码编写



```java
@Test
public void testAdd(){

    // 1 加载 Spring 配置文件
    ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");

    // 2 获取配置创建的对象
    User user = context.getBean("user", User.class);

    // 3 调用获取配置创建的对象的方法
    user.add();
    // 4 输出创建的对象
    System.out.println(user);
}
```



# 4、IOC容器

（1）IOC底层原理

（2）IOC接口（BeanFactory）

（3）IOC操作Bean管理（基于xml）

（4）IOC操作Bean管理（基于注解）



1、什么是IOC

（1）控制反转，把对象创建和对象之间的调用过程，交给Spring管理

（2）使用IOC目的：为了耦合度降低

（3）做入门案例就是IOC实现

2、IOC底层原理

（1）xml解析、工厂模式、反射

3、讲解一下IOC底层原理

`假设现在有两个类`==UserDao==和==UserService==

（方便起见，我们写在一个框里）

### 4.1 IOC底层原理

==原始情况==：UserService调用UserDao是如何调用的呢？

我们要在UserService中创建一个UserDao，然后再进行调用，但是这样的话导致了UserService和UserDao的耦合度很高，如果UserDao的方法或者路径变化，那么UserService也会变化。

```java
//UserService类
public class UserService{
	execute(){
    	UserDao dao = new UserDao(); 
    	dao.add();
	}
}

------

//UserDao类
public class UserDao{
	add(){
      ......
	}
}


```

***

==工厂模式==

我们有一个工厂类，来统一的创建对象，使用的话只需要调用工厂类来获得这个对象。

Factory.class   UserService.class UserDao.class

UserService使用UserDao的时候，通过工厂类来获得UserDao，降低了UserService和UserDao的耦合度，

但是Factory和UserDao还是有耦合度的（耦合度并不会消失，只能尽可能的小）

这样UserDao改变了的话，UserService并不会改变。

```java
class UserService{
    execute(){
		UserDao dao = Factory.getDao();
        dao.add();
    }
}

---
class UserDao{
  add(){
    ......
  }
}
---
//工厂类
class Factory{
  public static UserDao getDao(){
    return new UserDao();
  } 
}
```



####  4.1.1 IOC 解耦过程

IOC没有使耦合度消失，只是降低了耦合度，比如说，如果UserDao的路径改变了，那么只需要在xml配置文件中修改class属性即可，不需要修改工厂类。

- 第一步  xml配置文件 ， 配置创建的对象

  ```xml
  <bean id="dao" class="com.at.qy.UserDao"></bean>
  ```

  ​

- 第二部  有Service类和Dao类， 创建工厂类

  ```java
  class UserFactory{
    public static UserDao getDao(){
      String classValue = class属性值; //1  xml解析
      Class clazz = Class.forName(classValue); //2  通过反射创建对象
      return (UserDao)clazz.newInstance();
    }
  }
  ```

  ​

### 4.2 IOC接口

1、IOC思想基于IOC容器完成，IOC容器底层就是对象工厂



2、Spring提供IOC容器两种实现方式：（两个接口）

（1）BeanFactory：IOC容器的基本实现，是Spring内部的使用接口，不提供开发人员进行使用。

*加载配置文件时，不会创建对象，只有在获取（使用）对象才去创建对象

（2）ApplicationContext：BeanFactory接口的子接口，提供更多强大的功能，一般由开发人员使用

*加载配置文件时候就会把配置文件中所有对象进行创建（下列代码第一步，就是加载配置文件）

```java
    @Test
    public void testAdd(){
        // 1 加载 Spring 配置文件  
        // ********ApplicationContext可以替换为BeanFactory********
        ApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");
        // 2 获取配置创建的对象
        User user = context.getBean("user", User.class);
        // 3 调用获取配置创建的对象的方法
        user.add();
        // 4 输出创建的对象
        System.out.println(user);
    }
```



### 4.3 IOC操作Bean管理

#### 4.3.1 什么是Bean管理？

Bean管理指的是两个操作：

（1）Spring创建对象

（2）Spring注入属性

#### 4.3.2 Bean管理操作有两种方式

（1）基于xml配置文件方式实现

（2）基于注解方式实现



# 5、IOC操作Bean管理（基于xml方式）

### 基于xml方式创建对象

（1）在spring配置文件中，使用bean标签，标签里面添加对应属性，就可以实现对象创建

```xml
<bean id="user" class="com.qy.spring5.User"></bean>
```

（2）在bean标签有很多==属性==，介绍常用的属性：

* id属性：唯一标识
* class属性：类全路径（包类路径）

（3）创建对象的时候，默认也是执行==无参数的构造方法==完成对象创建

### 基于xml方式注入属性

（1）DI：依赖注入，就是注入属性

#### 第一种注入方式：使用set方法注入

==Book类==：

```java
public class Book {

    private String bname;

    private String bauthor;

    public void setBname(String bname) {
        this.bname = bname;
    }
    public void setBauthor(String bauthor) {
        this.bauthor = bauthor;
    }
    public String getBname() {
        return bname;
    }
    public String getBauthor() {
        return bauthor;
    }
}

```

==xml配置文件==：

```xml
 <bean id="book" class="com.qy.spring5.Book">
      <property name="bname" value="易经"></property>
      <property name="bauthor" value="达摩老祖"></property>
</bean>
```

==测试类==：

```java
@Test
public void testBook(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");
    // 2 获取配置创建的对象
    Book book = context.getBean("book",Book.class);
    // 3 输出配置创建的对象的属性
    System.out.println("书名："+book.getBname()+"作者："+book.getBauthor());
}

/***************/
输出：书名：易经作者：达摩老祖
```



#### 第二种注入方式：使用有参构造注入

==Order类==：

```java
public class Order {
    private String oname;

    private String address;

    public Order(String oname,String address){
        this.oname = oname;
        this.address = address;
    }
    public void orderTest(){
        System.out.println("订单名称："+oname+" 订单地址："+address);
    }
}
```

==xml配置文件==：

使用有参构造注入要在xml配置文件中加入`<constructor-arg></constructor-arg>`标签

```xml
<bean id="order" class="com.qy.spring5.Order">
    <constructor-arg name="oname" value="电脑"></constructor-arg>
    <constructor-arg name="address" value="China"></constructor-arg>
</bean>
```

==测试类==：

```java
@Test
public void testOrder(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");
    // 2 获取配置创建的对象
    Order order = context.getBean("order",Order.class);
    // 3 输出配置创建的对象
    System.out.println(order);
    order.orderTest();
}
/************************/
输出为：

com.qy.spring5.Order@569cfc36

订单名称：电脑 订单地址：China
```



### 基于xml方式注入其他类型属性

1、字面量

（1）null值

给name属性设置为null

```xml
<property name="bname">
    <null/>
</property>
```

（2）属性值包含特殊符号

例如，我们想要注入属性值为 <<南京>> 

```xml
<property name="bname" value="<<南京>>"></property> 
```

像这样写，因为带有特殊符号，会报错

```xml
<property name="bname">
    <value><![CDATA[<<南京>>]]></value>
</property>
```

应该这样写就可以了。<![CDATA[需要注入的属性值]]>



### 注入属性-外部bean

两个类UserService和UserDao，UserService调用UserDao

==UserService类==：

```java
public class UserService {
    private UserDao userDao;
    private void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }
    public  void add(){
        System.out.println("service......");
    }
}
```

==UserDao类==：

```java
public class UserDao {
    public void update(){
        System.out.println("dao update......");
    }
}
```

==xml配置文件==：

```xml
<bean id="userDao" class="com.qy.spring5.dao.UserDao"></bean>
<bean id="userService" class="com.qy.spring5.service.UserService">
    <!--
		注入UserDao对象
		name:类里面属性名称
		ref:创建UserDao对象bean标签的id值
    -->
    <property name="userDao" ref="userDao"></property>
</bean>
```



### 注入属性-内部bean和级联赋值

（1）一对多关系：部门和员工

一个部门有多个员工，一个员工属于一个部门

部门是一，员工是多

（2）在实体类之间表示一对多关系

==部门类、员工类、xml配置文件==

```java
//部门类
public class Dept {
    private String dname;

    public void setDname(String dname) {
        this.dname = dname;
    }
    public String getDeptName(){
        return this.dname;
    }
}

//员工类
public class Emp {
    private String ename;
    private String gender;
    private Dept dept;

    public void setEname(String ename) {
        this.ename = ename;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setDept(Dept dept) {
        this.dept = dept;
    }
    public void test(){
        System.out.println("员工名称："+ename+" 性别："+gender+" 部门名称："+dept.getDeptName());
    }
}

//xml配置文件
<bean id="emp" class="com.qy.spring5.DeptTest.Emp">
    <property name="ename" value="lucy"></property>
    <property name="gender" value="女"></property>
    <property name="dept">
        <bean id="dep" class="com.qy.spring5.DeptTest.Dept">
            <property name="dname" value="安保部"></property>
        </bean>
    </property>
</bean>

//测试类
@Test
public void testOrder(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean2.xml");
    // 2 获取配置创建的对象
    Emp emp = context.getBean("emp", Emp.class);
    // 3 输出配置创建的对象
    System.out.println(emp);
    emp.test();
}

//输出结果
com.qy.spring5.DeptTest.Emp@66d1af89
员工名称：lucy 性别：女 部门名称：安保部
```

### 注入属性-级联赋值 

```xml
<!--级联赋值-->
<bean id="emp" class="com.qy.spring5.DeptTest.Emp">
    <!--设置两个普通属性-->
    <property name="ename" value="lucy"></property>
    <property name="gender" value="女"></property>
    <!--级联赋值-->
    <property name="dept" ref="dept"></property>
    <!--dept.name用于设置dept的name，需要给Dept类上添加Dept的get方法-->
    <property name="dept.name" value="技术部"></property>
</bean>
<bean id="dept" class="com.qy.spring5.DeptTest.Dept">
    <property name="dname" value="财务部"></property>
</bean>
```



### 注入集合类型属性



(1)注入数组类型属性

(2)注入 List 集合类型属性

(3)注入 Map 集合



#### 综合案例

==Stu类和xml配置文件==

```java
//Stu类
public class Stu {

    //1 数组类型属性
    private String[] courses;
    //2 集合类型属性
    private List<String> list;
    //3 map类型属性
    private Map<String,String> maps;
    //4 set类型属性
    private Set<String> sets;

    public void setSets(Set<String> sets) {
        this.sets = sets;
    }
    public void setCourses(String[] courses) {
        this.courses = courses;
    }
    public void setList(List<String> list) {
        this.list = list;
    }
    public void setMaps(Map<String, String> maps) {
        this.maps = maps;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "courses=" + Arrays.toString(courses) +
                ", list=" + list +
                ", maps=" + maps +
                ", sets=" + sets +
                '}';
    }
}

//xml配置文件
<bean id="stu" class="com.qy.spring5.ListTest.Stu">
    <!--数组属性注入-->
    <property name="courses">
        <array>
            <value>java课程</value>
            <value>数据库课程</value>
        </array>
    </property>
    <!--list属性注入-->
    <property name="list">
        <list>
            <value>大王</value>
            <value>小王</value>
        </list>
    </property>
    <!--map属性注入-->
    <property name="maps">
        <map>
            <entry key="JAVA" value="java"></entry>
            <entry key="MYSQL" value="mysql"></entry>
        </map>
    </property>
    <!--set属性注入-->
    <property name="sets">
        <set>
            <value>set1</value>
            <value>set2</value>
        </set>
    </property>
</bean>

//测试类
@Test
public void testOrder(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/ListTest/bean.xml");
    // 2 获取配置创建的对象
    Stu stu = context.getBean("stu", Stu.class);
    // 3 输出配置创建的对象
    System.out.println(stu);
}
```

### 注入集合类型属性2

#### 在集合里面设置对象类型值

==Stu类、Course类、xml配置文件==

```java
//Stu类
public class Stu {
    private List<Course> course;
    public void setCourse(List<Course> course) {
        this.course = course;
    }
    public void test(){
        System.out.println(course);
    }
}

//Course类
public class Course {

    private String cname;
    public void setCname(String cname) {
        this.cname = cname;
    }
    @Override
    public String toString() {
        return "Course{" +
                "cname='" + cname + '\'' +
                '}';
    }
}

//xml配置文件
<bean id="stu" class="com.qy.spring5.ListTest.Stu">
    <property name="course">
        <list>
            <ref bean="course1"></ref>
            <ref bean="course2"></ref>
        </list>
    </property>
</bean>
<bean id="course1" class="com.qy.spring5.ListTest.Course">
    <property name="cname" value="Sprng5框架"></property>
</bean>
<bean id="course2" class="com.qy.spring5.ListTest.Course">
    <property name="cname" value="MyBatis框架"></property>
</bean>

//测试类
@Test
public void testOrder(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/ListTest/bean.xml");
    // 2 获取配置创建的对象
    Stu stu = context.getBean("stu", Stu.class);
    // 3 输出配置创建的对象
    stu.test();
}

//输出
[Course{cname='Sprng5框架'}, Course{cname='MyBatis框架'}]
```





# 6、IOC操作Bean管理（FactoryBean）



1、Spring有两种类型bean，一种普通bean，另一种工厂bean（FactoryBean）

2、普通bean：在配置文件中定义bean类型就是返回类型

3、工厂bean：在配置文件定义bean类型可以和返回类型不一样

第一步 创建类，让这个类作为工厂bean，实现接口 FactoryBean

第二部 实现接口里卖弄的方法，在实现的方法中定义返回的bean类型

**工厂Bean实现**

==MyBean类、Course类、xml配置文件、测试类==

```java
//MyBean类
public class MyBean implements FactoryBean {
    private String cname;

    @Override
    // 定义返回的类型为Course
    public Course getObject() throws Exception {
        Course course = new Course();
        course.setCname("Java课程");
        return course;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
    @Override
    public boolean isSingleton() {
        return false;
    }
}

//Course类
public class Course {
    private String cname;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }
}

//xml配置文件
<bean id="myBean" class="com.qy.spring5.MyBeanTest.MyBean"></bean>

//测试类
//会发现获取myBean的返回值已经变为了Course
@Test
public void test(){
    //1 加载xml配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/MyBeanTest/bean.xml");
    //2 获取配置文件创建的对象
    Course course = context.getBean("myBean", Course.class);
    //3 输出获得的对象
    System.out.println(course.getCname());
}

```



# 7、IOC操作Bean管理（bean作用域）



### 1、在Spring里边，默认情况下，bean是单实例对象

```java
// 测试类
@Test
public void testBook(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");
    // 2 获取配置创建的对象
    Book book1 = context.getBean("book",Book.class);
    Book book2 = context.getBean("book",Book.class);
    // 3 输出配置创建的对象的属性
    System.out.println(book1);
    System.out.println(book2);
}
// 输出结果
com.qy.spring5.Book@365185bd
com.qy.spring5.Book@365185bd
  
两个对象地址相同，说明Spring创建的是单实例对象
```



### 2、在Spring里边，如何设置创建对象是单实例还是多实例？



==在Spring配置文件bean标签里有属性（scope）y用于设置单实例还是多实例==

scope属性值：

- 第一个值，默认值，singleton，表示单实例对象
- 第二个值，prototype，表示多实例对象

设置创建多实例对象演示：

```java
//测试类
@Test
public void testBook(){
    // 1 加载Spring配置文件
    ApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean1.xml");
    // 2 获取配置创建的对象
    Book book1 = context.getBean("book",Book.class);
    Book book2 = context.getBean("book",Book.class);
    // 3 输出配置创建的对象的属性
    System.out.println(book1);
    System.out.println(book2);
}

//xml配置文件
<bean id="book" class="com.qy.spring5.Book" scope="prototype"></bean>

//测试类输出
com.qy.spring5.Book@365185bd
com.qy.spring5.Book@18bf3d14
 
// 发现创建的对象地址不同，说明创建了多实例对象
```

> singleton 和 prototype 区别：

（1）singleton创建单实例对象，prototype创建多实例对象

（2）设置scope是singleton的时候，加载spring配置文件的时候就会创建单实例对象

​         设置scope是prototype的时候，不是在加载spring配置文件的时候创建对象，在调用getBean方法的时候创建多实例对象



# 8、IOC操作Bean管理（bean生命周期）



### （1）生命周期

 	（1）从对象创建到对象销毁的过程

### （2）bean生命周期

​	（1）通过构造器创建bean实例（无参数构造）

​	（2）为bean的属性设置值和对其他bean的引用（调用类中的set方法）

​	（3）调用bean的初始化的方法（需要配置）

​	（4）bean可以使用了（对象获取到了）

​	（5）当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）

### （3）案例演示bean的生命周期

==Order类、测试类、xml配置文件==

```java
// Order类
public class Order {

    // 无参数构造
    public Order(){
        System.out.println("第一步 通过构造器创建bean实例（无参数构造）");
    }
    public String oname;

    public void setOname(String oname) {
        this.oname = oname;
        System.out.println("第二步 为bean的属性设置值（调用set方法）");
    }

    // bean初始化方法
    public void initMethod(){
        System.out.println("第三步 调用bean的初始化的方法（需要配置）");
    }
    // bean销毁方法
    public void destroyMethod(){
        System.out.println("第五步 当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）");
    }
}


// xml配置文件
<bean id="order" class="com.qy.spring5.bean.Order" 
  init-method="initMethod" 
  destroy-method="destroyMethod">
    <property name="oname" value="手机"></property>
</bean>


// 测试类
@Test
public void test(){
    // 1 加载Spring配置文件
    ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean/bean.xml");
    // 2 获取配置文件创建的对象
    Order order = context.getBean("order", Order.class);
    System.out.println("第四步 获取到创建的bean对象");
    // 3 使用创建的对象
    System.out.println(order);
    // 4 手动让bean实例销毁
    context.close();
}

// 输出
第一步 通过构造器创建bean实例（无参数构造）
第二步 为bean的属性设置值（调用set方法）
第三步 调用bean的初始化的方法（需要配置）
第四步 获取到创建的bean对象
com.qy.spring5.bean.Order@2c039ac6
第五步 当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）

```



### （4）bean的后置处理器，加入之后，bean的生命周期有七步

​	（1）通过构造器创建bean实例（无参数构造）

​	（2）为bean的属性设置值和对其他bean的引用（调用类中的set方法）

​	==（3）把bean实例传递到bean后置处理器的方法== 

​	（4）调用bean的初始化的方法（需要配置）

​	==（5）把bean实例传递到bean后置处理器的方法== 

​	（6）bean可以使用了（对象获取到了）

​	（7）当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）

### （5）演示添加后置处理器的效果

==Order类，xml配置文件，MyPostBean类（后置处理器），测试类==

（1）Order类

```java
public class Order {

    // 无参数构造
    public Order(){
        System.out.println("第一步 通过构造器创建bean实例（无参数构造）");
    }
    public String oname;

    public void setOname(String oname) {
        this.oname = oname;
        System.out.println("第二步 为bean的属性设置值（调用set方法）");
    }

    // bean初始化方法
    public void initMethod(){
        System.out.println("第四步 调用bean的初始化的方法（需要配置）");
    }
    // bean销毁方法
    public void destroyMethod(){
        System.out.println("第七步 当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）");
    }
}
```

（2）xml配置文件

**在xml配置文件中配置后置处理器** 

```xml
<bean id="order" class="com.qy.spring5.bean.Order" init-method="initMethod" destroy-method="destroyMethod">
    <property name="oname" value="手机"></property>
</bean>

<!--创建后置处理器-->
<bean id="myBeanPost" class="com.qy.spring5.bean.MyPostBean"></bean>
```



（3）创建后置处理器

创建==MyBeanPost类==，实现接口 `BeanPostProcessor` ，这个MyBeanPost类就是后置处理器

```java
public class MyPostBean implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("第三步 把bean实例传递到bean后置处理器的方法（在初始化之前执行的方法）");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("第五步 把bean实例传递到bean后置处理器的方法（在初始化之后执行的方法）");
        return bean;
    }
}
```



（4）测试类

```java
@Test
public void test(){
    // 1 加载Spring配置文件
    ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("com/qy/spring5/bean/bean.xml");
    // 2 获取配置文件创建的对象
    Order order = context.getBean("order", Order.class);
    System.out.println("第六步 获取到创建的bean对象");
    // 3 使用创建的对象
    System.out.println(order);
    // 4 手动让bean实例销毁
    context.close();
}
```



（5）输出

```java
第一步 通过构造器创建bean实例（无参数构造）
第二步 为bean的属性设置值（调用set方法）
第三步 把bean实例传递到bean后置处理器的方法（在初始化之前执行的方法）
第四步 调用bean的初始化的方法（需要配置）
第五步 把bean实例传递到bean后置处理器的方法（在初始化之后执行的方法）
第六步 获取到创建的bean对象
com.qy.spring5.bean.Order@66d1af89
第七步 当容器关闭的时候，调用bean的销毁的方法（需要进行配置销毁的方法）

Process finished with exit code 0
```



# 9、IOC操作Bean管理（xml自动装配）



### （1）什么是自动装配？

根据指定装配规则（属性名称或者属性类型），Spring自动将匹配的属性值进行注入



### （2）演示自动装配过程

==Dept类、Emp类、xml配置文件、测试类==

（1）Dept类

```java
public class Dept {
    @Override
    public String toString() {
        return "Dept{}";
    }
}
```

（2）Emp类

```java
public class Emp {
    private Dept dept;

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    public void test(){
        System.out.println("dept ： "+dept);
    }
}
```

（3）xml配置文件 （在配置文件中开启自动装配 ）

**实现自动装配，bean标签属性autowire，配置自动装配**

**autowire属性常用两个值：**

（1）byName 根据属性名称注入，注入值bean的id值和类属性名称不一样

（2）byType根据属性类型注入

```xml
<bean id="emp" class="com.qy.spring5.autowire.Emp" autowire="byName"></bean>
<bean id="dept" class="com.qy.spring5.autowire.Dept"></bean>
```

（4）测试类

```java
public class test {
    @Test
    public void test(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/autowire/bean.xml");
        Emp emp = context.getBean("emp", Emp.class);
        emp.test();
    }
}
```

输出：

```
dept ： Dept{}
```



# 10、IOC操作Bean管理（基于注解方式）

### 1、什么是注解

（1）注解是代码特殊标记，格式：@注解名称（属性名称=属性值）

（2）注解作用在类，方法，属性上

（3）使用注解目的：简化xml配置

### 2、Spring针对Bean管理创建对象提供注解

（1）@Component

（2）@Service

（3）@Controller

（4）@Repository



### 3、基于注解方式实现对象创建

（1）引入依赖

使用到注解需要引入  ==spring-aop-5.2.6.RELEASE==   jar包

（2）开启组件扫描

在xml配置文件中，需要引入context命名空间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd"
>

    <!--
        开启组件扫描
           1 如果扫描多个包，多个包用逗号隔开
           2 写上需要扫描包的上层目录
             扫描 com.qy 包下所有的类
    -->
    <context:component-scan base-package="com.qy"></context:component-scan>

</beans>
```



（3）创建UserService类和测试类

==UserService类== 

```java
@Service
public class UserService {
    public void add(){
        System.out.println("service add...");
    }
}

```

==测试类==

```java
public class test {
    @Test
    public void test(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/zhujie/bean.xml");
        // 这里getBean的name属性值，默认是需要创建对象的类的首字母小写
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
    }
}

//输出
service add...
```



### 4、开启组件扫描细节配置

```xml
<!--示例1
        use-default-filters="false" 表示现在不适用默认filter，自己配置filter
        context:include-filter ， 设置扫描哪些内容
-->
<context:component-scan base-package="com.qy" use-default-filters="false">
    <context:include-filter type="annotation"
                            expression="org.springframework.stereotype.Controller"/>
</context:component-scan>

<!--示例2
        下面配置扫描包的所有内容
        context:exclude-filter ， 设置哪些内容不扫描
-->
<context:component-scan base-package="com.qy">
    <context:exclude-filter type="annotation" 
                            expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```



### 5、基于注解方式实现属性注入

四个注解：@Autowired @Qulifier @Resource @Value

（1）@AutoWired：根据属性类型进行自动注入

**案例演示**：

使用到的类： Service类、Dao类

第一步：把Service和Dao对象创建 （在Service类上添加@Service，在Dao类上添加@Repository）

第二步：在Service中注入Dao对象，在Service中添加dao类型属性，在属性上添加注解@Autowired

```java
/***************代码演示 **********************/
//Service类
public class Service{
  @Autowired
  private Dao dao;
  public void add(){
    System.out.println("service add ...");
    dao.add();
  }
}

//Dao类
public class Dao{
  public void add(){
    System.out.println("dao add ...");
  }
}

//XML配置文件  记得引入context命名空间，上边有
 <context:component-scan base-package="com.qy"></context:component-scan>

//测试类
public class test {
    @Test
    public void test(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/zhujie/bean.xml");

        // 这里getBean的name属性值，默认是需要创建对象的类的首字母小写
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
    }
}

//输出结果
service add...
dao add...
```



（2）@Qualifier：根据属性名称进行注入，需要和@Autowired配合使用

**@Qualifier**注解用在什么场合呢？

我们来举个例子：

比如说现在有 Service类，Dao接口，DaoImpl1实现类（实现Dao接口），DaoImpl2实现类（实现Dao接口），这时候我们再使用@Autowired根据类型进行注入的话，因为有两个实现类，那么就不知道要注入哪个实现类了，所以要使用@Qualifier注解。

案例演示：

UserService类，UserDao接口，UserDaoImpl1实现类，UserDaoImpl2实现类，测试类

```java
//UserService类
@Service(value = "userService")
public class UserService {
    @Autowired
    @Qualifier(value = "userDaoImpl1")
    private UserDao userDaoImpl1;
    @Autowired
    @Qualifier(value = "userDaoImpl2")
    private UserDao userDaoImpl2;

    public void add(){
        System.out.println("service add ...");
        userDaoImpl1.add();
        userDaoImpl2.add();
    }
}

//UserDao接口
public interface UserDao {
    public void add();
}

//UserDaoImpl1实现类
@Repository(value = "userDaoImpl1")
public class UserDaoImpl1 implements UserDao{
    @Override
    public void add() {
        System.out.println("UserDaoImpl1 第一个实现类 add ...");
    }
}

//UserDaoImpl2实现类
@Repository(value = "userDaoImpl2")
public class UserDaoImpl2 implements UserDao{
    @Override
    public void add() {
        System.out.println("UserDaoImpl2 第二个实现类 add ...");
    }
}

//XML配置文件
<context:component-scan base-package="com.qy"></context:component-scan>

//测试类
public class test {
    @Test
    public void test(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/qualifier/bean.xml");
        // 这里getBean的name值默认是UserService首字母小写，可以在UserService上通过注解设置 @Service(value = "")
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
    }
}

//输出
service add ...
UserDaoImpl1 第一个实现类 add ...
UserDaoImpl2 第二个实现类 add ...
```



（3）@Resource：可以根据类型注入，可以根据名称注入

上边案例已经写的很清楚，这里简单写一下

@Resource根据类型注入

```java
@Resource
private UserDao userDaoImpl;
```

@Resource根据名称注入

和@Qualifier的区别，@Resource可以自己单独使用，@Qualifier需要和@Autowired配合使用

```java
@Resource(name = "userDaoImpl2")
private UserDao userDaoImpl2;
```



（4）@Value：注入普通类型属性

```java
@Value(value = "abc")
private String name;  // 将abc注入到属性name中
```



# 11、IOC操作Bean管理（完全注解开发）

（1）创建配置类（添加@Configuration注解），替代xml配置文件 

```java
@Configuration // 作为配置类，替代xml配置文件
@ComponentScan(basePackages = {"com.qy"})
public class SpringConfig {
}
```

（2）之前加载xml配置文件的代码也要修改为加载配置类

```java
public class test {
    @Test
    public void test(){
        // 加载配置类
        AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(SpringConfig.class);
        
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
    }
}
```



# 12、AOP

### 12.1 基本概念

（1）面向切面编程，利用 AOP 可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可用性，同时提高了开发的效率。

（2）通俗描述：不通过修改源代码方式，在主干功能里面添加新功能。

### 12.2 AOP底层原理

（1）AOP底层使用动态代理

 -  有两种情况动态代理

第一种  有接口情况，使用 JDK动态代理

创建接口实现类代理对象，增强类的方法

```java
interface UserDao{
  public void login();
}

class UserDaoImpl implements UserDao{
  public void login(){
    //登录实现过程
  }
}

JDK动态代理
创建UserDao接口实现类代理对象
```



第二种  没有接口情况，使用 CGLIB 动态代理

```java
class User{
  public void add(){
    ...
  }
}

//子类
class Person extends User{
  public void add(){
    super.add();
    //增强逻辑
  }
}

CGLIB动态代理
创建当前类子类的代理对象
```

### 12.3 AOP（JDK动态代理）

（1）使用JDK动态代理，使用 Proxy 类里面的方法创建出代理对象

java.lang.reflect.Proxy

 - 调用 newProxyInstance 方法

```java
public static Object newProxyInstance(
ClassLoader loader, 
类<?>[] interfaces,
InvocationHandler h) throws IllegalArgumentExceptio
```

第一个参数：类加载器来定义代理类

第二个参数：增强方法所在的类，这个类实现的接口，支持多个接口

第三个参数：实现这个接口 InvocationHandler，创建代理对象，写增强的方法

### 12.4 编写JDK动态代理代码

加强UserDao接口的add方法。

用到了==UserDao接口，UserDaoImpl实现类，JDKProxy类（创建动态代理）==

UserDao接口

```java
public interface UserDao {
    public int add(int arg1,int arg2);
    public String update(String id);
}
```

UserDaoImpl实现类

```java
public class UserDaoImpl implements UserDao{
    @Override
    public int add(int arg1, int arg2) {
        System.out.println("add方法执行...");
        return arg1 + arg2;
    }

    @Override
    public String update(String id) {
        return id;
    }
}
```

JDKProxy类

```java
public class JDKProxy {

    public static void main(String[] args) {

        /*
            三个参数：  类加载器、类实现的接口、InvocationHandler
        */
        // 2 第二个参数
        Class[] instances = {UserDao.class};
        UserDaoImpl userDao = new UserDaoImpl();
        // 1 调用Proxy的newProxyInstance方法创建动态代理
        UserDao dao = (UserDao) Proxy.newProxyInstance(JDKProxy.class.getClassLoader(), instances, new UserDaoProxy(userDao));
        int res = dao.add(1, 2);
        System.out.println("res : "+res);
    }

}
// 3 创建代理对象代码 （第三个参数）
class UserDaoProxy implements InvocationHandler{

    // 创建谁的代理对象，就把谁传过来
    // 使用有参数构造的方法进行传递
    // 这里应该传递UserDaoImpl，为了更通用，传递Object
    
    private Object obj;
    public UserDaoProxy(Object obj){
        this.obj = obj;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 方法执行之前的增强
        System.out.println("方法执行之前..."+method.getName()+" 方法的参数："+ Arrays.toString(args));

        // 方法执行
        Object result = method.invoke(obj, args);

        // 方法执行之后的增强
        System.out.println("方法执行之后..."+method.getName());
        return result;
    }
}
```

执行JDKProxy后的输出：

```
方法执行之前...add 方法的参数：[1, 2]

add方法执行...

方法执行之后...add

res : 3

Process finished with exit code 0

```



### 12.5 AOP操作术语

1、连接点  2、切入点  3、通知（增强）  4、切面

**连接点**

类里面哪些方法可以被增强，这些方法称为连接点

**切入点**

实际被真正增强的方法，成为切入点

**通知**

（1）实际增强的逻辑部分称为通知（增强）

（2）通知有多种类型

* 前置通知
* 后置通知
* 环绕通知
* 异常通知
* 最终通知（finally）

**切面**

是动作

（1）把通知应用到切入点的过程 



### 12.6 AOP操作演示(准备工作)

（1）Spring框架一般都是基于 AspectJ

 - 什么是AspectJ

AspectJ 不是Spring 组成部分，独立AOP框架，一般把AspectJ和Spring框架一起使用，进行AOP操作

（2）基于 AspectJ 实现AOP操作

 - 基于xml配置文件实现
 - 基于注解方式实现（使用）

（3）引入依赖


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200622110157518.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ1MjYwNjE5,size_16,color_FFFFFF,t_70)


（4）切入点表达式

 - 切入点表达式作用：知道对哪个类里面的哪个方法进行增强
 - 语法结构：
    - `execution( [权限修饰符][返回类型][类全路径][方法名称]([参数列表]) )`

举例1：对com.qy.dao.BookDao类里面的add方法进行增强

`execution(* com.qy.dao.BookDao.add(..))`

权限修饰符可以是public private都可，*代表可以是任意的权限修饰符

举例2：对com.qy.dao.BookDao类里面的所有方法进行增强

`execution(* com.qy.dao.BookDao.*(..))`

举例1：对com.qy.dao包里的所有类里面的所有方法进行增强

`execution(* com.qy.dao.*.*(..))`

### 12.7 AOP操作演示(AspectJ注解)

#### 1、创建类，在类里面定义方法

```java
public class User {
    public void add(){
        System.out.println("add ......");
    }
}
```

#### 2、创建增强类（编写增强逻辑）

在增强类里面，创建方法，让不同的方法代表不同的通知类型

```java
public class UserProxy {
    // 前置通知
    public void before(){
        System.out.println("before ......");
    }
}
```



#### 3、进行通知的配置

（1）在spring配置文件中，开启注解扫描（添加context和aop命名空间）

```xml
<!--开启注解扫描-->
<context:component-scan base-package="com.qy"></context:component-scan>
```



（2）使用注解创建User和UserProxy对象

```java
@Component // 被增强的类
public class User {
```



（3）在增强类上添加注解@Aspect

```java
@Component // 增强类
@Aspect // 生成代理对象
public class UserProxy {
```



（4）在spring配置文件中开启生成代理对象

```xml
<!--开启Aspect生成代理对象-->
<!--如果在类上发现@Aspect注解，就把该类生成对代理对象-->
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
```





#### 4、配置不同类型的通知

在增强类的里面 ，在作为通知方法上面添加通知类型注解，使用切入点表达式配置

```java
@Component // 增强类
@Aspect // 生成代理对象
public class UserProxy {
    // 前置通知
    //@Before注解表示作为前置通知
    @Before(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void before(){
        System.out.println("before ......");
    }
}
```



#### 5、完整代码

==User类==

```java
@Component // 被增强的类
public class User {
    public void add(){
        System.out.println("add ......");
    }
}
```

==UserProxy增强类==

```java
@Component // 增强类
@Aspect // 生成代理对象
public class UserProxy {
    // 前置通知
    //@Before注解表示作为前置通知
    @Before(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void before(){
        System.out.println("before ......");
    }
}
```

==XML配置文件==

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启注解扫描-->
    <context:component-scan base-package="com.qy"></context:component-scan>

    <!--开启Aspect生成代理对象-->
    <!--如果在类上发现@Aspect注解，就把该类生成对代理对象-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>

</beans>
```

==测试类==

```java
public class test {
    @Test
    public void test(){
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("com/qy/spring5/aopanno/bean.xml");
        User user = context.getBean("user", User.class);
        user.add();
    }
}
```

输出:

before ......
add ......

#### 6、增强类的其他类型通知

```java
@Component // 增强类
@Aspect // 生成代理对象
public class UserProxy {
    // 前置通知
    //@Before注解表示作为前置通知
    @Before(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void before(){
        System.out.println("before ......");
    }

    //后置通知（也叫做最终通知）
    @After(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void after(){
        System.out.println("after ......");
    }

    //返回通知（如果有异常，不会执行返回通知）
    @AfterReturning(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void afterReturning(){
        System.out.println("afterReturning ......");
    }

    //异常通知
    @AfterThrowing(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void afterThrowing(){
        System.out.println("afterThrowing ......");
    }

    //环绕通知
    @Around(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void before(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        System.out.println("环绕之前 ......");
        // 执行被增强的方法
        proceedingJoinPoint.proceed();
        System.out.println("环绕之后 ......");
    }
}
```

执行测试后的输出：

```
环绕之前 ......

before ......

add ......

环绕之后 ......

after ......

afterReturning ......

```



#### 7、相同的切入点抽取

我们可能很多通知的切入点都是同一个方法或是同一个类，那么我们就可以把它抽取出来

UserProxy增强类:

```java
@Component // 增强类
@Aspect // 生成代理对象
public class UserProxy {

    //抽取相同的切入点
    @Pointcut(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void pointdemo(){

    }

    // 前置通知
    // value值写我们抽取出来的相同切入点的方法名
    @Before(value = "pointdemo()")
    public void before(){
        System.out.println("before ......");
    }
}
```

#### 8、有多个增强类对同一个方法进行增强，设置增强类的优先级

==在增强类上面添加注解@Order(数字类型值)==，数字类型值越小优先级越高

```java
@Component
@Aspect
@Order(1)
public class PersonProxy {

    @Before(value = "execution(* com.qy.spring5.aopanno.User.add(..))")
    public void before(){
        System.out.println("Person before ......");
    }

}
```

