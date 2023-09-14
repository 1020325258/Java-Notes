# 0、基础

## （1）为什么静态方法不能访问成员方法

在加载类的时候，先将类中的静态方法加载到方法区中，然后才在堆中创建对象，如果在静态方法中访问成员变量，那么加载静态方法时，该变量并不存在，所以会出错。

## （2）字符串常量池

`JDK1.7之后，字符串常量池从方法区中移动到了堆中`



- **String str1 = new String("aa")的过程**

  1 、这里的aa在类加载的时候，会在字符串常量池里创建一个 "aa"对象，这是第一个对象

  2、类加载完成之后，开始执行代码，执行该行代码时new一个"aa"的String对象存放在Java堆中，这是第二个对象

  3、创建完第二个对象后，虚拟机栈上的str1会指向第二个对象，也就是堆中的对象

  **练习**

  ```java
  一：
  String str1 = new String("aa");
  String str2 = "aa";
  System.out.println(str1 == str2);  // false

  二：
  String str1 = new String("aa");  
  str1.intern();                
  String str2 = "aa";
  System.out.println(str1 == str2); // false

  首先解释intern()方法：
  intern()判断字符串常量池中是否有该字符串，如果有则返回该常量，如果没有，说明字符串在堆中，则把堆中该对象的引用加入到字符串常量池中，以后别人拿到的是该字符串常量的引用，实际上在堆中。

  这里为什么是false呢？
  对于该段代码，第一条语句 String str1 = new String("aa") 这条语句创建了两个对象，一个在字符串常量池，一个在堆中，而调用 str1.intern() 方法时，会发现字符串常量池已经有该字符串了，就不会在字符串常量池中添加一个指向堆中 "aa" 的引用，所以 str1 指向堆中地址，str2 指向字符串常量池中地址，所以并不相同

  三：
  String str1 = new String("a") + new String("a");
  str1.intern();
  String str2 = "aa";
  System.out.println(str1 == str2); // true

  为什么结果是true呢？
  当执行 String str1 = new String("a") + new String("a"); 在常量池中创建的对象是 "a" 而不是 "aa"，所以调用 str1.intern() 的时候，发现字符串常量池中并没有 "aa" 这个对象，此时就会在常量池中添加一个指向堆中的 "aa" 对象的引用，也就是说这个时候 str1 指向的是堆中的 "aa" 对象，str2指向的也是堆中的对象，所以结果为true
  ```

  **当执行 String str1 = new String("a") + new String("a"); 会创建几个对象呢？**

  使用 + 号的String字符串拼接，底层都是先创建一个StringBuilder对象，然后调用append把要拼接的字符串都append进去，最后使用toString()创建一个新的String对象

  所以总共创建5个对象，先创建一个StringBuilder对象（①），再通过两次new String("a")创建两个对象（②③），最后调用toString()创建一个对象（④），字符串常量池中还有一个对象 "a" （⑤）





字符串常量池有利于为Java运行节省大量空间，要想实现字符串常量池，也就必须让String为final类型的，才可以保证节省空间。

`String s = "str"` 创建字符串时，首先会在字符串常量池找是否有相同值的字符串，如果发现则返回其引用，否则就在字符串常量池创建一个新的字符串，返回其引用

`String s = new String("str")` 创建字符时，会在堆中创建一个新的String对象。可以使用`intern()`方法将其放入字符串常量池或者从字符串常量池找到它的引用

```java
String s = new String("str"); // 指向堆中的str
String s1 = s.intern(); // 指向字符串常量池的str
String s2 = "str";
System.out.println(s == s2); // false
System.out.println(s1 == s2); //true
```



## （3）为什么String是final类型的？

- 为了实现字符串池（只有当字符是不可变的，字符串池才有可能实现）
- 为了线程安全（字符串自己便是线程安全的）
- 为了实现String可以创建HashCode不可变性



## （4）内部类

为什么使用内部类？

- 内部类方法可以访问该类定义所在的作用域中的数据，包括私有的数据（内部类可以访问所在的类的私有数据）
- 内部类可以对同一个包中的其他类隐藏起来（封装性更好）
- 想要定义一个回调函数且不想编写大量代码时，使用匿名内部类比较便捷

代码示例

```java
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

public class OuterClass {
    private String outerName;
    public void display(){
        System.out.println("OuterClass Display");
        System.out.println(outerName);
    }
    // 内部类
    public class InnerClass{
        private String innerName;
        public void display(){
            System.out.println("InnerClass Display");
            System.out.println(innerName);
        }
        public InnerClass(){
            innerName = "inner class";
        }
    }

    public static void main(String[] args) {
        OuterClass outerClass = new OuterClass();//创建外部类实例对象
        InnerClass innerClass = outerClass.new InnerClass();//内部类的创建必须依赖于外部类
        outerClass.display();// 输出：OuterClass Display  null
        innerClass.display();// 输出：InnerClass Display  inner class
    }
}

```



#### 局部内部类

即写在方法体中的内部类，不能写public访问修饰符

代码示例

```java
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

public class OuterClass {
    private String outerName;
    public void display(){
        //声明局部内部类不要加public，因为它的作用域被限定在声明这个局部类的块中
        //除了display方法，没有任何方法知道这个局部类的存在
        class InnerClass{ // 局部内部类
            public void print(){
                System.out.println("method inner class");
            }    
        }
        InnerClass innerClass = new InnerClass(); // 直接创建内部类对象
        innerClass.print(); // 执行内部类方法
    }
}

```

#### 静态内部类

静态内部类的构造不需要依赖于外部对象，也就是可以直接以下边这种方式创建内部类

```java
InnerClass innerclass = new InnerClass();//静态内部类的创建不依赖于外部类
```



#### 匿名内部类

 主要应用在接口的实现。

```java
public class Test {
    public static void main(String[] args) {
        // 匿名内部类可以很方便的实现接口
        MyInterface myInterface = new MyInterface() {
            @Override
            public void test() {
                System.out.println("匿名内部类的实现");
            }
        };
        myInterface.test();
    }
}
```



# 1、面向对象

面向对象更注重在某一事情中的参与者是谁，将每个参与者创建为一个对象。

优点：更加方便复用、拓展和维护

面向过程更加注重处理事情的步骤，将每一个步骤创建为一个函数。

优点：直接高效

## 封装

封装的意义，在于明确标识出允许外部使用的所有成员函数和数据项，内部细节对外部调用透明，外部调用无需修改或者关心内部实现。

1、javabean的属性私有，通过get、set方法让外界进行访问，属性的赋值或者获取的逻辑只能由javabean本身决定，不能由外部进行修改

2、orm框架

操作数据库，不需要关心如何建立连接，sql如何执行，只需要引入mybatis框架即可操作

## 继承

子类继承父类（extends），并做出自己的改变或拓展

## 多态

多态即同一个行为具有多个不同表现形式，一个接口使用不同的实例而执行不同操作。

多态3个条件：继承，方法重写，父类引用指向子类对象

但是不能执行子类特有的方法，如果要执行子类的特有的方法，需要`((Cat) cat).test`这样才可以执行。

