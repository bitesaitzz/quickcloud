FROM openjdk:21-jdk-slim
WORKDIR /quickcloud
COPY target/NotificationService-0.0.1-SNAPSHOT.jar /quickcloud
COPY wait_for.sh /wait_for.sh
EXPOSE 8082

RUN chmod +x /wait_for.sh
CMD ["/wait_for.sh", "cloud-service:8081", "--","java", "-jar", "NotificationService-0.0.1-SNAPSHOT.jar"]