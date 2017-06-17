FROM centos:7.3.1611

ENV PACKAGE_DEPS='java-1.8.0-openjdk.x86_64'

RUN yum -y update \
  && yum -y install $PACKAGE_DEPS \
  && yum clean all

RUN mkdir -p /app && mkdir -p /app/log && chown -R nobody:nobody /app

ENV PS1='\u@\h$ '

ENV JAVA_OPTS='-Xms1024m -Xmx1024m -XX:+UseG1GC'
ENV APP_JAR='/app/service-exec.jar'

COPY target/quality-1.0-SNAPSHOT.jar $APP_JAR

RUN chown -R nobody:nobody /app

USER nobody
WORKDIR /app

CMD /usr/bin/java $JAVA_OPTS -jar $APP_JAR
