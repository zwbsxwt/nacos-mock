FROM openjdk:8-jdk-alpine

# 安装 fontconfig 包
RUN apk add --no-cache fontconfig

# 拷贝 jar 包
COPY ./target/eladmin-system-1.1.jar /app/app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 更新字体
RUN apk add --update font-adobe-100dpi ttf-dejavu fontconfig

# 设置入口点
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=dev"]