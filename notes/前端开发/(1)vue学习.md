# 1、认识Vuejs

Vue (读音 /vjuː/，类似于 **view**) 是一套用于构建用户界面的**渐进式框架**。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。Vue 的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。



# 2、Vuejs的安装

- 下载和引入：

开发环境： https://vuejs.org/js/vue.js

生产环境： https://vuejs.org/js/vue.min.js

- npm安装

# 3、HelloWorld



```html
<body>

    <div id="app">{{msg}}</div>

    
    <script src="./vue.min.js"></script>
    <script>
      	// 编程范式:声明式编程
        // 声明变量 let/const(常量)
        const app = new Vue({
            el: '#app',//挂载需要管理的元素
            data: {
                msg: 'Hello World！'
            }
        });
        </script>
</body>
```

代码做了什么事情？

- 创建Vue对象的时候，传入了一些参数，参数包括el属性和data属性
- 传入的el属性决定我我们要将创建的Vue对象挂载到哪一个元素上，很明显我们挂载到了id为app的元素 上
- 传入的data属性中通常会存储一些数据
  - 这些数据可以使我们自定义的
  - 也可能是来自网络，从服务器加载的

# 4、数组数据的列表展示

```html
<body>

    <div id="app">
        <ul>
            <!-- 使用v-for遍历出数组中的元素 -->
            <li v-for="item in movies">{{item}}</li>
        </ul>
    </div>

    <script src="./vue.min.js"></script>
    <script>
        const app = new Vue({
            el: '#app',
            data: {
                movies: ['大话西游','唐人街探案','调音师']
            }
        })
    </script>
</body>
```

# 5、Vue中的MVVM

什么是MVVM呢？（Model View Viewmodel）

- 通常我们学习一个概念，最好的方式是去查看==维基百科==（不是百度百科）

  https://www.jianshu.com/p/0ae3c0d830e5



- View层：
  - 视图层，在前端开发中，通常就是DOM层
  - 主要的作用是给用户展示各种信息
- Model层：
  - 数据层
  - 数据可能是我们固定的死数据，更多的是来自我们的服务器，从网络上请求下来的数据
- Viewmodel层
  - 视图模型层
  - 视图模型层是View和Model沟通的桥梁
  - 一方面它实现了Data Binding，也就是==数据绑定==，将Model的改变实时的反映到了View中
  - 另一方面它实现了DOM Listener，也就是DOM监听，当DOM发生一些事件时，可以监听到，并在需要的情况下改变对应的Data。



# 6、方法和函数

- 方法：method

  定义在类里

- 函数：function

  定义在类外

  ```html
  function f1(){

  }
  ```

  ​

# 7、插值操作

mustache语法：也就是双大括号`{{}}`

mustache：胡须

代码演示

```html
<body>
    
<div id="app">
  <h2>{{message}}</h2>
  <!-- mustache语法不仅可以直接写变量，也可以写一些简单的表达式 -->
  <h2>{{firstName + ' ' + lastName}}</h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      message: 'Hello World!',
      firstName: 'kobe',
      lastName: 'bryant'
    }
  })
</script>
</body>
```

# 8、vue中的插值指令

### v-once

- 该指令后面不需要跟任何表达式
- 该指令表示元素和组件只渲染一次，不会随着数据的改变而改变

`当我们在控制台改变message的值，有v-once的不会改变！`

```html
<div id="app">
  <h2>{{message}}</h2>
  <h2 v-once>{{message}}</h2>
</div>
```

### v-html

- 某些情况下，我们从服务器请求到的数据本身就是一个HTML代码
  - 如果我们直接通知{{}}来输出，会将HTML代码也一起输出
  - 但是我们可能希望的是按照HTML格式进行解析，并且显示其内容
- 如果我们希望解析出HTML进行展示
  - 可以使用v-html指令
    - 该指令后面往往会跟上一个string类型
    - 会将string类型的html解析出来并进行渲染



```html
<div id="app">
  <h2>{{url}}</h2>
  <h2 v-html="url"></h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      url: '<a href="https://www.baidu.com">百度一下</a>'
    }
  })
</script>
</body>
```

### v-pre

- 用于跳过这个元素和它子元素的编译过程，用于显示原本的Mustache语法

第一行代码会解析出url的值

第二行代码会显示： `{{url}}`

```html
<div id="app">
  <h2>{{url}}</h2>
  <h2 v-pre>{{url}}</h2>
</div>
```



# 9、v-bind

- 前面学习的指令的主要作用是将值插入到我们的模板内容当中
- 但是，除了内容需要动态来决定外，某些标签的属性我们也希望动态来绑定 
  - 比如 ，动态绑定a元素的href属性
  - 比如，动态绑定img元素的src属性
- 这个时候，我们可以使用v-bind指令
  - 作用：动态绑定属性
  - 缩写： `：`（一个冒号）

```html
<div id="app">
  <!-- v-bind绑定data中的值到元素的属性中 -->
  <img v-bind:src="imgUrl" alt="">
  <!-- :是v-bind的缩写形式 -->
  <img :src="imgUrl" alt="">
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      imgUrl: 'https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png'
    }
  })
</script>
</body>
```

### v-bind动态绑定class（对象语法）

使用 v-bind绑定class，用true或false来决定class是否显示。

```html
<h2 :class="{active: true,line: false}">{{msg}}</h2>
```



```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<style>
  .active{
    color: red;
  }
</style>
<body>
    
<div id="app">
  <!-- 方式一：使用v-bind绑定class属性，对象类型{keu1:value1,key2:value2} ，key存放样式，value存放布尔类型值，来决定是否显示 -->
  <h2 :class="{active: true,line: false}">{{msg}}</h2>
  <!-- 不过我们一般不直接写true或者false -->
  <h2 :class="{active: isActive,line: isLine}">{{msg}}</h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      msg: "Hello World!",
      isActive: false,
      isLine: true
    }
  })
</script>
</body>
</html>
```

