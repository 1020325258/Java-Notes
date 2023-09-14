















总结：

webpack.config.js文件内容解析：

```javascript
const path = require('path') //引入全局变量路径，在定义出口的时候可以用到
const webpack = require('webpack') //引入webpack
const HtmlWebpackPlugin = require('html-webpack-plugin') //引入打包html文件的插件，将index.html打包到dist文件夹下
const UngliFyJsPlugin = require('uglifyjs-webpack-plugin') //引入js文件压缩插件（丑化插件）
module.exports = {
  entry: './src/main.js',  // 入口，打包main.js文件
  output: {                // 出口，将main.js文件打包到dist文件夹下的bundle.js文件中
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    // publicPath: 'dist/' //在使用url-loader解析图片的时候加上的publicPath，否则找不到图片
  },
  module: {
    rules: [
      {
        test: /\.css$/, //正则表达式:匹配所有的css文件
        // webpack加载多个loader的时候，从右向左加载，所以css-loader放在右边，先解析css文件
        // css-loader：解析 CSS 文件后，使用 import 加载，并且返回 CSS 代码
        // style-loader：将模块的导出作为样式添加到 DOM 中
        // 所以，还需要安装style-loader，css-loader只负责加载css文件，并不将样式添加到DOM中
        use: ['style-loader', 'css-loader'] //在这里指明需要使用到的loader
      },
      {
        test: /\.less$/,
        use: [{
          loader: "style-loader" // creates style nodes from JS strings
        }, {
          loader: "css-loader" // translates CSS into CommonJS
        }, {
          loader: "less-loader" // compiles Less to CSS
        }]
      },
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: 'url-loader', //用于打包图片
            options: {
              limit: 8192 //默认8kb，超过8kb的图片需要使用file-loader进行解析，所以需要npm install file-loader --save-dev
            }
          }
        ]
      },
      {
        test: /\.js$/, //用于打包js文件
        // exclude：排除  不对node_modules和bower_components文件下的内容进行打包
        // include：包含
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: 'babel-loader', //使用babel-loader
          options: {
            presets: ['es2015'] //es2015也就是es6，最新的es
          }
        }
      },
      {
        test: /\.vue$/,
        use: ['vue-loader'] //打包.vue文件
      }
    ]
  },
  resolve: {
    // alias： 别名
    alias: {
      // 使用vue环境运行的时候需要配置这个，指向dist文件夹里的vue.esm.js文件，可以编译template。
      'vue$': 'vue/dist/vue.esm.js' 
    }
  },
  plugins: [
    // 打包html插件
    new HtmlWebpackPlugin({
      template: 'index.html' //模板是index.html文件
    }),
    //丑化插件，压缩js文件
    new UngliFyJsPlugin()
  ],
  devServer: {
    contentBase: './dist', //指定本地服务器的文件夹，默认是根文件，这里指定dist文件夹
    inline: true //是否实时刷新
  }
}
```

package.json文件解析：

webpack:版本3.6.0

vue-loader:版本^13.0.0

webpack-dev-server:版本^2.9.1

```json
{
  "name": "meetwebpack",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack",
    "dev": "webpack-dev-server --open"
  },
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "babel-core": "^6.26.3",
    "babel-loader": "^7.1.5",
    "babel-preset-es2015": "^6.24.1",
    "css-loader": "^3.6.0",
    "file-loader": "^6.0.0",
    "html-webpack-plugin": "^3.2.0",
    "less": "^3.9",
    "less-loader": "^5.0.0",
    "style-loader": "^1.2.1",
    "uglifyjs-webpack-plugin": "^1.1.1",
    "url-loader": "^4.1.0",
    "vue-loader": "^13.0.0",
    "vue-template-compiler": "^2.6.11",
    "webpack": "^3.6.0", 
    "webpack-dev-server": "^2.9.1"
  },
  "dependencies": {
    "vue": "^2.6.11"
  }
}

```



# 1、webpack

### 1、认是webpack

webpack是一个现代的JavaScript应用的静态模块打包工具

- **webpack为了可以正常运行，必须依赖node环境**
- **node环境为了可以正常的执行很多代码，必须其中包含很多依赖的包，所以在安装node的时候会自动安装npm工具（node packages manager）**

### 2、webpack的安装

webpack4的打包命令发生改变：

```vue
webpack ./src/main.js -o ./dist/bundle.js --mode development
```



- 安装webpack首先需要安装Node.js，Node.js自带了软件包管理工具npm

