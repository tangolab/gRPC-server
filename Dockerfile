FROM adoptopenjdk/openjdk11
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/jobs-0.0.1-SNAPSHOT.jar jobs.jar
EXPOSE 8002
ENTRYPOINT exec java $JAVA_OPTS -jar jobs.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar jobs.jar