### v-bind动态绑定style（对象语法）

对象语法就是：{key:value,key:value}，以key-value的形式去写style样式

也可以使用方法来获取style

```html
<div id="app">
  <!-- 50px必须加上单引号，否则会当做变量解析 -->
  <h2 :style="{fontSize: '50px'}">Hello World !</h2>
  <!-- finalSize被当做变量解析，后边需要拼接上px -->
  <h2 :style="{fontSize: finalSize+'px'}">Hello World !</h2>
  <!-- 绑定方法来获取style样式 -->
  <h2 :style="getStyles()">Hello World !</h2>

</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      finalSize: 20,
      finalColor: 'red'
    },
    methods: {
      getStyles: function(){
        return {fontSize: this.finalSize + 'px',backgroundColor: this.finalColor}
      }
    }
  })
</script>
</body>
```

# 10、计算属性的基本使用

计算属性写在computed中

> 计算属性的需求

如果我们在页面展示的数据需要使用mustache语法进行拼接，如果每次都进行拼接会很麻烦，所以引出了计算属性，在需要使用的时候直接用mustache语法进行调用即可。

```html
<div id="app">
  <!-- 使用mustache语法来拼接 -->
  <h2>{{firstName + ' ' + lastName}}</h2>
  <!-- 使用计算属性 -->
  <h2>{{fullName}}</h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      firstName: 'Kobe',
      lastName: 'Byrant'
    },
    computed: {
      fullName: function(){
        return this.firstName + ' ' + this.lastName;
      }
    },
    methods: {
    }
  })
</script>
</body>
```



### 计算属性复杂使用

假如有数据，一个数组，数组有许多对象，我们要计算数组中所有书的总价格，如果直接使用mustache语法是不可能实现的，所以使用到了计算属性。

```html
<body>
    
<div id="app">
  <h2>书的总价格：{{totalPrice}}</h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      books: [
        {id: '100' , name: '编译原理' , price: 106},
        {id: '101' , name: '计算机组成原理' , price: 86},
        {id: '102' , name: '数据结构' , price: 76},
        {id: '103' , name: '算法4' , price: 89}
      ]
    },
    computed: {
      // 使用计算属性计算出书的总价哥
      totalPrice: function(){
        let result = 0;
        for(let i = 0 ; i < this.books.length ; i++)
        {
          result += this.books[i].price;
        }
        return result;
      }
    },
    methods: {
    }
  })
</script>
</body>
```



### 计算属性和method对比

- 计算属性带有缓存，多次调用的时候，如果值没有发生改变，就直接从缓存中取出，不需要多次执行
- method没有缓存，调用一次，执行一次，所以效率不如计算属性！



```html
<div id="app">
  <!-- 在这里调用了两次计算属性，但是实际上只执行了一次 -->
  <h2>{{fullName}}</h2>
  <h2>{{fullName}}</h2>
  <!-- 在这里调用了两次方法，方法执行了两次 -->
  <h2>{{getFullName()}}</h2>
  <h2>{{getFullName()}}</h2>
</div>

<script src="./vue.min.js"></script>
<script>
  const app = new Vue({
    el: "#app",
    data: {
      firstName: 'Kobe',
      lastName: 'Bryant'
    },
    computed: {
      fullName: function(){
        console.log('调用了计算属性');
        return this.firstName + ' ' + this.lastName;
      }
    },
    methods: {
      getFullName: function(){
        console.log('调用了method');
        return this.firstName + ' ' + this.lastName;
      }
    }
  })
</script>
</body>
```

# 11、ES6语法

### let/var

- 事实上var的设计可以看成JavaScript语言设计上的错误，但是这种错误多半不能修复和移除，以为需要向后兼容
- 我们可以将let看成更完美的var

> 块级作用域

- JS中使用var来声明一个变量时，变量的作用域主要是和函数的定义有关
- 针对于其他块定义来说是没有作用域的，比如if/for等，这往往会引起一些问题



var对于for循环没有块级作用域：

`也就是说在for循环里，使用var定义的变量也可以在 for循环外部取到！`

下面是完整的代码演示：

点击任何按钮的时候会发现控制台打印的：我是按钮：5

那么为什么i会为5呢？因为for循环进行完成之后，i++直到i为5为止，此时再来调用console.log就会调用已经成为了5的i。

如果使用let定义i的话，就不会出现这种情况了。

```html
<body>
    
  <button>按钮一</button>
  <button>按钮二</button>
  <button>按钮三</button>
  <button>按钮四</button>
  <button>按钮五</button>

<script src="./vue.min.js"></script>
<script>
  // 通过标签名取出所有的button
  var x = document.getElementsByTagName('button');
  for(var i = 0 ; i < x.length ; i++)
  {
    // 遍历所有的button，给每个button添加点击事件
    x[i].addEventListener('click',function(){
      console.log('我是按钮：'+i);
    })
  }
</script>
```

### const

使用 const修饰的标识符为常量，不可以再次赋值

- 什么时候使用const呢？
  - 当我们修饰的标识符不会再次赋值时，就可以使用const来保证数据 的 安全性

```html
const a = 20;
a = 30; //错误。不可以修改

/****/
const name;//错误。const修饰的标识符必须赋值

/****/
//常量的含义是指向的对象不能修改，但是可以改变对象内部的属性
const person = {
	name: 'Lucy',
	age: 20
}
person.name =  'Tom';
person.age = 18; // 可以修改对象内部的属性
```

**建议：在ES6开发中，优先使用const，只有需要改变某一个标识符的时候才使用let**



### 对象字面量增强写法



#### 属性的增强写法

- 如果我们想要将声明的常量放进一个对象中，该如何做呢？

ES5中

```html
const name = 'Tom';
const age = 18;
const address = '北京';
const obj = {
  name: name,
  age: age,
  address: address
}
```

ES6中

```html
const name = 'Tom';
const age = 18;
const address = '北京';
const obj = {
  name,
  age,
  address
}
```

#### 函数的增强写法