- 查看自己的node版本

  node -v

- 全局安装webpack（这里先安装3.6.0）（下边的是局部安装）

  ```
  npm install webpack@3.6.0 --save-dev 
  ```


- 局部安装webpack
  - --save-dev是开发时依赖，项目打包后不需要继续使用的
- 为什么全局安装之后还需要局部安装呢？
  - 在终端执行webpack命令，使用的全局安装的webpack
  - 当在package.json中定义了scripts时，其中包含了webpack命令，那么使用的是局部webpack



### 3、webpack.config.js

- 使用到了node的语法\

  动态获取文件的路径，这里用到了node

  **使用node之前，要在命令行： npm init**，然后会生成package.json文件

  **如果package.json文件中依赖了东西的话，在命令行：npm install**，然后会自动导入需要的依赖

  ```vue
  const path = require('path')
  ```



webpack.config.js文件的配置：

path是node中的一个模块，所以使用之前需要配置node，就是`npm init npm install`

- entry和output是入口和出口，也就是打包的时候，如果每次都写打包的路径，例如`webpack ./src/main.js -o ./dist/bundle.js --mode development`的话很麻烦
- 那么我们希望直接输入webpack就可以自动打包，就在webpack.config.js文件中配置
- output中的path不能使用相对路径，所以获取到了path这个模块，path中有一个全局属性`__dirname`，dirname就是总路径，后边加上文件夹的名称即可。
- 下边的配置文件的意思就是将 `./src/main.js`文件打包到`./dist/bundle.js`文件

```javascript
const path = require('path')

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname,'dist'),
    filename: 'bundle.js'
  }
}
```





### 4、package.json

下边就是初始化后的package.json文件

```json
{
  "name": "meetwebpack",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "",
  "license": "ISC"
}
```

scripts属性中有一个属性test：

作用就是：

在命令行输入`npm run test`的话，就会执行test后的内容。

当`npm install webpack@3.6.0 --save-dev`局部安装webpack之后，package.json文件中就会多个属性(devDependencies)

```json
{
  "name": "meetwebpack",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "webpack": "^3.6.0"
  }
}
```



### 5、npm run test和直接webpack的区别

加入我们在package.json文件中添加了脚本

```javascript
{
  "name": "meetwebpack",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack"
  },
  "author": "",
  "license": "ISC",
}
```

上边在scripts中添加了build，也就是打包

**那么我们使用npm run build 和 使用webpack有什么区别呢？**

使用npm run build其实就是将 build属性后边的内容替换过来，但是如果使用npm的话，会现在本地去寻找命令，找不到的话再去全局的webpack中寻找。

而在开发中，有可能本地的webpack的全局的webpack版本不一致，所以npm的好处就是不会直接去全局寻找，不会造成版本不一致而出错的问题。



### 6、webpack中使用css文件的配置

### 1、loader

- loader是webpack中一个非常核心的概念
- webpack用来做什么呢？
  - 在我们之前的实例中，我们主要是用webpack来处理我们写的js代码，并且webpack会自动处理js文件之间的依赖
  - 但是，在开发中我们不仅仅有基本的js代码处理，我们也需要加载css、图片，也包括一些高级的将ES6转成ES5代码，将TypeScript转成ES5代码，将scss、less转成css，将.jsx、.vue文件转成js文件等
  - 对于webpack本身的能力来说，对于这些转化是不支持的
  - 那么怎么办呢？给webpack扩展对应的loader就可以了
- loader使用过程
  - 步骤一：通过npm安装需要使用的loader
  - 步骤二：在webpack.config.js中的modules关键字下进行配置
- 大部分loader我们都可以在webpack的官网中找到，并且学习对应的用法。



***



webpack官网loader描述:https://www.webpackjs.com/loaders/css-loader/

### 2、css-loader打包css文件

步骤：

- 一：想把css文件也打包到bundle.js中，就必须在main.js文件中依赖需要打包的css文件，在main.js文件中依赖css文件

```javascript
require('./css/normal.css')
```

- 二：安装css-loader和style-loader，为什么还需要style-loader呢？css-loader负责解析CSS文件，style-loader负责将解析后的样式添加到DOM中，如果只解析CSS文件，样式是不会生效的！

`npm install style-loader --save-dev`

`npm install css-loader --save-dev`

- 配置webpack.config.js，注释解释的很清楚。

