FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml --batch-mode --update-snapshots verify -DskipTests

FROM openjdk

COPY --from=build /app/target/ems-actuator-0.0.1-SNAPSHOT.jar ./ems-actuator-0.0.1-SNAPSHOT.jar

COPY --from=build /app/src/main/resources/application-docker.yml ./application.yml

CMD java -jar ems-actuator-0.0.1-SNAPSHOT.jar --spring.config.location=application.yml