ES5中定义函数

```html
const obj = {
  name: name,
  age: age,
  address: address,
  run: function(){
  },
  eat: function(){
  }
}
```

ES6中定义函数

可以直接使用:   `[函数名](){}`进行定义

```html
const obj = {
  name: name,
  age: age,
  address: address,
  run(){
  },
  eat(){
  }
}
```

# 12、v-on

- v-on介绍
  - 作用：绑定事件监听器
  - 缩写： @

`v-on:click`的语法糖：`@click`

语法糖也就是简写形式

>  ==事件监听调用方法的时候什么时候加括号什么时候不加？==

- 情况一：如果该方法不需要额外的参数，那么方法后的()可以不添加
  - 但是注意：如果方法本身中有一个参数，那么会默认将原生事件event参数传递进去
- 如果使用mustache语法（双大括号）调用方法，方法后的()必须添加
- 情况二：如果需要同时传入某个参数时，同时需要event时，可以通过`$event`传入事件

v-on的基本使用：

```html
<div id="app">
  <h2>当前数字：{{counter}}</h2>
  <button @click="increment">+</button>
  <button @click="decrement">-</button>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
      counter: 0
    },
    methods: {
      increment(){
        this.counter++;
      },
      decrement(){
        this.counter--;
      }
    }
  })  
</script>
```



#### 如果需要event对象

如果函数定义了传入的参数，在使用v-on进行调用的时候

- 如果使用`@click="function()"`，不传入参数，则默认的参数为undefined
- 如果使用`@click="function"`，不写括号，则默认将浏览器产生的event事件对象作为参数传入到方法中

代码演示：

```html
<div id="app">
  <button @click="btn1">按钮1</button><!--我是按钮1 -->
  <button @click="btn2">按钮2</button><!--undefined -->
  <button @click="btn2()">按钮3</button><!--MouseEvent {isTrusted: true...} -->
  <button>按钮4</button>
  <button>按钮5</button>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
    },
    methods: {
      btn1(){
        console.log("我是按钮1");
      },
      btn2(event){
        console.log(event);
      }
    }
  })  
</script>
</body>
```

### 如果需要event对象和参数

可以使用`$event`来手动的获取到浏览器参数的event对象

```html
<button @click="function1('张三',$event)"></button>
方法定义：
btn3(param , event){
  console.log('参数为：'+param);
  console.log('事件为：'+event);
}
```

### v-on的修饰符使用



- 在某些情况下，我们拿到event的目的可能是进行一些事件处理
- Vue提供了修饰符来帮助我们方便的处理一些事件：
  - .stop -调用 event.st opPropagation()
  - .prevent -调用event.preventDefault()
  - .{keyCode | keytAlias}- 只当事件是从特定键触发时才触发回调
  - .native - 监听组件根元素的原生时间
  - .once - 只触发一次回调



#### .stop修饰符的使用

需求：

有一个div里面包含了一个按钮button，div和button都有点击事件，但是当我们点击button的时候，div的点击事件也会触发，也就是冒泡，我们想要阻止这种情况的出现，也就是当我们点击button的时候，div的点击事件不会触发，这事就就可以使用到了stop修饰符。

```html
<div id="app">
  <div @click="divclick">
    div中的内容 
    <!--在这里加上.stop即可！！-->
    <button @click.stop="btnclick">按钮</button>
  </div>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
    },
    methods: {
      divclick(){
        console.log("divclick");
      },
      btnclick(){
        console.log("btnclick");
      }
    }
  })  
</script>
```

#### .prevent修饰符的使用

需求：

在提交表单的时候，通常会有一个提交按钮，当点击提交按钮的时候，会提交表单中所有的数据，我们如果想要通过点击提交按钮来使用我们自己的方式进行提交，而不让浏览器自动地提交，就要在click后边加上.prevent修饰符。

如果不加上prevent修饰符的话，点击提交按钮之后，会执行onclick事件，浏览器同时也会自动进行提交。

`prevent用于阻止浏览器自动地进行提交`

代码演示

```html
<div id="app">
  <form action="baidu">
    <!-- 我们希望浏览器不会自动提交，而只执行我们的onclick事件进行提交 -->
    <button type="submit" @click.prevent="btnSub">提交</button>
  </form>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
    },
    methods: {
      btnSub() {
        console.log('btnSub');
      }
    }
  })  
</script>
```



#### 监听某个键盘的键帽的点击



```html
<!-- @keyUp用于监听键盘事件,加上.enter之后用于监听enter键 -->
<input type="text" @keyUp.enter="keyUp"></input>
```



# 13、v-if

v-if代码演示

通过一个数据 isShow来决定是否显示

```html
<div id="app">
  <h2 v-if="isShow">isShow为true的时候，显示我</h2>
  <h2 v-if="!isShow">isShow为false的时候，显示我</h2>
</div>s
```

v-if和v-else代码演示

```html
<div id="app">
  <h2 v-if="isShow">isShow为true的时候，显示我</h2>
  <h2 v-else>isShow为false的时候，显示我</h2>
</div>
```

# 14、v-show

v-show的用法和v-if非常的相似，也用于决定一个元素是否渲染

- v-if和v-show对比
- v-if和v-show都可以决定一个元素是否渲染，那么开发中我们该如何选择呢？
  - v-if当条件为false时，压根不会有对应的元素在DOM中
  - v-show当条件为false时，仅仅是将元素的display属性设置为none而已
- 开发中如何选择？
  - 当需要在显示与隐藏之间切片很频繁时，使用v-show
  - 当只有一次切换的时候，使用v-if



# 15、v-for

- 当我们有一组数据需要进行渲染时，我们就可以使用v-for来完成
  - v-for格式： item in items 的形式
- 获取遍历的索引值
  - 格式： v-for =(item,index) in items 
  - 其中index代表了取出的数据的索引值

v-for可以遍历数组，对象，对象中的key和value

代码:

