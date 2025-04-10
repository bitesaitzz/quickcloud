FROM openjdk:22-jdk-slim
WORKDIR /quickcloud
COPY target/CloudService-0.0.1-SNAPSHOT.jar /quickcloud
EXPOSE 8081
RUN mkdir -p /quickcloud/uploads
ENTRYPOINT ["java", "-jar", "CloudService-0.0.1-SNAPSHOT.jar"]