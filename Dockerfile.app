FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src
COPY web ./web

RUN mvn clean package -DskipTests

FROM tomcat:9.0.96-jdk17

ENV CATALINA_OPTS="-Dserver.port=8080"
ENV JAVA_OPTS="-Djava.awt.headless=true"

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080 8443

CMD ["catalina.sh", "run"]