```javascript
const path = require('path')

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname,'dist'),
    filename: 'bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.css$/, //正则表达式:匹配所有的css文件
        // webpack加载多个loader的时候，从右向左加载，所以css-loader放在右边，先解析css文件
        // css-loader：解析 CSS 文件后，使用 import 加载，并且返回 CSS 代码
        // style-loader：将模块的导出作为样式添加到 DOM 中
        // 所以，还需要安装style-loader，css-loader只负责加载css文件，并不将样式添加到DOM中
        use: [ 'style-loader', 'css-loader' ] 
      }
    ]
  }
}
```

- 执行`npm run build`（已经在package.json文件中配置build的命令为webpack打包命令）

- 打包css文件完成后，在页面中引入bundle.js文件，样式已经生效

  ```html
  <script src="./dist/bundle.js"></script>
  ```

  ​

#### 完整的package.json文件

```json
{
  "name": "meetwebpack",
  "version": "1.0.0",
  "description": "",
  "main": "webpack.config.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack"
  },
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "css-loader": "^3.6.0",
    "style-loader": "^1.2.1",
    "webpack": "^3.6.0"
  }
}

```



#### 完整的webpack.config.js文件

```javascript
const path = require('path')

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname,'dist'),
    filename: 'bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.css$/, //正则表达式:匹配所有的css文件
        // webpack加载多个loader的时候，从右向左加载，所以css-loader放在右边，先解析css文件
        // css-loader：解析 CSS 文件后，使用 import 加载，并且返回 CSS 代码
        // style-loader：将模块的导出作为样式添加到 DOM 中
        // 所以，还需要安装style-loader，css-loader只负责加载css文件，并不将样式添加到DOM中
        use: [ 'style-loader', 'css-loader' ] 
      }
    ]
  }
}
```

#### 完整的main.js文件

```javascript
require('./css/normal.css')
```



### 3、less-loader打包less文件

打包步骤：

- 一：安装less-loader和less，less-loader负责加载less文件，less负责解析less文件

`npm install --save-dev less-loader less`

- 二：编写less文件，并在main.js文件中依赖

```javascript
//special.less
@fontSize: 50px;
@fontColor: orange;
body {
  font-size: @fontSize;
  color: @fontColor;
}
```

main.js中依赖

```javascript
require('./css/special.less')
```

- 三：配置webpack.config.js文件

```javascript
{
  test: /\.less$/,
  use: [{
      loader: "style-loader" // creates style nodes from JS strings
  }, {
      loader: "css-loader" // translates CSS into CommonJS
  }, {
      loader: "less-loader" // compiles Less to CSS
  }]
}
```

- 四：npm run build进行打包

可能会报错`（Module build failed: TypeError: loaderContext.getResolve is not a function）`，原因less和less-loader的版本过高。

找到package.json文件修改版本如下：

```json
"less": "^3.9",
"less-loader": "^5.0.0",
```

再重新`npm install`即可



### 4、webpack图片文件的处理



处理步骤：

- 一：将css文件添加到main.js依赖，在css文件中写入图片

```css
//normal.css
body {
  background: url('../img/test1.jpg');
}
```

添加到main.js依赖

```javascript
require('./css/normal.css')
```

- 二：安装url-loader

`npm install --save-dev url-loader`

- 三：配置webpack.config.js文件

**limit属性：**

当加载的图片，小于limit时，会将图片编译成base64字符串形式

当加载的图片，大于limit时，需要使用**file-loader模块**进行加载，所以还需要安装file-loader

**name属性：**

name属性可以定义图片命名规范，[name]用于取出原来图片的name，[hash:8]表示使用8位hash，[ext]取出原来图片的扩展名

```javascript
{
  test: /\.(png|jpg|gif)$/,
  use: [
    {
      loader: 'url-loader',
      options: {
        limit: 8192//默认是8192==8*1024 也就是8kb，
        name: 'img/[name].[hash:8].[ext]'
      }
    }
  ]
}
```

- 四：安装file-loader ，原因见步骤三

`npm install file-loader --save-dev`

- 五：`npm run build`打包即可显示效果。



**注意，如果图片大于limit的话，需要给url进行处理否则找不到图片！**

如果图片大于limit的话，会使用file-loader进行加载，并且图片会被加载到dist文件下边，url中是不知道dist文件夹的，所以会找不到图片。

**只需要在webpack.config.js文件中配置`publicPath: 'dist/'`即可**

webpack.config.js文件

