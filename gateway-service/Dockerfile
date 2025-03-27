FROM openjdk:21-jdk-slim
WORKDIR /quickcloud
COPY target/GatewayService-0.0.1-SNAPSHOT.jar /quickcloud
EXPOSE 80
ENV CLOUD_SERVICE_URI=http://cloud-service:8081
CMD ["java", "-jar", "GatewayService-0.0.1-SNAPSHOT.jar"]