```java
public class Animal {
    void eat(){}
    void work(){}
}

public class Cat extends Animal {
    public void eat(){
        System.out.println("吃鱼");
    }

    public void work(){
        System.out.println("抓老鼠");
    }
    
    public void test(){
        System.out.println("test");
    }
}

public class Dog extends Animal {
    public void eat(){
        System.out.println("吃肉");
    }

    public void work(){
        System.out.println("看门");
    }
}

public class exe {
    public static void main(String[] args) {
        Animal cat = new Cat();
        Animal dog = new Dog();
        cat.eat();
        cat.work();
        dog.eat();
        dog.work();
        /**
         * 输出：
         * 吃鱼
         * 抓老鼠
         * 吃肉
         * 看门
         */
    }
}

```



# 2、JDK和JRE

JRE（Java Runtime Environment）是Java运行环境，面向Java程序的使用者，而不是开发者

JDK（Java Development Kit）是Java开发工具包，包含了JRE

JVM（Java Virtual Machine）

JRE包含了bin（jvm）和lib（类库）

`.java文件`经过javac编译形成`.class文件`会放到jvm上，



# 3、==和equals

==对比的是栈中的值，对于基本数据类型比较的是变量值，对于引用类型比较的是堆中内存对象的地址

equals：object中默认也是采用==比较，通常会重写

```java
public class StringDemo {
  public static void main(String args[]) {
    String str1 = "Hello";// Hello存放在堆中的常量池里
    String str2 = new String("Hello");// Hello在堆中分配内存
    String str3 = str2;// 引用传递
    System.out.println(str1 == str2);// false
    System.out.println(str1 == str3);// false
    System.out.println(str2 == str3);// true
    System.out.println(str1.equals(str2));// true
    System.out.println(str1.equals(str3));// true
    System.out.println(str2.equals(str3));// true
  }
}
```

# 4、堆和栈

Java程序运行时，在内存划分5片空间进行数据的存储。分别是：寄存器、本地方法区、方法区、栈、堆。

基本数据类型、局部变量都是存放在栈内存中的，用完就消失。

new创建的实例化对象及数组，是存放在堆内存中的，用完之后靠垃圾回收机制不定期自动消除。

实例

```java
main(){
  int[] x = new int[3];
}
```

程序执行步骤：
JVM执行 main() 函数，在栈内存中开辟一个空间，存放x变量

同时在堆内存中开辟一个空间，存放int数组，堆内存会自动存储内存首地址值，假设首地址0x0045

数组在栈内存中的地址值，会赋给x，这样x有了地址值，x就指向了这个数组。

此时栈中内容（存放值在堆中地址）为：

```
x ： 0x0045
```

此时堆中内容（存放真正的值）为：

```
new int[3];
[0] = 0;
[1] = 0;
[2] = 0;
```



栈：

栈内存中，数据一执行完毕，变量会立即释放，节约内存空间。

堆：

当堆中的实体（也就是数据）不再被指向时，JVM启动垃圾回收机制，自动清除。



# 5、final

修饰类：表示类不可以被继承

修饰方法：表示方法不可以被子类覆盖，但是可以重载

修饰变量：表示变量一旦被赋值就不可以更改它的值

（1）修饰成员变量

- 如果final修饰的是类变量，只能在静态初始块中指定初始值或者声明该类变量时指定初始值
- 如果final修饰的是成员变量，可以在非静态初始化块、声明该变量或者构造器中执行初始值

（2）修饰局部变量

系统不会为局部变量进行初始化，局部变量必须由程序员初始化。因此使用final修饰局部变量时，即可以在定义时指定默认值，也可以不指定默认值，而在后面的代码中对final变量赋初值。

```java
public class FinalVar {
  final static int a = 0; // 声明时进行赋值，也可以用静态代码块赋值
  /**
  static {
    a = 0;
  }
  */
  final int b = 0; // 声明时需要赋值，或者代码块中赋值，或者构造器赋值
  /**
  {
  	b = 0;
  }
  */
  public static void main(String[] args) {
    final int localA; // 局部变量只声明没有初始化，不会报错
    localA = 0; // 使用之前要进行初始化
  }
}
```

静态初始块

```java
给类进行初始化，随着类的加载而执行，且只执行一次
public class Book {
	private static String defaultName;
    static {
    	defaultName = "default book";
    	System.out.println("default book");
    }
}
```

（3）修饰基本类型数据和引用类型数据

- 如果是基本类型的变量，初始化后不能更改
- 如果是引用类型变量，初始化后不能让其指向另一个对象，**但是引用的值是可以变的。**

```java
// 引用类型变量
public class FinalReferenceTest {
  public static void main() {
    final int[] iArr = {1, 2, 3, 4};
    iArr[2] = -3; // 合法
    iArr = null; //非法，对iArr不能重新赋值
    
    final Person p = new Person(25);
    p.setAge(24); // 合法
    p = null; // 非法
  }
}
```

**局部内部类和匿名内部类只能访问局部final变量**

```java
public class Test{
  public static vodi main(String[] args){
  }
  // 局部final变量a,b
  public void test(final int b){
    final int a = 10;
    // 匿名内部类
    new Thread(){
      public void run(){
        System.out.println(a);
        System.out.println(b);
      };
    }.start();
  }
}

class OutClass{
  private int age = 12; // 内部类访问成员变量不用加final
  public void outPint(final int x) {
    class InClass{
      public void InPrint(){
        System.out.println(x);
        System.out.println(age);
      }
    }
    new InClass().InPrint();
  }
}
```

为什么呢？

首先，内部类和外部类处于同一级别，内部类不会因为定义在方法中就会随着方法的执行完毕而销毁

因此就产生了问题，当外部类的方法结束时，局部变量就被销毁了，但是内部类对象可能还存在。这里就存在一个矛盾：内部类访问一个不存在的变量，为了解决这个问题，就将局部变量复制了一份作为内部类的成员变量，这样当局部变量死亡后，内部类仍可以访问它，实际访问的是局部变量的“copy”，这样就好像延长了局部变量的生命周期

将局部变量赋值为内部类的成员变量时，必须保证这两个变量是一样的，也就是如果我们在内部类中修改了成员变量，方法中的局部变量也得跟着改变，怎么解决这个问题呢？

就将局部变量设置为final，对它初始化后，我就不让你再去修改这个变量，就保证了内部类的成员变量和方法的局部变量的一致性。这实际上也是一种妥协。使得局部变量与内部类内建立的拷贝保持一致。



# 6、String、StringBuffer、StringBuilder区别及使用场景

String是final修饰的，不可变，每次操作都会产生新的String对象

StringBuffer和StringBuilder都是在原对象上操作

StringBuffer是**线程安全**的，StringBuilder是**线程不安全**的。

StringBuffer方法都是synchronized修饰的



场景：优先使用StringBuilder，多线程使用共享变量时使用StringBuffer



# 7、重载和重写的区别

重载：发生在同一个类中，方法名必须相同，参数类型不同、个数不同、顺序不同，方法返回值和访问修饰符可以不同，发生在编译时。

重写：发生在父子类，方法名、参数列表必须相同，返回值范围小于等于父类，抛出的异常范围小于等于父类，访问修饰符范围大于等于父类；如果父类方法访问修饰符为private则子类就不能重写该方法

