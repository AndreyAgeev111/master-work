FROM openjdk:21-jdk-slim

EXPOSE 8080

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]