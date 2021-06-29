FROM openjdk:8-jdk-alpine
MAINTAINER jhzeg
COPY target/nld-bot-0.0.1-SNAPSHOT.jar nld-bot-1.0.0.jar
ENTRYPOINT ["java","-jar","/nld-bot-1.0.0.jar"]
