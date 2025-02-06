FROM maven:3.8.5-openjdk-17

WORKDIR /driver

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean
RUN mvn package -DskipTests

FROM openjdk:17-jdk

COPY /target/driver-microservice-0.0.1-SNAPSHOT.jar /driver/launch-driver.jar

ENTRYPOINT ["java","-jar","/driver/launch-driver.jar"]

EXPOSE 8082