`publicPath`属性：只要碰到url，就会在url前边加上publicPath后边的内容。

```javascript
const path = require('path')

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    publicPath: 'dist/'
  },
  module: {
    rules: [
      {
        test: /\.css$/, //正则表达式:匹配所有的css文件
        // webpack加载多个loader的时候，从右向左加载，所以css-loader放在右边，先解析css文件
        // css-loader：解析 CSS 文件后，使用 import 加载，并且返回 CSS 代码
        // style-loader：将模块的导出作为样式添加到 DOM 中
        // 所以，还需要安装style-loader，css-loader只负责加载css文件，并不将样式添加到DOM中
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.less$/,
        use: [{
          loader: "style-loader" // creates style nodes from JS strings
        }, {
          loader: "css-loader" // translates CSS into CommonJS
        }, {
          loader: "less-loader" // compiles Less to CSS
        }]
      },
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 8192
            }
          }
        ]
      }
    ]
  }
}
```

### 5、webpack的ES6语法转为ES5语法

**在webpack中，如果希望将ES6的语法转成ES5，就需要使用babel对应的loader就可以了。**



打包步骤：

- 一：安装babel-loader，和官方的不同

@7： 安装7的版本

`npm install --save-dev  babel-loader@7 babel-core babel-preset-es2015`

- 二：配置webpack.config.js文件

```javascript
{
  test: /\.js$/,
  // exclude：排除  不对node_modules和bower_components文件下的内容进行打包
  // include：包含
  exclude: /(node_modules|bower_components)/,
  use: {
    loader: 'babel-loader',
    options: {
      presets: ['es2015']
    }
  }
}
```



- 三：打包 `npm run build`，发现bundle.js中的内容变为了ES5的语法

发现bundle.js文件中已经找不到了ES6的语法了，比如：const



# 2、webpack引入Vuejs

- 后续项目中，我们会使用Vuejs进行开发，而且会以特殊的文件来组织vue的组件

  - 所以，下面我们来学习一下如何在webpack环境中集成Vuejs

- 现在，我们希望在项目中使用Vuejs，那么必然需要对其有依赖，所以需要先进行安装

  - 注意：我们后续在实际项目中也会使用vue的，所以并不是开发时依赖

  `npm install vue --save`

  --save-dev是开发时依赖，运行时不需要，但是vue环境在运行时也需要，所以不加-dev。

使用vue

```vue
import Vue from 'vue';//不需要写路径，会自动去node_modules文件夹中寻找vue
new Vue({
  el: '#app',
  data: {
    message: 'Hello Webpack'
  }
})
```

>  vue最终构建发布版本的时候，构建了两个版本：
>
> 1.runtime-only  --> 代码中不可以有任何的template
>
> 2.runtime-complier --> 代码中可以有template，因为有complier可以用于编译template

当我们使用vue的时候，使用el:'#app'挂载app元素的时候把app当做了template，所以会报错，需要进行以下修改。

>  修改webpack.config.js文件，添加resolve属性







```javascript
const path = require('path')

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    publicPath: 'dist/'
  },
  module: {
  },
  resolve: {
    // alias： 别名
    alias: {
      //在import vue的时候指向vue/dist/vue.esm.js,vue.esm.js中有runtime-complier可以用于编译template
      'vue$': 'vue/dist/vue.esm.js' //vue$:$的意思是结尾
    }
  }
}
```





```
 rules: [
      {
        test: /\.css$/, //正则表达式:匹配所有的css文件
        // webpack加载多个loader的时候，从右向左加载，所以css-loader放在右边，先解析css文件
        // css-loader：解析 CSS 文件后，使用 import 加载，并且返回 CSS 代码
        // style-loader：将模块的导出作为样式添加到 DOM 中
        // 所以，还需要安装style-loader，css-loader只负责加载css文件，并不将样式添加到DOM中
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.less$/,
        use: [{
          loader: "style-loader" // creates style nodes from JS strings
        }, {
          loader: "css-loader" // translates CSS into CommonJS
        }, {
          loader: "less-loader" // compiles Less to CSS
        }]
      },
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 8192
            }
          }
        ]
      },
      {
        test: /\.js$/,
        // exclude：排除  不对node_modules和bower_components文件下的内容进行打包
        // include：包含
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['es2015']
          }
        }
      }
    ]
```



# 3、Vue的终极开发

### 安装vue-loader



- 一：安装vue-loader和vue-template-compiler