```java
public int add(int a, String b)
public String add(int a, String b)
// 编译报错，返回值类型不同，不是重载。
```



# 8、接口和抽象类的区别

- 抽象类可以存在普通成员函数（即可以存在实现了的方法），而接口中只能存在public abstact （抽象）方法
- 抽象类的成员变量可以是各种类型的，而接口中的成员变量只能是public static final类型的
- 抽象类只能继承一个，接口可以实现多个

接口设计的目的，是对类的行为进行约束，约束了行为的有无，但不对如何实现行为进行限制。

抽象类的设计目的，是代码复用，将不同类的相同行为抽象出来，抽象类不可以实例化。

抽象类的功能远超过接口，但是定义抽象类的代价高。

抽象类是用于进行继承的，如果定义了抽象类而不去继承则毫无意义。

**设计层面上的区别：**

抽象类是对一种事物的抽象，是对整个类进行抽象

接口是对行为的抽象（如飞机和鸟都会飞）



# 9、List和Set的区别

List：有序，按对象进入的顺序保存对象，可重复，允许多个null元素，可以使用Iterator取出所有元素，还可以使用`get(int index)`获取指定下标的元素

Set：无序，不可重复，最多允许有一个Null元素对象，取元素时只能用`Iterator`接口取得所有元素，再逐一遍历各个元素



# 10、hashCode与equals

### hashCode介绍

hashCode()的作用是获取哈希码，也成为散列码，它实际上是返回一个int整数，这个哈希码的作用是确定该对象在哈希表中的索引位置==（即数据在堆中存放的地址）==。hashCode()定义在JDK的Object.java中，Java中的任何类都包含有hashCode()函数。散列表存储的是键值对（key-value），它的特点是：能根据“键”快速检索出对应的“值”。这其中就利用到了散列码。

### 为什么要有hashCode

**HashCode的存在主要是为了查找的快捷性**

**以“HashSet如何检查重复”为例子来说明为什么要有hashCode：**

对象加入HashSet时，HashSet会先计算对象的hashCode值来判断对象加入的位置，看该位置是否有值，如果没有，HashSet会假设对象没有重复出现。但是如果发现有值，这是会调用`equals()`方法来检查两个对象是否真的相同，如果两者相同，HashSet就不会让其加入集合，如果不同，就会再次散列到其他位置，这样就大大减少了调用equals的次数，大大提高了执行速度。

- 如果两个对象相同，就适用于`equals(java.lang.Object)`方法，那么这两个对象的hashCode一定要相同
- 如果对象的equals方法被重写，那么对象的hashCode方法也要重写，并且产生hashCode使用的对象一定要和equals方法中使用的一致，否则会违反上一条原则



# 11、ArrayList和LinkedList区别

ArrayList：基于动态数组，连续内存存储，适合下标访问，扩容机制：因为数组长度固定，超出长度存储据需要新建数组，然后将老数组的数据拷贝到新数组，如果不是尾部插入数据还会涉及到元素的移动，所以使用尾插法并且指定初始容量可以极大提升性能，甚至超过LinkedList（LinkedList在大量插入元素时，大量创建node对象比较耗费性能）



LinkedList：基于链表，可以存储在分散的内存中，适合做数据插入以及删除操作，不适合查询，必须使用`Iterator`进行遍历，不能根据下标访问。



# 12、HashMap和HashTable的区别？底层实现是什么？

区别：

（1）HashMap方法没有synchronized修饰，线程非安全，HashTable线程安全；

（2）HashMap允许key和value为null，而HashTable不允许

**底层实现**：数组 + 链表实现



jdk8的时候，当链表高度到8，数组长度超过64，链表转变为红黑树，元素以内部类Node节点存在

- 计算key的hash值，二次hash然后对数组长度取模，对应到数组下标
- 如果没有产生冲突，则直接创建Node存入数组
- 如果产生冲突，先进行equal比较，相同则取代该元素，不同，则判断链表高度插入链表，链表高度达到8，并且数组长度到64则变为红黑树，长度小于6则将红黑树转回链表
- key为null，存在下标0的位置

数组扩容和ArrayList相同。



### HashMap的线程安全方式

- 方法一：通过 Collections.synchronizedMap()返回一个新的Map，这个新的map就是线程安全的，这个要求大家习惯基于接口编程，因为返回的不是HashMap，而是一个Map的实现
- 方法二：重新改写了HashMap，具体的可以查看java.util.concurrent.ConcurrentHashMap，这个方法比方法一有了很大的改进

方法一特点：

通过Collections.synchronizedMap()封装不安全的HashMap方法，封装的关键有两处（1）使用synchronized来互斥（2）使用了代理模式new一个新的类，这个类同样实现Map接口，在HashMap上面，synchronized锁住的是对象。缺点：锁住了尽可能大的代码块，性能较差

方法二特点：重写了HashMap，使用了新的锁机制，把HashMap进行了拆分，拆分成多个独立的代码块，减少了所冲突的可能。缺点：代码繁琐



# 13、ConcurrentHashMap原理，jdk7和jdk8版本的区别：暂时留（收藏夹）





# 14、字节码

**java中的编译器和解释器**

java中引入了虚拟机的概念，即在机器和编译程序之间加入了一层抽象的虚拟机器。

编译程序面向虚拟机，生成虚拟机可以理解的代码==（即字节码，也就是.class文件）==，然后由解释器来将虚拟机代码转换为特定系统的机器码执行。

一次编译到处运行，即编译好形成.class文件，可以放在任何平台上运行，只要装有虚拟机。

Java源代码---->编译器---->jvm可执行的java字节码---->jvm---->jvm中解释器---->机器可执行的二进制机器码---->程序运行

**采用字节码的好处**

Java语言通过字节码的存在，



# 15、双亲委派模型（留）



# 16、Java中的异常体系

Java中所有的异常都来自顶级父类`Throwable`

Throwable有两个子类`Exception`和`Error`

`Error`是程序无法处理的错误，一旦出现，则程序将被迫停止运行，如内存泄漏（OOM，out of memory）

`Exception`不会导致程序停止，又分为两个部分`RunTimeException运行时异常`和`CheckedException检查异常`

`RunTimeException`常常发生在程序运行过程中，会导致程序当前线程执行失败，`CheckedException`常常发生在编译的时候，会导致编译不通过



# 17、GC如何判断对象可以被回收

- 引用计数法（Java没有使用，其他语言有使用）：每个对象有一个引用计数属性，新增一个引用时计数加1，引用释放时减1，计数为0时回收

- 可达性分析法：从 GC Roots 开始向下搜索，搜索所走过的路径成为引用链。当一个对象到 GC Root 没有任何引用链相连时，则证明此对象是不可用的，那么虚拟机就判断是可回收对象。（发现对象不在引用链中，不会直接回收，对象还有一次机会，后边的略..）

  > 引用计数法虽然效率较高，但是可能出现A引用B，B引用A的情况，即使他们都不再使用了，但是因为相互引用导致无法被回收

GC Roots 的对象有：

