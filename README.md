# 项目结构



## mock-console

后端管理接口,改的eladmin



## mock-console-ui

UI界面



## mock-server

mockServer服务代码



## docker-compose.zip

docker配置文件及数据







# 使用方式



安装docker以及docker-compose组件



`ubuntu`为例

```
# 一键化指令
sudo -i 

# docker
apt update -y && \
apt install docker.io -y && \
apt install docker -y 

# 检查版本
docker -v

# docker-compose
git clone https://gitee.com/zwbsxwt/docker-compose.git && \
cd docker-compose && \
mv docker-compose-linux-x86_64 /usr/local/bin/docker-compose && \
chmod +x /usr/local/bin/docker-compose

# 检查版本
docker-compose -v
```





```agsl
cd docker

# 解压`docker-compose.zip`
unzip docker-compose.zip

cd mock

docker-compose up -d
```



