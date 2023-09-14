---
typora-copy-images-to: imgs
---



# 说明：CentOS7 服务器





# 0、关闭防火墙

```
systemctl stop firewalld.service
systemctl disable firewalld.service
```





# 1、安装xshell连接虚拟机

1. 直接官网安装xshell免费版

2. 设置VirtualBox的网络端口转发

   ![1688873520412](imgs/1688873520412.png)

   ![1688873540021](imgs/1688873540021.png)

3. 使用xshell连接 `127.0.0.1:2222` 即可连接上

   ![1688873872976](imgs/1688873872976.png)

   注意，这里的端口转发规则中，子系统IP 10.0.2.15 是虚拟机里linux的ip，centos7使用`ip addr`查看

   如果没有ip的话，使用`vim /etc/sysconfig/network-scripts/ifcfg-ens33`，设置最后一行的ONBOOT为yes，之后再重启就可以

   ​



# 2、安装docker

```shell
yum -y update
yum install -y yum-utils device-mapper-persistent-data lvm2 #安装依赖包
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
yum -y install docker-ce-18.03.1.ce
systemctl start docker
systemctl enable docker # 开机自启
```



# 3、安装nginx

```bash
yum install -y gcc-c++ 
yum install -y pcre pcre-devel
yum install -y zlib zlib-devel
yum install -y openssl openssl-devel
```



下载nginx包

http://nginx.org/download/

下载nginx-1.14.2.tar.gz ，上传到linux

```bash
tar -zxvf nginx-1.14.2.tar.gz
mv nginx-1.14.2 nginx
cp -r nginx /usr/local/src
cd /usr/local/src/nginx
mkdir /usr/nginx
./configure --prefix=/usr/nginx    #(指定安装目录编译)
make
make install  
# 进入nginx的sbin目录，./nginx就可以启动
./nginx #启动
#关闭nginx可以使用kill命令，但是不推荐使用。推荐使用：
#刷新配置重启：./nginx -s reload
./nginx -s stop # 关闭

# 如果访问不到，记得关闭防火墙
systemctl stop firewalld.service # 关闭防火墙
```

之后访问ip:80即可看到效果如下图

![1688896100196](imgs/1688896100196.png)



> 这里使用的虚拟机是virtualbox，需要加上端口转发

![1688902424038](imgs/1688902424038.png)

这样只需要访问127.0.0.1就可以直接访问虚拟机里的nginx了！





# 4、安装mysql

```
docker run --name api-gateway-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.7
```





# 5、安装zookeeper

```bash
mkdir /root/docker/zookeeper # 创建挂载文件夹
docker run -e TZ="Asia/Shanghai" -d -p 2181:2181 -v /root/docker/zookeeper:/data --name zookeeper --privileged=true zookeeper  # 启动  -e：向容器内传递环境变量，启动容器时用户可以动态传参
```



# 6、安装mongodb

```bash
docker run --name mongodb -v  /export/server/docker/mongodb/data:/data/db -d -p 27017:27017 --privileged=true mongo --auth 
docker exec -it mongodb /bin/mongosh # 进入 mongodb 容器
use admin # 切换到 admin 数据库
# 创建新用户，并赋予权限，再使用新用户登陆
db.createUser({ user:'test',pwd:'123',roles:[ { role:'userAdminAnyDatabase', db: 'admin'},"readWriteAnyDatabase"]});
db.auth('test', '123')
# 测试
show databases #查看所有数据库
show tables #查看所有表(集合)
use test #切换数据库(如不存在则先自动创建)
db.test.insert( { _id: 1, name: "kudaren", age: "29" } ) #insert
db.test.find();#select

```



## json数据导入mongodb

```bash
docker cp /root/mongodb/data/ mongodb:/root/mongodb/ # 将宿主机的data目录复制到mongodb容器内的root/mongodb目录下
docker exec -it mongodb /bin/bash # 进入 mongodb 容器
cd /bin # 进入bin目录
# 执行以下命令
mongoimport --db imooc-admin --collection article-list -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/article-list.json 
mongoimport --db imooc-admin --collection chapter -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/chapter.json
mongoimport --db imooc-admin --collection feature -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/feature.json
mongoimport --db imooc-admin --collection permission -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/permission.json
mongoimport --db imooc-admin --collection roles -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/roles.json
mongoimport --db imooc-admin --collection user-list -u test -p 123 --authenticationDatabase admin --jsonArray /root/mongodb/data/user-list.json


```



## mongoose连接mongodb

```js
/*!
 * Koa CMS Backstage management
 *
 * Copyright JS suwenhao
 * Released under the ISC license
 * Email swh1057607246@qq.com
 *
 */
const mongoose = require('mongoose')
const db = mongoose.connect(
  'mongodb://localhost:27017/imooc-admin?authSource=admin', // 需要指定授权
  // 'mongodb://test:123@127.0.0.1:27017/imooc-admin',
  {
    username: 'test',
    password: '123',
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useFindAndModify: false
  },
  function (error) {
    if (error) {
      console.log('连接本地mongo数据库失败:' + error.message)
    } else {
      console.log('连接本地mongo数据库成功')
    }
  }
)
module.exports = db

```



