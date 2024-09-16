#这是基础镜像

FROM openjdk:8

# 解决容器内时区不一致问题
ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp

#复制jar包到镜像中，并且将名字改成app.jar

ADD ./target/docker-jar-1.0.0-RELEASE.jar DemoApp.jar

#在容器启动的时候运行命令，来启动我们的项目（这其实就是一段Linux命令,该命令可以在服务启动时加一些参数）

ENTRYPOINT ["sh", "-c", "java -jar DemoApp.jar"]