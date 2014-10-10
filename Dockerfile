# Written against Docker v1.2.0
FROM dockerfile/java
MAINTAINER Chris Rebert <code@rebertia.com>

WORKDIR /
USER daemon

ADD target/scala-2.10/rorschach-assembly-1.0.jar /app/server.jar

CMD ["java", "-jar", "/app/server.jar", "9090"]
EXPOSE 9090
