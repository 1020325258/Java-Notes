

# 1、Linux常用命令



- 查看占用端口

```bash
yum -y install net-tools # 安装netstat命令
netstat -tunlp|grep {port}
```

- 杀死进程

```bash
kill -9 {PID}
```

