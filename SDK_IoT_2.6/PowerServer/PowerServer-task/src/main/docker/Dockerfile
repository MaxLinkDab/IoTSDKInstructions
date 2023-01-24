FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD PowerServer-task.jar app.jar
RUN sh -c 'touch /app.jar'
RUN mkdir /agent
COPY agent /agent

EXPOSE 8080

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