- 虚拟机栈（栈帧中的本地变量表）中引用的对象

  - ```java
    例如
    当执行到下边这个方法时，add函数入栈
    void add(){
    	new User(); // 创建了一个User对象，这个User对象就是GC Roots，这个User所引用的对象不可以被GC回收，当函数调用完毕之后，add函数出栈，GC Roots被回收。（大概是这样）
    	// GC在进行对象回收时，会以还在栈中的对象为GC Roots来进行可达性分析797448070
    }
    ```

  - ​

- 方法区中类静态属性引用的对象

- 方法区中常量引用的对象

- 本地方法栈中JNI（即一般说的Native方法）引用的对象





# 18、线程的生命周期

线程五种状态：创建、就绪、运行、阻塞和死亡

阻塞的情况分为三种：

（1）等待阻塞：运行的线程执行wait方法`（是Object类中的方法）`，该线程释放所占用的资源，JVM会把线程放入`"等待池"`中。进入这个状态后，是不能自动唤醒的，必须依靠其他线程调用notify或notifyAll方法才能被唤醒。

（2）同步阻塞：运行的线程在获取对象的同步锁时，若该同步锁被别的线程占用，则JVM会把该线程放入`"锁池"`中。

（3）其他阻塞：运行的线程执行sleep或join方法，或者发出了IO请求时，JVM会把该线程设置为阻塞状态。当sleep状态超时、join等待线程终止或者超时、或者IO处理完毕时，线程重新转入就绪状态。sleep是Thread类的方法



1、新建状态（New）：新创建了一个线程对象

2、就绪状态（Runnable）：线程创建后，其他线程调用了该对象的start方法。该状态的线程位于可运行线程池中，等待获取cpu使用权

3、运行状态（Running）：就绪状态的线程获取了cpu，执行程序代码

4、阻塞状态（Blocked）：阻塞状态是线程因为某种原因放弃cpu使用权，暂时停止运行。直到线程进入就绪状态，才有机会转到运行状态

5、死亡状态（Dead）：线程执行完了或者因为异常推出了run方法，该线程结束生命周期



# 19、sleep、wait、join、yield的区别

1、锁池

所有需要竞争同步锁的线程会放在锁池中，竞争得到同步锁之后就会进入就绪队列进行等待cpu资源分配

2、等待池

调用`wait()`方法后，线程会放到等待池中，等待池的线程是不会去竞争同步锁。只有调用了notify()或notifyAll()后，等待池的线程才回去竞争锁。

> notify()是随机从等待池选出一个线程放到锁池，notifyAll()是将等待池的所有线程放到锁池当中



1、sleep 是 Thread 类的静态本地方法，wait 则是 Object 类的本地方法

2、sleep方法不会释放lock，但是wait会释放，而且会加入到等待队列中

```java
sleep就是把cpu的执行资格和执行权释放出去，不在运行此线程，当定时时间结束再取回cpu资源，参与cpu的调度，获取到cpu资源后就可以继续运行。如果sleep是线程有锁，那么sleep不会释放这个锁，而是把锁带着进入了冻结状态，也就是说其他需要这个锁的线程根本不可能获取到这个锁，也就是说无法执行程序。如果在睡眠期间其他线程调用了这个线程的interrupt方法，那么这个线程也会抛出interruptexception异常返回，这点和wait是一样的。
```



3、sleep方法不依赖于同步器synchronized，但是wait需要以来synchronized关键字

4、sleep不需要被唤醒，但是wait需要（不指定时间时需要被别人中断）

5、sleep一般用于当前线程休眠，或者轮询暂停操作，wait 则多用于多线程之间的通信

6、sleep 会让出 cpu 执行时间且强制上下文切换，而 wait 则不一定， wait 后可能还是有机会重新竞争到锁继续执行的



yield() 执行后线程直接进入就绪状态，马上释放了cpu的执行权，但是依然保留了cpu的执行资格，所以有可能cpu下次进行线程调度还会让这个线程获取到执行权继续执行

join()执行后线程进入阻塞状态，例如在线程B中调用线程A的`join()`，那么线程B会进入到阻塞队列，直到线程A结束或者中断

```java
public static void main(String[] args){
  Thread t1 = new Thread(new Runnable(){
    @Override
    public void run(){
      try{
        Thread.sleep(3000);
      }catch(InterruptedException e){
        e.printStackTrace();
      }
      System.out.println("2222");
    }
  });
  t1.start();
  t1.join();
  // 这行代码必须等到t1全部执行完毕，才会执行
  System.out.println("1111");
}
```



# 20、线程安全

线程安全其实也是内存安全，堆是共享内存，可以被所有线程访问

## **堆**

堆是进程和线程共有的空间，分为局部堆和全局堆，全局堆就是所有没有分配的空间，局部堆就是用户分配的空间，堆在操作系统对进程初始化的时候分配，运行过程中也可以向系统要额外的堆，但是用完了要还给操作系统，不然就是**内存泄漏**

```java
在java中，堆是java虚拟机所管理的内存中最大的一块，是所有线程共享的一块内存区域，在虚拟机启动时创建，堆所存在的内存区域的唯一目的就是存放对象实例，几乎所有的对象实例以及数组都在这里分配内存
```

## 栈

栈是每个线程独有的，保存其运行状态和局部变量的，栈在线程开始的时候初始化，每个线程的栈相互独立，因此栈是**线程安全**的，操作系统在切换线程的时候会自动换栈。



每个进程的内存空间里的一块特殊的公共区域，通常称为堆（内存）



# 21、守护线程

jvm内的线程分为守护线程和用户线程（非守护线程）

守护线程：为所有的非守护线程提供服务；任何一个守护线程都是整个JVM中==所有==非守护线程的保姆

守护线程的死活无关重要，它依赖整个线程而运行；当其他线程结束了，没有要执行的了，程序就结束了，守护线程就没有存在的必要了。

例如：

GC垃圾回收线程就是一个经典的守护线程，当程序中不再有任何运行的Thread，程序就不会产生垃圾，因此垃圾回收器就无事可做，当垃圾回收线程是JVM上仅剩的线程时，垃圾回收线程会自动离开。



# 22、ThreadLocal

ThreadLocal用于线程间的数据隔离。

每一个`Thread`对象均含有一个`ThreadLocalMap`类型的成员变量`threadLocals`，它存储本线程所有的ThreadLocal对象及其对应的值

`ThreadLocalMap`由一个个的`Entry<key,value>`对象构成

Entry继承自`weakReference<ThreadLocal<?>>`，一个`Entry`由`ThreadLocal`对象和`Object`构成，由此可见，`Entry`的key是ThreadLocal对象，并且是一个弱引用。当没指向key的强引用后，该key就会被垃圾收集器回收

当执行set方法时，ThreadLocal首先会获取当前线程对象，然后获取当前线程的ThreadLocalMap对象，再以当前ThreadLocal对象为key，获取对应的value。

由于每一条线程均含有各自私有的ThreadLocalMap对象，这些容器相互独立互不影响，因此不会存在线程安全性问题，从而也就无需使用同步机制来保证多条线程访问容器的互斥性

使用场景

1、在进行对象跨层传递的时候，使用ThreadLocal可以避免多次传送，打破层次间的约束。

