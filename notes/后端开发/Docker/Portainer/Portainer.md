# 1、Portainer使用

官网 https://www.portainer.io

使用：

```bash
docker run -d -p 8000:8000 -p 9000:9000 --name=portainer --restart=always -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data_portain --privileged=true portainer/portainer # 8000端口用于容器通信，9000端口用于web访问
```

