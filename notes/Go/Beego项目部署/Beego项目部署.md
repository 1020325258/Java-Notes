---
typora-copy-images-to: imgs
---



# Beego项目部署

## 编写 Dockerfile

```dockerfile
FROM golang:1.15.6

#正常这2句不需要，上网   逼得
RUN go env -w GO111MODULE=on
RUN go env -w GOPROXY=https://goproxy.cn,direct
# Install beego and the bee dev tool*
RUN go get github.com/astaxie/beego && go get github.com/beego/bee

# 以后命令都基于这个目录执行
WORKDIR /go/src/demo 

# Expose the application on port 8080
EXPOSE 8880

# Set the entry point of the container to the bee command that runs the
# application and watches for changes
CMD ["bee", "run"]
```



## 编写script脚本

```bash
#!/bin/bash 

#构建docker镜像
echo "========================================正在构建镜像================================="
docker build -t beego:01 .

# 停止并删除容器
echo "========================================正在删除容器================================="
docker stop beego
docker rm beego

# 运行容器
echo "========================================正在创建新容器================================="
docker run --name beego -v /develop/work/beego-mbook/mbook:/go/src/demo -p 8880:8880 -d beego:01
```



```bash
chmod u+x script.sh # 赋予脚本执行权限
```



## 执行script脚本

启动容器

```bash
./script.sh
```