> 即如果一个User对象需要从Controller层传到Service层再传到Dao层，那么把User放在ThreadLocal中，每次使用ThreadLocal来进行获取即可

2、线程间数据隔离

3、进行事务操作，用于存储线程事务信息

4、数据库连接，Session会话管理



# 23、ThreadLocal引起的内存泄漏，如何避免

内存泄露：程序在申请内存后，无法释放已申请的内存空间，一次内存泄露的危害可以忽略，但是内存泄漏堆积后果很严重，无论多少内存，迟早会被占光

强引用：使用最普遍的一种引用（用new创建对象），一个对象有强引用，不会被垃圾回收器回收。当内存空间不足时，Java虚拟机宁愿抛出`OutOfMemoryError`错误，也不回收这种对象

如果想取消强引用和某个对象之间的关联，可以显式地将引用赋值为null，这样可以是JVM在合适的时间就会回收该对象

弱引用：JVM进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。在Java中，用`java.lang.ref.WeakReference`类来表示。可以在缓存中使用弱引用。



ThreadLocal的实现原理，每一个Thread维护一个ThreadLocalMap，key为使用**弱引用**的ThreadLocal实例，value为线程变量的副本



ThreadLocalMap使用ThreadLocal的弱引用为key，如果一个ThreadLocal不存在外部对它的 **强引用** 时，**Key（ThreadLocal）** 势必会被GC回收，这样就会导致ThreadLocalMap中key为null，而 **value** 还在引用线程变量，存在着 **强引用** ，只有thread线程退出以后，value的强引用链条才会断掉，但如果当前线程再迟迟不结束的话，这些key为null的Entry的value就会一直存在一条强引用链。

**总结一下：**

> ThreadLocalMap的key使用了弱引用，当ThreadLocal没有被ThreadLocalMap以外的对象引用的时候，在下一次GC的时候，ThreadLocal实例就会被回收，ThreadLocalMap中的ThreadLocal表示的key就是null了，此时value便不会被外部访问到，只要Thread实例一直存在，这里key为null的value值就一直占用着内存

**因此，ThreadLocal内存泄露的根源是：**

由于ThreadLocalMap的生命周期跟Thread一样长，如果没有手动删除对应key就会导致内存泄漏。

ThreadLocal正确的使用方法

- 每次使用完ThreadLocal都调用它的remove()方法清除数据
- 将ThreadLocal变量定义成private static，这样就一直存在ThreadLocal的强引用，也能保证任何时候都能通过ThreadLocal的弱引用访问到Entry的value值，进而清除掉



# 24、并发的三大特性

- 原子性

  在一个操作中cpu不可以在中途被中断

  ```
  java的i++操作就不是线程安全的，有4步：
  1：将i值从主存读到工作内存中的副本中（每个线程都有一个自己的工作内存）
  2：+1的运算
  3：将结果写入工作内存
  4：将工作内存的值刷回主存（什么时候刷入由操作系统决定，不确定）
  ```

- 可见性

  原子性保证了i++的1、2、3步操作不可分割，可见性保证了i++的3、4步操作不可分割

  ```
  如果一个线程运算完之后还没刷到主存，此时这个共享变量的值被另外一个线程从主存读取到了，这个时候读取的数据就是脏数据了，它会覆盖其他线程计算完的值。这就是经典的内存不可见问题。
  volatile能保证可见性
  ```

  **可以使用`java.util.concurrent.atomic.AtomicInteger`来保证i++线程安全，AtomicInteger使用volatile和CAS算法来保证**

- 有序性

  在多线程执行的时候，代码可能会进行指令重排 ，有可能出现线程安全问题。

  可以使用：volatile、synchronized

  volatile本身就包含了禁止指令重排的语义

  synchronized是由“一个变量在同一时刻只允许一条线程对其lock操作”这条规则明确的

- # ​,,25、为什么用线程池？解释线程池的参数？（参数留）

1、降低资源消耗；提高线程利用率，降低创建和销毁线程的消耗。

> 将创建好的线程放在线程池中，用的时候直接取

2、提高响应速度；拿来就可以用

3、提高线程的可管理性；线程是稀缺资源，使用线程池可以统一分配调优监控

线程池参数：

`corePoolSize` 核心线程数量

`maximumPoolSize` 线程池最大线程数量

`keepAliveTime` 空闲线程存活时间

`util`空闲线程存活时间单位（keepAliveTime单位）

`workQueue` 工作队列（任务队列）

`threadFactory` 线程工厂，创建一个新线程时使用的工厂

`handler` 拒绝策略 ，有4种（因为是为了找实习，所以暂时先不了解）

# 26、线程池处理流程

```
1-线程池执行任务（核心线程即线程池中的常驻线程）
2-如果核心线程未满，创建核心线程执行
3-如果核心线程已满，查看任务队列是否已满
4-如果任务队列未满，将任务放到任务队列
5-如果任务队列已满，看最大线程数是否达到
6-如果未达到最大线程数，则创建临时线程执行
7-如果达到最大线程数，根据拒绝策略处理
```



# 27、线程池中阻塞队列的作用？为什么先添加队列而不是先创建最大线程？

1、一般的队列只能保证作为一个有线长度的缓冲区，如果超出了缓冲长度，就无法保留当前的任务了，阻塞队列通过阻塞可以保留住当前想要继续入队的任务

阻塞队列可以保证任务队列中没有任务时阻塞获取任务的线程，使得线程进入wait状态，释放cpu资源

阻塞队列自带阻塞和唤醒功能，不需要额外处理，无任务执行时，线程池利用阻塞队列的take方法挂起，从而维持核心线程的存活，不至于一直占用cpu资源



2、为什么有任务的时候，当核心线程满了，要把任务先放到任务队列，而不是取创建临时线程？

因为创建新线程的时候，是要获取全局锁的，这个时候其他线程就得阻塞，影响了整体的效率。



# 28、线程池中线程复用原理

线程池将线程和任务进行解耦，线程是线程，任务是任务，摆脱了之前通过 Thread 创建线程时的一个线程必须对应一个任务的限制。

在线程池中，同一个线程可以从阻塞队列中不断获取新任务来执行，其核心原理在于线程池对 Thread 进行了封装，并不是每次执行任务都会调用 Thread.start() 来创建新线程，而是让每个线程取执行一个“循环任务”，在这个“循环任务”中不停检查是否有任务需要被执行，如果有则直接执行，也就是调用任务中的run方法，将 run 方法当成一个普通的方法执行，通过这种方式只使用固定的线程就将所有任务的 run 方法串联起来。

### 线程的start和run方法

start方法可以启动多线程

run方法只是thread的一个普通方法调用，还是在主线程里执行，是不会开启多线程的

# 29、Spring

## Spring框架介绍

是一个容器框架，用来装javabean(java对象)，是中间层框架。

Spring是一个轻量级的控制反转（IOC）和面向切面（AOP）的容器框架

- 大小与开销两方面，Spring都是轻量级的
- 通过控制反转IOC技术达到松耦合的目的
- 提供了面向切面变成的丰富支持
- 包含并管理应用对象（Bean）的配置和生命周期，这个意义上是一个容器
- 将简单的的组件配置、组合成为复杂的应用，这个意义上是一个框架