`npm install vue-loader vue-template-compiler --save-dev`

- 二：修改webpack.configl.js文件

```javascript
{
  test: /\.vue$/,
  use: ['vue-loader']
}
```

如果报错。可能vue-loader版本过高，修改package.json文件vue-loader版本为：

^13.0.0 就会在13版本之后找到对应的版本。

再执行`npm install`



# 4、webpack横幅plugin的使用

### 1、认识plugin

- plugin是什么？
  - plugin是插件的意思，通常是用于对某个现有的架构进行扩展
  - webpack中的插件，就是对webpack现有功能的各种扩展，比如打包优化，文件压缩等等
- loader和plugin区别
  - loader主要用于转换某些类型的模块，他是一个转换器
  - plugin是插件，它是对webpack本身的扩展，是一个扩展器
- plugin的使用过程
  - 步骤一：通过npm安装需要使用的plugins
  - 步骤二：在webpack.config.js中的plugins中配置插件



### 2、横幅插件的使用

在webpack.config.js文件中添加：

添加下边的横幅插件的作用就是，在最后打包的bundle.js文件的第一行添加：

`版权归aaa所有`

```javascript
const webpack = require('webpack')
module.exports = {
  ..
  plugins: [
    new webpack.BannerPlugin('版权归aaa所有')
  ]
}
```



### 3、HtmlWebpackPlugin的使用

打包html的plugin

- 目前，我们的index.html文件是存放在项目的根目录下的

  -  我们直到，在真是发布项目的时候，发布的是dist文件夹的内容，但是dist文件夹如果没有index.html文件，那么打包的js等文件也就没有意义了
  - 所以，我们需要将index.html文件打包到dist文件夹中，这个时候就可以使用HtmlWebpackPlugin插件

- HtmlWebpackPlugin插件可以为我们做这些事情

  - 自动生成一个index.html文件（可以指定模板来生成）
  - 将打包的js文件，自动通过script标签插入到body中

- 安装HtmlWebpackPlugin插件**(要使用3.2.0版本，否则会报错)** 

  `npm install html-webpack-plugin@3.2.0  --save-dev`

- 使用插件，修改webpack.config,js文件中的plugins部分的内容如下

  ```javascript
  const HtmlWebpackPlugin = require('html-webpack-plugin')//使用require引入
  plugins: [
    new webpack.BannerPlugin('最后版权归aaa所有'),//横幅插件
    new HtmlWebpackPlugin({
      template: 'index.html' //根据index.html模板生成
    })
  ]
  ```

  - 这里的template表示根据什么模板来生成index.html
  - 另外，我们需要删除之前在output中添加的publicPath属性
  - 否则插入的script标签中的src可能会有问题

index.html模板：

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>
  <div id="app">
  </div>
</body>
</html>
```



### 4、js压缩的plugin

- 在项目发布之前，我们必然对js等文件进行压缩处理

  - 我们使用一个第三方的插件`uglifyjs-webpack-plugin`，并且指定版本号1.1.1，和CLI2保持一致

    `npm install uglifyjs-webpack-plugin@1.1.1 --save-dev`

- 修改webpack.config.js文件，使用插件

  ```javascript
  const uglifyJsPlugin = require('uglifyjs-webpack-plugin')

  module.exports = {
    ...
    plugins: [
      new uglifyJsPlugin()
    ]
  }
  ```

  ​

# 5、webpack搭建本地服务器

- webpack提供了一个可选的本地开发服务器，这个本地服务器基于node.js搭建，内部使用express框架，可以实现我们想要的让浏览器自动刷新显示我们修改后的结果

- 不过它是一个单独的模块，在webpack中使用之前需要先安装它

  webpack3.6.0版本对应的dev-server版本为2.9.1

  `npm install --save-dev webpack-dev-server@2.9.1`

- devserver也是作为webpack中的一个选项，选项本身可以设置如下属性：

  - contentBase:为哪一个文件夹提供本地服务，默认是根文件夹，我们这里要填写`./dist`
  - port:端口号
  - inline:页面实时刷新(是否实时监听)
  - historyApiFallback:在SPA页面中，依赖HTML5的history模式

- webpack.config.js文件配置修改如下

  ```javascript
  devServer: {
    contentBase: './dist',
    inline: true
  }
  ```

- 我们可以再配置另外一个scripts：（在package.json中）

  - --open参数表示直接打开浏览器

  ```
  "dev": "webpack-dev-server --open"
  ```

  ​