```html
<div id="app">
  <ul>
    <!-- 1、遍历数组中的value值 -->
    <li v-for="name in names">{{name}}</li>
  </ul>
  <ul>
    <!-- 2、遍历对象中的value值 -->
    <li v-for="item in person">{{item}}</li>
  </ul>
  <ul>
    <!-- 3、遍历对象中的key、value值 -->
    <li v-for="(value,key) in person">{{key}} : {{value}}</li>
  </ul>
</div>
```

完整代码演示：

```html
<div id="app">
  <ul>
    <!-- 1、遍历数组中的value值 -->
    <li v-for="name in names">{{name}}</li>
  </ul>
  <ul>
    <!-- 2、遍历对象中的value值 -->
    <li v-for="item in person">{{item}}</li>
  </ul>
  <ul>
    <!-- 3、遍历对象中的key、value值 -->
    <li v-for="(value,key) in person">{{key}} : {{value}}</li>
  </ul>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
      names: ['Lucy','Curry','Tom','James'],
      person: {
        name: 'Tom',
        age: 18,
        address: '北京'
      }
    },
    methods: {

    }
  })  
</script>
</body>
```



# 16、数组中的哪些方法是响应式的

响应式是什么意思呢？

当我们修改了数据，数据会在页面上直接更新出来，这就是响应式。

#### 数组的push方法是响应式的

```html
const app = new Vue({
    el: '#app',
    data: {
      letters: ['a','b','c','d','e']
    },
    methods: {
      btnClick(){
        this.letters.push('aaa')
      }
    }
  })  
```

#### 数组的pop方法是响应式的

删除数组中的最后一个元素

```html
btnClick(){
  this.letters.pop()
}
```

#### 数组的shift方法是响应式的

删除数组中的第一个元素

```html
btnClick(){
  this.letters.shift()
}
```

#### 数组的unshift方法是响应式的

在数组的最前面添加元素，可以添加多个

```html
btnClick(){
  this.letters.unshift('aaa','bbb','ccc')
}
```

#### 数组的splice方法是响应式的

```html
// splice作用： 删除元素/替换元素/插入元素
// splice(start)
// 删除元素：第二个参数传入要删除几个元素
//this.letters.splice(1,3)//从下标为1开始，删除3个元素
// 替换元素：第二个参数传入要替换几个元素，后边的参数写需要替换为的元素
//this.letters.splice(1,2,'m','n') //从下标为1开始，替换后边的两个元素为m和n
// 插入元素：第二个参数传入0，后边传入需要插入的元素
this.letters.splice(1,0,'m','n','l') //从下标为1开始，插入m、n、l
```

### sort()方法

排序

### reverse()方法

数组反转

### Vue的set方法也是响应式的

```html
// set方法三个参数：（需要修改的对象，修改的下标值，需要修改为的值）
Vue.set(this.letters,0,'aaa')
```





### 注意！通过索引值修改数组中的元素不是响应式的

我们发现通过索引值修改数组中的元素之后，页面并不会刷新出来新的数据

```html
btnClick(){
  this.letters[0] = 'ccc'
}
```



# 17、小案例

需求：

一个数组中存储电影名称，实现功能：点击电影名称之后，让电影名称变为红色！

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<style>
  .active{
    color: red
  }
</style>
<body>
    
<div id="app">
  <ul>
    <li v-for='(item , index) in movies' 
    :class="{active: index === currentIndex}"
    @click="liClick(index)">
    {{item}}
  </li>
  </ul>
</div>

<script src="./vue.min.js"></script>
<script>

  const app = new Vue({
    el: '#app',
    data: {
      movies: ['海王','海贼王','调音师','大话西游','武庚纪'],
      currentIndex: 0
    },
    methods: {
      liClick(index){
        this.currentIndex = index
      }
    }
  })  
</script>
</body>
</html>
```

# 18、过滤器filters



过滤器定义

```html
filters: {
showPirce(param){
  //对param参数进行处理后返回
}
}
```

过滤器使用

对参数param进行过滤

```html
{{param|showPrice}}
```

# 19、toFixed()函数

让数据保留小数

让param数据保留两位小数

```html
{{param.toFixed(2)}}
```

# 20、购物车综合小案例

功能：

- 可以实现商品的数量的增加和减少，当减少到1之后，不能够再继续减少
- 可以移除商品
- 当商品全部移除完毕之后，显示购物车为空
- 当增加或者减少商品时，总价格也随之改变

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
  <style>
    table{
      border: red 1px solid;
    }
    tr,td{
      border: red 1px solid;
    }
  </style>
</head>
<body>
  
  <div id="app">
    <div v-if="books.length">
      <table>
        <tr>
          <td>书籍名称</td>
          <td>出版日期</td>
          <td>价格</td>
          <td>购买数量</td>
          <td>操作</td>
        </tr>
        <tr v-for="(item,index) in books">
          <td>{{item.name}}</td>
          <td>{{item.date}}</td>
          <!-- toFixed(2)：让价格保留两位小数 -->
          <td>{{item.price | showPirce()}}</td>
          <td>  
            <!-- click事件传入index，用于改变书的数量 -->
            <!-- v-bind绑定disabled，实现当数量小于1的时候，不能够再减少 -->
            <button @click="decrement(index)" :disabled="item.number <= 1">-</button>
            {{item.number}}
            <button @click="increment(index)">+</button>
          </td>
          <td><button @click="remove(index)">移除</button></td>
        </tr>
      </table>
      <!-- 使用过滤器 -->
      <h2>总价格：{{totalPrice | showPirce}}</h2>
    </div>
    <h2 v-else>购物车为空</h2>
  </div>

  <script src="./vue.min.js"></script>
  <script>
    const app = new Vue({
      el: "#app",
      data: {
        books: [
          {id: 101 , name: '编程珠玑' ,date: '2019-02-02' , price: 68 , number: 1},
          {id: 102 , name: '计算机组成原理' ,date: '2019-04-02' , price: 49 , number: 1},
          {id: 103 , name: '数据结构与算法' ,date: '2019-05-02' , price: 34 , number: 1},
          {id: 104 , name: '编译原理' ,date: '2019-02-02' , price: 75 , number: 1},
        ]
      },
      // 过滤器
      filters:{
        showPirce(price){
          return '￥' + price.toFixed(2);
        }
      },
      methods: {
        increment(index){
          this.books[index].number++
        },
        decrement(index){
          this.books[index].number--
        },
        //移除方法
        remove(index){
          // 使用splice方法移除，从index下标开始，移除一个元素
          this.books.splice(index,1)
        }
      },
      computed: {
        // 使用计算属性展示总价格
        totalPrice(){
          let totalPrice = 0;
          for(let i = 0 ; i < this.books.length ; i++)
          {
            totalPrice += this.books[i].price * this.books[i].number
          }
          return totalPrice
        }
      }
    })
  </script>
</body>
</html>
```