## AOP

当我们需要为分散的对象引入公共行为的时候（如打印日志，抛出异常），OOP则显的无能为力。也就是说，OOP允许你定义从上到下的关系，但不适合定义从左到右的关系。

AOP：将程序中的交叉业务逻辑（比如安全，日志，事务等）封装成一个切面，然后注入到目标对象（具体业务逻辑）中去。AOP可以对某个对象或者某些对象的功能进行增强，比如对象中的方法进行增强，可以在执行某个方法之前额外的做一些事情，在某个方法执行之后额外做一些事情



## IOC

容器概念、控制反转、依赖注入

- IOC容器

  实际上就是个map（key，value），里面存的是各种对象（在xml里配置的bean节点，@Repository，@Service，@Controller，@Component），在项目启动的时候会读取配置文件里面的bean节点，根据全限定类名使用反射创建对象放到map里、扫描到打上上述注解的类还是通过反射创建对象放到map里

  这个时候map里就有各种对象了，当用到哪个对象了，再通过DI注入（@Autowired、@Resource等注解，xml里bean节点的ref属性，项目启动的时候会读取xml节点ref属性根据id注入，也会扫描这些注解，根据类型或id注入；id就是对象名）

- 控制反转

  没有引入IOC容器之前，对象A若要使用对象B，对象A需要自己去创建或者使用已经创建的对象B，控制权都是在对象A手中

  引入IOC容器后，对象A和对象B之间失去了直接联系，当对象A运行到需要使用对象B的地方的时候，IOC容器会主动创建一个对象B注入到A需要的地方。

  通过对比，对象A使用对象B由主动行为变成了被动行为，控制权颠倒了，这就是“控制反转”这个名称由来

- 依赖注入

  获得依赖对象的过程被反转了，获得依赖对象的过程是由自身管理变为了由IOC容器主动注入。

  依赖注入是实现IOC的方法，就是由IOC容器在运行期间，动态地将某种依赖关系注入到对象之中



## BeanFactory和ApplicationContext的区别

都是Spring的bean容器

ApplicationContext是BeanFactory的子接口，提供了更为完善的功能。



- BeanFactory使用延迟加载来注入Bean，只有使用到了某个Bean（调用getBean方法）才对该Bean进行加载实例化。这样，如果Spring的配置有问题，只有到调用getBean方法才会抛出异常
- ApplicationContext在容器启动后预载入所有的单实例Bean，所以启动较慢。
- 还有其他点，暂留

# 30、Spring Bean的生命周期（还未学，留）



# 31、Spring支持的几种bean的作用域

- **singleton**：默认，每个容器中只有一个bean的实例（可能由多个bean容器），单例的模式由BeanFactory自身来维护。该对象的生命周期与Spring IOC容器一直（但在第一次被注入时才会创建）
- **prototype**：为每一个bean请求（getbean）提供一个实例。在每次注入时都会创建一个新对象
- request：每个HTTP请求创建一个**单例**对象。即在单个请求中都会复用这个bean对象
- session：与request范围类似，确保每个session有一个bean的实例。
- application：在ServletContext的生命周期中复用一个单例对象
- websocket：bean被定义为在websocket的生命周期中服用一个单例对象





# 32、Spring框架中的单例Bean是线程安全的么？

Spring中的Bean默认是单例模式的，框架并没有对bean进行多线程的封装处理。

如果Bean是有状态的，那就需要开发人员自己来进行线程安全的保证，最简单的就是改变bean的作用域，把`singleton`改为`prototype`，这样每次请求Bean都相当于是new Bean()，可以保证线程安全

- 有状态就是有数据存储功能
- 无状态就是不会保存数据  

Dao会操作数据库Connection，Connection是带有状态的，比如说数据库事务，Spring的事务管理器使用ThreadLocal为不同线程维护了一套独立的connection副本，保证线程之间不会互相影响



# 33、Spring中的设计模式（留）

# 34、Spring事务（留）

# 35、Spring事务传播机制（留）

# 36、Spring事务什么时候会失效？（留）



# 37、什么是bean的自动装配，有哪些方式?

开启自动装配，只需要在xml配置文件<bean>中定义"autowire"属性

```java
<bean id="customer" class="com.xxx.xxx.Customer" autowire="" />
```

autowire属性有五种装配的方式：

- no - 缺省情况下，自动配置是通过“ref”属性手动设定

  ```
  手动装配：以value或ref的方式明确指出属性值的都是手动装配
  需要通过‘ref’属性来链接bean
  ```

- byName - 根据bean的属性名称进行自动装配

  ```
  假设Customer的一个属性名称是person. Spring会将bean id为person的bean通过setter方法进行自动装配。
  <bean id="customer" class="com.xxx.xxx.Customer" autowire="byName" />
  <bean id="person" class="com.xxx.xxx.Person" />
  ```

- byType - 根据bean的类型进行自动装配

  ```
  假设Customer的一个属性person的类型为Person，Spring会将Person类型通过setter方法进行自动装配。
  <bean id="customer" class="com.xxx.xxx.Customer" autowire="byType" />
  <bean id="person" class="com.xxx.xxx.Person" />
  ```

- constructor - 类似byType，不过是应用于构造器的参数。如果一个bean与构造器参数的类型相同，则进行自动装配，否则导致异常

  ```
  Customer构造函数的参数person的类型为Person，Spring会将Person类型通过构造方法进行自动装配
  <bean id="customer" class="com.xxx.xxx.Customer" autowire="constructor" />
  <bean id="person" class="com.xxx.xxx.Person" />
  ```

# 38、SpringBoot、SpringMVC和Spring有什么区别



Spring是一个IOC容器，用于管理bean，使用依赖注入实现控制反转，可以很方便的整合各种框架，提供AOP机制弥补OOP（Object Oriented Programming）的代码重复问题，更方便将不同类不同方法中的共同处理抽取成切面，自动注入给方法执行，比如日志、异常等等。

SpringMvc是Spring对web框架的一个解决方案，提供了一个总的前端控制器Servlet，用来接受请求，然后定义了一套路由策略（url到handle的映射）及适配执行handle，将handle结果使用视图解析技术生成视图展现给前端

springboot是spring提供的一个快速开发工具包，让程序员能更方便、更快速的开发spring+springmvc应用，简化了配置（约定了默认配置）。



# 39、SpringMVC工作流程

1、用户发送请求至前端控制器 DispatcherServlet

2、DispatcherServlet 收到请求调用 HandlerMapping处理器映射器（维护url到handler的映射），去寻找handler（也就是对应的controller）

3、处理器映射器找到具体的处理器（可以根据xml配置、注解进行查找），生成处理器执行链（如果有则生成）一并返回给 DispatcherServlet

4、DispatcherServlet需要去执行Handler，因为handler实现方式不同，所以DispatcherServlet需要通过处理器适配器去执行handler

> 因为handler有多种实现形式

5、 执行完成handler后返回 ModelAndView给DispatcherServlet

6、DispathcherServlet将 ModelAndView 传给 ViewResolver 视图解析器

