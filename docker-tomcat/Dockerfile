# 基础镜像
FROM tomcat:9.0.86-jdk8-temurin-focal
# 作者信息
MAINTAINER Daishuai
# 定义变量，后续会使用，具体路径
ENV DIR_WEBAPP /usr/local/tomcat/webapps/
# 删除webapp下的所有文件，因为当前应用作为根应用
RUN rm -rf $DIR_WEBAPP/*
# 添加本地的war包到远程容器
ADD ./target/docker-tomcat.war $DIR_WEBAPP/ROOT.war
# 配置文件夹映射
VOLUME /usr/local/tomcat/webapps
# 配置工作目录
WORKDIR /usr/local/tomcat/webapps
# 安装 unzip
RUN apt-get update && apt-get install -y unzip
# 解压war包到ROOT目录下
RUN unzip $DIR_WEBAPP/ROOT.war -d $DIR_WEBAPP/ROOT/
# 暴露端口
EXPOSE 8080
# 启动tomcat
CMD ["catalina.sh", "run"]