# 21、JavaScript高阶函数的使用

### for循环的简单遍历



**注意，这里取到的i的索引值** 

```html
for(let i in this.books)
{
  console.log(i);
}
```

**注意，这里取到的是对象值**

```html
for(let book of this.books)
{
  console.log(books)
}
```



**三个高阶函数：**

- filter
- map
- reduce



### 1、filter

可以对数组中的元素进行函数回调，返回true的话，将改元素加入新的数组，返回false的话，过滤掉该元素。

```html
let nums = [1324,1324,34,25,134,4215,53,231];
// filter中的回调函数有一个要求：必须返回布尔类型的值 true/false
// filter的回调函数会取出数组中的每一个值进行回调
// true：当返回true的时候，函数内部会自动将这次回调的n加入到新的数组中
// false：当返回false的时候，函数内部会过滤掉这次的n
// 现在的需求：将数组中100以下的数取出来。使用filter()非常方便
let newNums = nums.filter(function(n){
  if(n < 100)
    return true;
})
console.log(newNums);//会将2打印出来
```

### 2、map

对数组中的元素进行变化

```html
//回调数组中的每个元素，乘2之后，放入新的数组中
let new2Nums = newNums.map(function(n){
  return n*2;
})
console.log(new2Nums);
```

### 3、reduce

reduce的参数：

- 有两个参数：第一个参数是一个回调函数function(prevalue,n){},回调函数也有两个参数
  - 第一个函数：prevalue也就是上次回调之后返回的值
  - 第二个函数：n也就是，每次回调的时候数组中的元素
- 第二个参数：自己设置的一个默认值

```html
// reduce函数的作用：用于对数组中的值进行汇总 
// 如下：求数组中所有的元素的和
let nums2 = [123 , 23 , 245 , 125 , 123 ]
let total = nums2.reduce(function(prevalue,n){
  return prevalue + n
},0)
console.log(total);
```

来看一下reduce函数的执行过程

​    第1次： prevalue = 0 ， n = 123  为什么prevalue为0？因为我们传入了一个默认参数0。

​    第2次： prevalue = 123, n = 23  

​    第3次： prevalue = 146, n = 245

​    第4次： prevalue = 391, n = 125

​    第5次： prevalue = 516, n = 123

​    第6次： prevalue = 639, n = 0



三个高阶函数组合使用

对于一个数组，找出小于100的数，乘以2之后，求出总和。

这就是函数式编程

```html
let total = nums.filter(function(n){
  if(n < 100) return true;
}).map(function(n){
  return n*2;
}).reduce(function(prevalue,n){
  return prevalue + n;
},0)
             //使用箭头函数再次简化
let total2 = nums.filter(n => n < 100).map(n => n*2).reduce((pre,n) => pre+n)
console.log(total);
console.log(total2);
```



# 22、v-model

表单绑定 v-model

v-model简单的实现双向绑定

```html
<input type="text" v-model="message">
{{message}}
```

v-model是如何实现的呢？

```html
<div id="app">
  <!-- v-model也可以通过 v-bind+v-on:input来实现
      v-bind: 通过v-bind绑定message
      @input: 通过@input监听输入框的输入事件，通过$event获取输入事件，将输入的值动态的赋给message
  -->
  <input type="text" :value="message" @input="message = $event.target.value">
  {{message}}
</div>
```

### v-model和radio的结合使用

```html
<body>

  <div id="app">
    <label for="male">
      <!-- 两个input标签的name写一样，保证radio同时只可以选择一个 -->
      <!-- 当我们写了v-model绑定了sex之后，就可以省略name属性，也可以实现两个input标签互斥 -->
      <input type="radio" id="male" name="sex" v-model="sex" value="男"> 男
    </label>
    <label for="female">
      <input type="radio" id="female" name="sex" v-model="sex" value="女">女
    </label>
    <h2>您选择的性别是：{{sex}}</h2>
  </div>
  
  <script src="./vue.min.js"></script>
  <script>
    const app = new Vue({
      el: '#app',
      data: {
        sex: ''
      }
    })
  </script>
</body>
```

### v-model和checkbox的结合使用

绑定checkbox的数据，可以使用数组类型的值

```html
<body>

  <div id="app">

    <input type="checkbox" value="篮球" v-model="hobbies">篮球
    <input type="checkbox" value="足球" v-model="hobbies">足球
    <input type="checkbox" value="排球" v-model="hobbies">排球
    <input type="checkbox" value="乒乓球" v-model="hobbies">乒乓球

    <h2>您选择的爱好是：{{hobbies}}</h2>
  </div>

  <script src="./vue.min.js"></script>
  <script>
    const app = new Vue({
      el: '#app',
      data: {
        hobbies: []
      }
    })
  </script>
</body>
```

### v-model修饰符的使用

修饰符就是在v-model的后边加上一个点

- lazy修饰符
  - 默认情况下，v-model默认是在input事件中同步输入框的数据的。
  - 也就是说，一旦数据发生改变，对应的data中的数据就会自动发生改变
  - lazy修饰符可以让数据在失去焦点或者回车的时候才更新