7、ViewResolver 解析后返回具体 View

8、DispathcerServlet 根据 View 进行渲染视图（即将模型数据填充至视图中）

9、DispatcherServlet 响应用户

# 40、Spring MVC的主要组件（九大组件）

Handler：也就是处理器，并不是组件。它直接应对着MVC中的C也就是Controller层，它的具体表现形式有很多，可以是类，也可以是方法。在Controller层中`@RequestMapper`标注的所有方法都可以看作一个handler，只要可以实际处理请求就可以是Handler

**1、HandlerMapping（是一个接口）**

initHandlerMappings(context)，即处理器映射器，根据用户请求的资源uri来查找Handler的。在SpringMVC中会有很多请求，每个请求都需要一个Handler处理，具体接收到一个请求之后使用哪个Handler进行，这就是HandlerMapping需要做的事。

**2、HandlerAdapter（是一个接口）**

initHandlerAdapters(context)，即处理器适配器。因为SpringMVC中的Handler可以是任意的形式，只要能处理请求就ok，但是Servlet需要的处理方法的结构确实固定的，都是以request和response为参数的方法。那么就可以通过`HandlerAdapter` 来让固定的 `Servlet` 处理方法调用灵活的 `Handler` 来进行处理。

Handler 是用来干活的工具；HandlerMapping 用于根据需要干的活找到相应的工具；HandlerAdapter 是使用工具干活的人

其他七个组件略。



# 41、Spring Boot 自动配置的原理

自动配置：根据我们添加的jar包依赖，会自动将一些配置类的bean注册进ioc容器，我们可以在需要的地方使用@autowired或者@resource等注解来使用。



Spring Boot 应用的启动入口是 `@SpringBootApplication` 注解标注类中的main()方法

`@SpringBootApplication` ：用于表示SpringBoot的主配置类

### `@SpringBootApplication` 

````java
@SpringBootConfiguration //标志该类为配置类
@EnableAutoConfiguration // 启动自动配置
@ComponentScan
和其他一些元注解
public @interface SpringBootApplication{}
````

#### `@SpringBootConfiguration`

```java
@Configuration // 其实SpringBootConfiguration就是对Spring提供的注解@Configuration进行了一个包装
public @interface SpringBootConfiguration{}
```

#### @EnableAutoConfiguration

**Spring中有很多以`Enable`开头的注解，其作用就是借助`@Import`来收集并注册特定场景相关的`Bean`，并加载到IOC容器**

```java
// 自动配置包
@AutoConfigurationPackage

// Spring的底层注解@Import，给容器中导入一个组件
// 导入的组件是AutoConfigurationPackages.Registrar.class
@Import(AutoConfigurationImportSelector.class)

// 告诉SpringBoot 开启自动配置功能，这样自动配置才能生效
public @interface EnableAutoConfiguration{
  String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautuconfiguration";
  
  // 返回不会被导入到 Spring 容器中的类
  Class<?>[] exclude() default {};
  
  // 返回不会被导入到 Spring 容器中的类名
  String[] excludeName() default {};
}
```

##### @AutoConfigurationPackage

@AutoConfigurationPackage：自动配置包，也是一个组合注解，最主要的注解是`@Import(AutuConfigurationPackages.Registrar.class)`，它是`Spring`框架的低层注解，它的作用就是给容器中导入某个组件类，例如`@Import(AutoConfigurationPackages.Registrar.class)`，他就是将这个组件类导入到容器中，可查看`Registrar`类中的`registerBeanDefinition`方法：

```java
@Override
public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
  // 将注解标注的原信息传入，获取到相应的包名
  register(registry, new PackageImport(metadata).getPackageName());
  // new PackageImport(metadata).getPackageName()计算出来的结果就是主启动类的包名
}
```

再来看`register`方法

```java
public static void register(BeanDefinitionRegistry registry, String... packageNames) {
  // 这里参数packageNames缺省情况下就是一个字符串，是使用了注解
  // @SpringBootApplication 的SpringBoot 应用程序入口类所在的包
  
  if(registry.containsBeanDefinition(BEAN)) {
    // 如果该Bean已经注册，则把要注册的包名称添加进去
    BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
    ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
    constructorArguments.addIndexedArgumentValue(0, addBasePackages(constructorArguments, packageNames));
  }else {
    // 如果bean尚未注册，则注册该bean，参数中提供的包名称会被设置到bean定义中去
    GenericBeanDefinition beanDefinition = new GenericBeanDefinition(); //bean对象的一个建模对象
    beanDefinition.setBeanClass(BasePackages.class); 
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, packageNames);//设置包名以便后续使用
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registr.registerBeanDefinition(BEAN, beanDefinition);//完成了bean对象的注册
  }
}
```



> AutoConfigurationPackages.Registrar这个类就就干了一件事，注册了一个Bean，这个Bean就是`org.springframework.boot.autoconfigure.AutoConfigurationPackages.BasePackages，`，==作用就是保存包路径==，他有一个参数，这个参数即为使用了`@AutoConfigurationPackage`这个注解的类所在的包路径，保存自动配置类以供之后的使用，比如给`JPA entity`扫描器用来扫描开发人员通过注解`@Entity`定义的entity类。



##### @Import(AutoConfigurationImportSelector.class)（自动装配核心）

@Import是Spring的底层注解，用于给容器中导入一个组件

`AutoConfigurationImportSelector`可以帮助`SpringBoot`应用将所有符合条件的`@Configuration`配置都加载到当前`SpringBoot`创建并使用的`IOC`容器`ApplicationContext`中



SpringBoot自动装配主要做了以下事情：

1、从spring.factories（META-INF/spring.factories）配置文件中加载自动配置类

2、加载的自动配置类中排除掉`@EnableAutoConfiguration` 注解的`exclude`属性指定的自动配置类

3、然后再用`AutoConfigurationImportFilter`接口去过滤自动配置类是否符合其标注注解`@ConditionalOncClass`，`@ConditionalOnBean`和`@ConditionalOnWebApplication`的条件，若都符合的话则返回匹配结果

4、然后触发`AutoConfigurationImportEvent`时间，告诉`ConditionEvaluationReport`条件评估报告器对象来分别记录符合条件和`exclude`的自动配置类

5、最后spring再将筛选后的自动配置类导入IOC容器

#### @ComponentScan

主要是从定义的扫描路径中，找出标识了需要装配的类自动装配到spring的bean容器中

常用属性：

- basePackages、value：指定扫描路径，如果为空则以@ComponentScan注解的类所在的包为基本的扫描路径
- basePackageClasses：指定具体扫描的类
- includeFilters：指定满足Filter条件的类
- excludeFilters：指定排除Filter条件的类

includeFilters和excludeFilters 的FilterType可选：ANNOTATION=注解类型 默认、ASSIGNABLE_TYPE（指定固定类）、ASPECTJ（ASPECTJ类型）、REGEX（正则表达式）、CUSTOM（自定义类型）、自定义的Filter需要实现TypeFilter接口



@ComponentScan的配置如下

```java
@ComponentScan(excludeFilters = {@Filter(type - FilterType.CUSTOM, classes = TypeExcludeFilter.class), @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
```

