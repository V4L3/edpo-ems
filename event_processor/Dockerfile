FROM maven as build

COPY . /app

RUN mvn -f app/pom.xml --batch-mode --update-snapshots package -DskipTests

FROM openjdk:18

COPY --from=build /app/target/ems-event-processor-1.0.0-SNAPSHOT.jar ./ems-event-processor-1.0.0-SNAPSHOT.jar

COPY --from=build /app/target/lib lib

ENV JAVA_OPTS=""

CMD java -jar ems-event-processor-1.0.0-SNAPSHOT.jar $JAVA_OPTS