# yu-tools

一个工具性的java服务

<br>

java启动命令：

```
java -jar -DjellyfinUrl=http://127.0.0.1:8096 -DjellyfinApiToken=xxxxxx yu-tools.jar
```

docker启动命令（将jar包放至宿主机"/opt/jar/"目录下）：

```
docker run -d \
 --name=yu-tools \
 --restart unless-stopped \
 -p 8688:8688 \
 -e TZ=Asia/Shanghai \
 -v /opt/jar/yu-tools.jar:/opt/jar/yu-tools.jar \
 java:8u111 java \
  -DjellyfinUrl={url} \
  -DjellyfinApiToken={apiToken} \
  -jar /opt/jar/yu-tools.jar
```

<br>
参数说明：

| 参数               | 是否必填 | 说明             | 示例                             |
| ------------------ | -------- | ---------------- | -------------------------------- |
| -DjellyfinUrl      | 否       | jellyfin访问地址 | http://127.0.0.1:8096            |
| -DjellyfinApiToken | 否       | jellyfin API密钥 | 2csa00a4a10f33aca16a1cse38c4d9c7 |

<br>

部署完成验证：

访问：http://127.0.0.1:8688/hello/world

<br>

现有功能：

1.刷新jellyfin媒体库，访问：http://127.0.0.1:8688/jellyfin/media/refresh

<br>

待更新（有需要的功能欢迎提宝贵建议）...