```html
<input type="text" v-model.lazy="message">
```

- number修饰符
  - 默认情况下，在输入框中无论我们输入的字母还是数字，都会被当作字符串类型进行处理
  - 但是如果我们希望处理的是数字类型，那么最好直接将内容当做数字处理
  - number修饰符可以让在输入框中输入的内容自动转换成数字类型
- trim修饰符
  - 如果输入的内容首位有很多的空格，通常我们希望将其取出
  - trim修饰符可以过滤内容左右两边的空格

# 23、input中的值绑定

通过v-bind:value动态的给value绑定值

```html
<body>
  
  <div id="app">
    <label v-for="item in hobbies" :for="item">
      <input type="checkbox" :value="item" :id="item" v-model="hobbies">{{item}}
    </label>
    <h2>您选择的爱好是：{{hobbies}}</h2>
  </div>

  <script src="./vue.min.js"></script>
  <script>
    const app = new Vue({
      el: '#app',
      data: {
        hobbies: ['篮球','足球','排球','台球','羽毛球']
      }
    })
  </script>
</body>
```

# 24、vue的组件化开发

- 组件的使用分为三个步骤
  - 创建组件构造器
  - 注册组件
  - 使用组件
- Vue.extend()
  - 调用Vue.extend()创建的是一个组件构造器
  - 通常在创建组件构造器时，传入template代表我们自定义组件的模板
  - 该模板就是在使用到组件的地方，要显示的HTML代码
  - 事实上，这种写法在Vue2.x的文档中几乎已经看不到了，他会直接使用下面我们讲到的语法糖。



**一个小语法，定义字符串使用反引号（tab键上的反引号），使用反引号定义的字符串可以进行换行**

组件的创建

```html
  <script>
    // 1、创建组件构造器对象
    const cpnConstructer = Vue.extend({
      template: `
        <div>
          <h2>我是标题</h2>
          <p>我是内容</p>
        </div>
      `
    })
    // 2、注册组件
    Vue.component('my-cpn',cpnConstructer)
    const app = new Vue({
      el: '#app',
      data: {
      }
    })
  </script>
```

组件的使用

```html
<my-cpn></my-cpn>
```

### 全局组件和局部组件

全局和局部的不同在于如何去注册

#### 全局组件

```html
<script> 
  //1、创建组件构造器
  const cpnC = Vue.extend({
    template: `...`
  })
  //2、注册组件，这样注册的是全局组件，可以在多个vue的实例中使用
  Vue.component('cpn',cpnC)
</script>
```

#### 局部组件

```html
<script>
    //1、创建组件构造器
  const cpnC = Vue.extend({
    template: `...`
  })
  
  const app = new Vue({
    el: "#app",
    //2、在vue实例内部创建的是局部组件
    components: {
      //cpn使用组件时的标签名
      cpn: cpnC
    }
  })
</script>
```

### 父组件和子组件

需求：

创建两个组件构造器，将组件构造器1注册进组件构造器2，将组件构造器2注册进cue实例

```html
<body>
  
  <div id="app">
    <cpn2></cpn2>
  </div>

  <script src="./vue.min.js"></script>
  <script>
    // 1、创建组件构造器对象1
    const cpnC1 = Vue.extend({
      template: `
        <div>
          <h2>我是标题1</h2>
          <p>我是内容</p>
        </div>
      `
    })
     // 2、创建组件构造器对象2
     const cpnC2 = Vue.extend({
      template: `
        <div>
          <h2>我是标题2</h2>
          <p>我是内容</p>
          <cpn1></cpn1>
        </div>
      `,
      components: {
        cpn1: cpnC1
      }
    })
    const app = new Vue({
      el: '#app',
      data: {
      },
      components: {
        cpn2: cpnC2
      }
    })
  </script>
</body>
```

### 注册组件语法糖

#### 注册全局组件语法糖

```html
Vue.component('cpn',{
  template: `
    <div>
      <h2>我是标题1</h2>
      <p>我是内容</p>
    </div>
  `
})
```

#### 注册局部组件语法糖

```html
components: {
  'cpn1': {
    template: `
  <div>
    <h2>我是标题1</h2>
    <p>我是内容</p>
  </div>
`
  }
}
```

### 组件模板抽离写法

将组件模板写在template标签中

```html
<template id="cpn">
  <div>
    <h2>我是标题</h2>
    <p>我是内容，哈哈哈</p>
  </div>
</template>
```

完整代码：

```html
<body>

  <div id="app">
    <cpn></cpn>
    <cpn></cpn>
    <cpn></cpn>
  </div>
  <template id="cpn">
    <div>
      <h2>我是标题</h2>
      <p>我是内容，哈哈哈</p>
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>

    Vue.component('cpn',{
      template: '#cpn'
    })

    const app = new Vue({
      el: '#app',
      data: {
      },
      components: {
      }
    })
  </script>
</body>
```



### 组件中如何取出数据？

组件中的data必须是一个函数，并且返回一个对象，对象中写我们需要取出的数据。

需求：

我们组件中的数据也有可能是需要灵活的进行取出的，那么组件中需要的数据我们需要去哪里取出呢？

我们可以在vue实例中取出数据放在组件中吗？不可以。

组件中需要的数据定义在组件中即可！

```html
Vue.component('cpn',{
  template: '#cpn',
  data(){
    return {
      title: 'data中的title数据'
    }
  }
})
```

完整代码：

```html
<body>

  <div id="app">
    <cpn></cpn>
    <cpn></cpn>
    <cpn></cpn>
  </div>
  <template id="cpn">
    <div>
      <h2>我是{{title}}</h2>
      <p>我是内容，哈哈哈</p>
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>

    Vue.component('cpn',{
      template: '#cpn',
      data(){
        return {
          title: 'data中的title数据'
        }
      }
    })

    const app = new Vue({
      el: '#app',
      data: {
      },
      components: {
      }
    })
  </script>
</body>
```

