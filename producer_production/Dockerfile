FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml --batch-mode --update-snapshots package -DskipTests

FROM openjdk:17

COPY --from=build /app/target/producer-production-0.0.1-SNAPSHOT.jar ./producer-production-0.0.1-SNAPSHOT.jar

COPY --from=build /app/src/main/resources/application.properties ./application.properties

COPY --from=build /app/src/main/resources/loadprofiles loadprofiles

COPY --from=build /app/target/lib lib

ENV JAVA_OPTS=""

CMD java -jar producer-production-0.0.1-SNAPSHOT.jar $JAVA_OPTS