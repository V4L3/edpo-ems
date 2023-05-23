FROM openjdk:18-alpine
VOLUME /tmp
ADD target/*.jar app.jar
COPY src/main/resources/loadprofiles app/loadprofiles
COPY target/lib lib
ADD src/main/resources/application.properties app/resources/application.properties
ENV JAVA_OPTS=""
ENTRYPOINT exec java -jar /app.jar $JAVA_OPTS