#### 那么为什么组件中的data必须是一个函数呢？

如果一个组件同时使用多次的话，如果data定义的是属性，那么多个组件的data属性可能会造成混乱，如果data定义为一个函数，并且返回的是一个对象，那么每次就会返回一个新的对象，而不会造成多个组件共用一个data的情况。

如果就是想让多个组件共用一个data

```html
const obj = {
  //..定义需要公用的data
}
Vue.component('cpn',{
  template: "#cpn",
  data(){
    //返回obj对象
    return obj;
  }
})
```

# 25、父子组件通信

- 通过props向子组件传递数据
- 通过事件向父组件发送消息



下边的代码中将vue实例当做父组件。

### props基本用法（父传子）

- 在组件中，使用选项props来声明需要从父级接收到的数据
- props的值有两种方式：
  - 方式一：字符串数组，数组中的字符串就是传递时的名称
  - 方式二：对象，对象可以设置传递时的类型，也可以设置默认值等

方式一：

使用：

```html
<cpn :cmovies="movies"></cpn>
```

完整代码

```html
<body>

  <div id="app">
    <!-- 在使用组件的时候，使用v-bind绑定父组件中的值 -->
    <cpn :cmovies="movies"></cpn>
  </div>
  <template id="cpn">
    <div>
      {{cmovies}}
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>

    const cpn = {
      template: "#cpn",
      // 定义传递时的名称
      props: ['cmovies'],
      data(){
        return {}
      }
    }

    const app = new Vue({
      el: '#app',
      data: {
        movies: ['海王','海尔兄弟','调音师']
      },
      //定义组件：用到了属性的增强写法 'cpn':cpn
      components: {
        cpn
      }
    })
  </script>
</body>
```

方式二：

props对象类型的写法。

props对象类型的写法，可以定义类型/是否必须/默认值

```html
const cpn = {
      template: "#cpn",
      // 定义传递时的名称
      // props对象类型的写法，可以定义类型/是否必须/默认值
      props: {
        cmovies: {
          type: Array,
          required: true,
          // 如果是对象或数组类型，默认值必须是函数
          default(){
            return []
          }
        },
        cmessage: {
          type: String,
          required: false,
          default: 'aaa'
        }
      },
      data(){
        return {}
      }
    }
```



完整代码：

```html
<body>

  <div id="app">
    <!-- 在使用组件的时候，使用v-bind绑定父组件中的值 -->
    <cpn :cmovies="movies" :cmessage="message"></cpn>
  </div>
  <template id="cpn">
    <div>
      {{cmovies}}
      {{cmessage}}
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>

    const cpn = {
      template: "#cpn",
      // 定义传递时的名称
      // props对象类型的写法，可以定义类型/是否必须/默认值
      props: {
        cmovies: {
          type: Array,
          required: true,
          // 如果是对象或数组类型，默认值必须是函数
          default(){
            return []
          }
        },
        cmessage: {
          type: String,
          required: false,
          default: 'aaa'
        }
      },
      data(){
        return {}
      }
    }

    const app = new Vue({
      el: '#app',
      data: {
        movies: ['海王','海尔兄弟','调音师'],
        message: '你好'
      },
      //定义组件：用到了属性的增强写法 'cpn':cpn
      components: {
        cpn
      }
    })
  </script>
</body>
```



### props的驼峰规则

在props中定义数据使用驼峰规则的话，在子组件中取数据时，v-bind不支持驼峰规则，必须将驼峰命名转换为-的命名

比如： cInfo --> c-info

```html
<cpn :c-info="info"></cpn>
cInfo
```



完整代码：

```html
<body>

  <div id="app">
    <!-- 注意这里：v-bind不支持驼峰命名，所以写:cInfo的话，获取不到数据 -->
    <cpn :c-info="info"></cpn>
  </div>
  
  <template id="cpn">
    <div>
      <h2>{{cInfo}}</h2>
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>

    const cpn = {
      template: "#cpn",
      props: {
        //这里使用驼峰命名
        cInfo: {
          type: Object,
          default(){
            return {}
          }
        }
      }
    }

    const app = new Vue({
      el: "#app",
      data: {
        info: {name: 'Lucy',age: 19,address: '北京'}
      },
      components: {
        cpn
      }
    })
  </script>
</body>
```

### 父子通信（子传父）

- 自定义事件的流程
  - 在子组件，通过$emit()来向父组件发送事件
    - 两个参数
    - 第一个参数：发送事件的名称，用于父组件监听
    - 第二个参数：发送的参数
  - 在父组件中，通过v-on来监听子组件发送的事件



完整代码

```html
<body>
  
  <div id="app">
    <!-- 3、在父组件中监听发送的事件 -->
    <cpn :c-categories="categories" @item-click="fun"></cpn>
  </div>
  <template id="cpn">
    <div>
      <!-- 1、定义子组件的点击事件 -->
      <button v-for="item in cCategories" @click="itemClick(item)">{{item.name}}</button>
    </div>
  </template>

  <script src="./vue.min.js"></script>
  <script>
    const cpn = {
      template: "#cpn",
      props: {
        cCategories: {
          type: Array,
          default() {
            return {}
          }
        }
      },
      methods:{
        // 2、子组件点击之后，通过$emit向父组件发送事件，第一个参数：发送的事件的名称
        // 第二个参数：发送的参数
        itemClick(item){
          this.$emit("item-click",item)
        }
      }
    }

    const app = new Vue({
      el: "#app",
      data: {
        categories: [
          {id: 1,name: '热门推荐'},
          {id: 2,name: '数码宝贝'},
          {id: 3,name: '家用家电'},
          {id: 4,name: '欢迎光临'}
        ]
      },
      components: {
        cpn
      },
      methods: {
        fun(item){
          console.log(item);
        }
      }
    })
  </script>
</body>
```



# 26、父子访问

**父子组件的访问方式**

- 父组件访问子组件：使用`$children`或者`$refs`
- 子组件访问父组件：使用`$parent`

