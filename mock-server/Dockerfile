FROM openjdk:8-jdk-alpine

# 安装必要的包，包括 bash 和 curl
RUN apk add --no-cache fontconfig bash curl

# 拷贝 jar 包
COPY ./target/mock-service-1.0.0.jar /app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 更新字体
RUN apk add --update font-adobe-100dpi ttf-dejavu fontconfig

EXPOSE 8080

# 设置入口点
ENTRYPOINT ["java","-jar","/app.jar"]
