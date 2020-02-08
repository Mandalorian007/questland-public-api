FROM adoptopenjdk/openjdk11:latest
MAINTAINER Mandalorian007

COPY ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=production","/app.jar"]