接触excludeFilters将TypeExcludeFilter及FilterType这两个类排除



# 42、如何理解Spring Boot中的starter

starter就是定义一个starter的jar包，在jar包中写一个`@Configuration`配置类、将需要注入的bean定义在里面，但是只定义了配置类，如何去让springboot加载这个配置类呢？只需要在starter包的META-INF/spring.factories中写入该配置类的全路径，springboot会按照约定来扫描spring.factories文件，来将这个配置类加载到ioc容器。



# 43、什么是嵌入式服务其？

没有Spring Boot之前，在开发应用时，需要下载tomcat进行单独部署，把项目部署到tomcat里面去。

有了SpringBoot之后，tomcat只是一个jar包，即tomcat.jar

为什么要使用嵌入式服务？

节省了下载tomcat，应用也不需要再打war包，然后放到webapp目录下运行

只需要安装一个 Java 的虚拟机，就可以直接在上面部署应用程序了

springboot已经内置了tomcat.jar，运行main方法时会去启动tomcat，并利用tomcat的spi机制加载springmvc



# 44、MyBatis的优缺点

优点：

1、基于SQL变成，比较灵活，SQL写在XML文件里，解除 sql 与程序代码的耦合；提供 XML 标签，支持编写动态 SQL语句

2、与 JDBC 相比，消除了大量冗余代码，不需要手动开关链接

3、很好的与各种数据库兼容（因为 MyBatis 使用 JDBC 来连接数据库，所以只要 JDBC 支持的数据库MyBatis都支持）

4、能很好与Spring集成

5、提供映射标签，支持对象与数据库的 ORM 字段关系映射，提供对象关系映射标签，支持对象关系组件维护

缺点

1、SQL语句编写工作量大

2、SQL语句依赖于数据库（Oracle、MySQL），导致数据库移植性差



# 45、#{}和${}的区别

`#{}`是预编译处理，是占位符

MyBatis在处理 `#{}` 时，会将 sql 中的 `#{}` 替换为 `?`号，调用 PreparedStatement 来赋值

`#{}`的变量替换是在 DBMS 中、变量替换后，#{} 对应的变量自动加上单引号，可以有效防止 **SQL 注入** ， 提高系统安全性



`${}`是字符串替换、是拼接符

MyBatis在处理 `${}` 时，就是把 `${}` 替换成变量的值，调用 Statement 来赋值

`${}`的变量替换是在 DBMS 外、变量替换后，`${}` 对应的变量不会加上单引号



# 46、简述MyBatis插件的运行原理，如何编写一个插件？

**MyBatis的插件其实也就是指MyBatis的拦截器**

MyBatis四大接口，即MyBatis只能够对这四大对象进行拦截：

- Executor是 Mybatis的内部执行器，它负责调用StatementHandler操作数据库，并把结果集通过 ResultSetHandler进行自动映射，另外，他还处理了二级缓存的操作。从这里可以看出，我们也是可以通过插件来实现自定义的二级缓存的。
- StatementHandler是Mybatis直接和数据库执行sql脚本的对象。另外它也实现了Mybatis的一级缓存。这里，我们可以使用插件来实现对一级缓存的操作(禁用等等)。
- ParameterHandler是Mybatis实现Sql入参设置的对象。插件可以改变我们Sql的参数默认设置。
- ResultSetHandler是Mybatis把ResultSet集合映射成POJO的接口对象。我们可以定义插件对Mybatis的结果集自动映射进行修改。

[^版权声明：段落摘自CSDN博主「闫二白」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。]: 

MyBatis 使用 JDK 的动态代理，为需要拦截的接口生成代理对象以实现接口方法拦截功能，每当执行这4中接口对象的方法时，就会进入拦截方法，具体就是 InvocationHandler 的 invoke() 方法，拦截需要拦截的方法。



编写插件：实现 MyBatis 的 Interceptor 接口并复写 intercept() 方法，然后再给插件编写注解，指定要拦截哪个接口的哪些方法即可，在配置文件中配置编写的插件。

```java
代码先略
```

# 47、数据库索引的基本原理

索引用来快速寻找具有特定值的记录。

索引的原理：就是把无序的数据变成有序的查询

1、把创建了索引的列的内容进行排序

2、对排序结果生成倒排表（倒排表也就是对数据进行哈希（也不一定非要哈希，倒排表还可以放B树的id（听老师讲的）），将哈希值放在一起的表也就叫倒排表）

3、将倒排表的内容拼接上数据的地址链

4、这样，在查询时，找到了倒排表内容，再取出来数据地址链，就可以取出数据了



# 48、MySQL聚簇和非聚簇索引的区别

聚簇和非聚簇都是以B+树的数据结构为前提的

- 聚簇索引：将数据存储与索引放到了一块，并且是按照一定的顺序组织的，找到的索引也就找到了数据，数据的物理存放顺序与索引顺序是一致的，即：只要索引相邻，那么对应的数据也一定相邻存放在磁盘上
- 非聚簇索引：叶子节点不存储数据，存储的是数据行地址，也就是说根据索引找到数据行的位置，再去磁盘查找数据。

**聚簇索引优点：**
1、聚簇所以可以直接获取数据，相比于非聚簇索引需要第二次查询（非覆盖索引的情况下）效率更高。

```
覆盖索引：即数据与索引一致，如查找用户的id，id既作为了索引又作为了数据，这就是覆盖索引。
覆盖索引只需要查询一次。
```

2、聚簇索引对范围查询的效率高，因为数据是按照大小排序

3、聚簇索引适用于排序的场合，非聚簇索引不适合

**聚簇索引缺点：**

1、维护索引很昂贵，因为索引要排序，所以大量插入行后维护索引开销比较大。

2、表因为使用UUID（随机ID）作为主键（去磁盘读数据，一次读一页，索引分散的话会导致空间局部性不好），使得数据存储稀疏，这就会出现聚簇索引有可能比全表扫描更慢的情况，所以建议使用int的auto_increment作为主键

3、如果主键比较大的话，那辅助索引（辅助索引基于聚簇索引创建）将会变得更大，因为辅助索引的叶子存储的是主键值；过长的主键值，会导致叶子节点占用更多的物理空间



InnoDB与MyISAM在索引上的区别：（还有其他区别）



-  InnoDB使用的是聚集索引，使用B+Tree作为索引结构，数据文件是和（主键）索引绑在一起的（表数据文件本身就是按B+Tree组织的一个索引结构），必须要有主键，通过主键索引效率很高。但是辅助索引需要两次查询，先查询到主键，然后再通过主键查询到数据。因此，主键不应该过大，因为主键太大，其他索引也都会很大。


- MyISAM使用的是非聚集索引，也是使用B+Tree作为索引结构，索引和数据文件是分离的，索引保存的是数据文件的指针。主键索引和辅助索引是独立的。也就是说：InnoDB的B+树主键索引的叶子节点就是数据文件，辅助索引的叶子节点是主键的值；而MyISAM的B+树主键索引和辅助索引的叶子节点都是数据文件的地址指针。

大量数据排序、全表扫描，MyISAM较为占优势，索引占用内存较少，InnoDB的索引加数据更加占内存空间。 