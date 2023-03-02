FROM openjdk:19-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ./
ENTRYPOINT ["java","-jar","/app.jar"]