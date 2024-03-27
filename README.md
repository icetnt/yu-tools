# yu-tools

一个NAS辅助工具性的java服务

<br>

### java启动命令：
```
java -jar -DjfUrl={url} -DjfToken={apiToken} -DmpUrl={mpUrl} -DmpUser={mpUser} -DmpPwd={mpPwd} \
-DqbUrl={qbUrl} -DqbUser={qbUser} -DqbPwd={qbPwd} -DqbUMin={qbUMin} -DqbUMax={qbUMax} -DqbUMPU={qbUMPU} yu-tools.jar
```

### docker启动命令（将jar包放至宿主机"/opt/jar/"目录下）：
```
docker run -d \
 --name=yu-tools \
 --restart unless-stopped \
 -p 8688:8688 \
 -e TZ=Asia/Shanghai \
 -v /opt/jar/yu-tools.jar:/opt/jar/yu-tools.jar \
 java:8u111 java \
  -DjfUrl={url} \
  -DjfToken={apiToken} \
  -DmpUrl={mpUrl} \
  -DmpUser={mpUser} \
  -DmpPwd={mpPwd} \
  -DqbUrl={qbUrl} \
  -DqbUser={qbUser} \
  -DqbPwd={qbPwd} \
  -DqbUMin={qbUMin} \
  -DqbUMax={qbUMax} \
  -DqbUMPU={qbUMPU} \
  -jar /opt/jar/yu-tools.jar
```

### 参数说明：
| 参数               | 是否必填 | 说明             | 示例                             |
| ------------------ | -------- | ---------------- | -------------------------------- |
| -DjfUrl  | 否       | jellyfin 访问地址     | http://127.0.0.1:8096            |
| -DjfToken| 否       | jellyfin API密钥     | 2asa10a4a10123aca16a13sec8c4d1v6 |
| -DmpUrl  | 否       | MoviePilot API地址   | http://127.0.0.1:3001            |
| -DmpUser | 否       | MoviePilot 登录用户名 | admin |
| -DmpPwd  | 否       | MoviePilot 登录密码   | adminpassword |
| -DqbUrl  | 否       | qBittorrent 访问地址    | http://127.0.0.1:8080            |
| -DqbUser | 否       | qBittorrent 登录用户名 | admin |
| -DqbPwd  | 否       | qBittorrent 登录密码   | adminpassword |
| -DqbUMin  | 否       | qBittorrent 上传限速最小值（单位MB/s）   | 10 |
| -DqbUMax  | 否       | qBittorrent 上传限速最大值（单位MB/s）   | 0.5 |
| -DqbUMPU  | 否       | jellyfin 在线观看单用户占用上传带宽（单位MB/s）   | 2 |


### 部署完成验证：
访问：http://127.0.0.1:8688/hello/world


### 现有功能：
1.刷新jellyfin媒体库，访问：http://127.0.0.1:8688/jellyfin/media/refresh
<br>
2.MoviePilot下载完成后自动刷新jellyfin媒体库<br>
(MoviePilot需安装webhook插件，配置POST请求，配置本服务webhook地址：http://127.0.0.1:8688/webhook/mp)
<br>![img_3.png](img/img_3.png)![img_4.png](img/img_4.png)
<br>
3.jellyfin有在线用户观看时，自动更新qBittorrent上传限速<br>
(jellyfin需安装webhook插件，NotificationType项至少勾选"Playback Progress"，配置本服务webhook地址：http://127.0.0.1:8688/webhook/jf)
<br>
![img_1.png](img/img_1.png)![img_2.png](img/img_2.png)
<br>

### TODO：
1.自动限速功能排除本地用户

<br>
待更新（有需要的功能欢迎提宝贵建议）...