#### 父组件访问子组件$children使用较少

`$children`通过索引来拿数据，如果数据改变，索引也需要改变，所以使用较少。

需求：子组件中定义了方法，在父组件中想要访问子组件中的方法

`this.$children`获取的是子组件的一个数组，通过下标来取出对应的子组件。

```html
//调用了下标为0的子组件的showMessage()方法
this.$children[0].showMessage()
```

#### 父组件访问子组件$refs较常用

如何去使用呢？

- 使用组件的时候，给组件定义ref属性
- 通过$refs来取出组件

```html
//使用cpn子组件的时候，定义了ref属性为cpn1
<cpn :title="Vtitle" ref="cpn1"></cpn>
//在使用的时候使用$refs来取出cpn1组件，并调用showMessage方法
this.$refs.cpn1.showMessage()
```



#### 子组件访问父组件$parent

使用this.$parent来取出父组件

不过一般不适用，子组件的出现是为了降低耦合度，$parent会增加组件之间的耦合度

#### 子组件访问根组件$root

使用this.$root访问根组件，也使用较少

加入一个子组件有多个层级的父组件，也就是有父组件，有爷组件，使用$root访问的是根组件



# 27、插槽slot的使用

**组件的插槽的也是为了让我们封装的组件更加具有扩展性**

- 插槽的基本使用

  `<slot></slot>`

- 插槽的默认值

  `<slot>button</slot>`

- 如果有多个值，同时放入到组件进行替换，那么会一起作为替换元素



需求：

加入现在有一个组件，组件里有一个按钮，我们想要多次使用这个组件，但是希望第一次使用的时候是按钮，第二次使用的使用将按钮转换为a标签，第三次将按钮转换为input，那么就可以使用slot插槽进行扩展。

代码演示

```html
//使用组件，写在子组件中的内容就会替换插槽对应的位置
<div id="app">
  <cpn></cpn>
  <cpn><a>a标签将会替换插槽</a></cpn>
  <cpn><input placeholder="input标签将会替换插槽.."></cpn>
</div>

//组件模板的定义
<template id="cpn">
  <div>
    <slot><button>按钮</button></slot>
  </div>
</template>
```

### 具名插槽

需求：

在子组件中定义了多个插槽，想要改变指定插槽，就需要给插槽写上名字name

在需要改变插槽的时候，指定插槽的name即可

```html
<div id="app">
  <cpn><button slot="center">button</button></cpn>
</div>

<template id="cpn">
  <div>
    <slot name="left">left</slot>
    <slot name="center">center</slot>
    <slot name="right">right</slot>
  </div>
</template>
```

### 编译作用域



加入下边id为app的div是vue的一个模板，cpn是一个子组件。

在vue实例和子组件中均定义了isShow变量，那么这里取出来的isShow变量是哪个呢？

是vue实例中的isShow变量，因为isShow是在vue的模板中。

```html
<div id="app">
  <cpn v-if="isShow"></cpn>
</div>
```

### 作用域插槽

作用域插槽是slot较难理解的一个点

简单的一句话对其总结：

- 父组件替换插槽的标签，但是内容由子组件来提供。



需求：

- 子组件中包括了一组数据，比如movies['海王','大话西游','调音师']

- 需要在多个界面进行展示

  - 某些界面上水平展示

  - 某些界面上垂直展示


- 而内容在子组件中，我们只需要父组件告诉我们如何展示，那么使用slot作用域插槽即可

使用步骤：

- 在子组件中，绑定需要让父组件展示的数据

  ```html
  <slot :data="movies"></slot>
  ```

- 在父组件中使用slot-scope="slot"接收数据，使用{{slot.data}}即可取出数据

  ```html
  //vue2.5.x之前，必须使用template标签来接收，vue2.5.x之后可以使用其他标签来接收
  <cpn>
    <template slot-scope="slot">
      {{slot.data}}
    </template>
  </cpn>
  ```

  ​

完整代码：

用到了一个函数join函数，函数作用将数据使用特定字符拼接起来。

需求：第一次展示使用li展示，第二次展示使用-拼接，第三次展示使用*拼接

```html
<body>
  
  <div id="app">
    <cpn></cpn>
    <cpn>
      <template slot-scope="slot">
        {{slot.data.join(" - ")}}
      </template>
    </cpn>
    <cpn>
      <template slot-scope="slot">
        {{slot.data.join(" * ")}}
      </template>
    </cpn>
  </div>

  <template id="cpn">
    <div>
      <!--data的名称可以自己随意起，不过一般使用data-->
      <slot :data="movies">
        <ul>
          <li v-for="item in movies">{{item}}</li>
        </ul>
      </slot>
    </div>
  </template>
  <script src="./vue.min.js"></script>
  <script>
    const app = new Vue({
      el: "#app",
      components: {
        'cpn':{
          template: "#cpn",
          data(){
            return {
              movies: ['海王','大话西游','调音师']
            }
          }
        }
      }
    })
  </script>
</body>
```



# 28、ES6的模块化实现、

多个js文件会造成变量命名冲突，所以引入了模块化的概念。

ES6的模块化会有跨域问题，需要搭建本地服务器，使用HBuilder也可以解决

**注意，我们需要保证引入的javascript文件type是module**

```html
<script src="./aaa.js" type="module"></script>
```



### export导出

```html
//导出方式一 导出已经定义的变量或者函数
export {flag,name,sum}

//导出方式二 导出变量
export var height = 100;

//导出方式三 导出函数
export function mul(num1,num2){
  return num1 * num2;
}
```



**export default**

当我们想要导出功能，但是功能的名称让导入这自己去命名，就可以使用export default

**一个js文件有且仅仅只能有一个export default**

```html
export default const address = "北京市"
import addr from 'aaa.js'  //不需要加{}
```





### import导入

```html
//导入指定文件
import {flag,name,sum} from './aaa.js'

//同意全部导入,通过as起别名
import * as aaa from 'aaa.js'
```



