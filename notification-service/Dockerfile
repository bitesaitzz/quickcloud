FROM openjdk:21-jdk-slim
WORKDIR /quickcloud
COPY target/NotificationService-0.0.1-SNAPSHOT.jar /quickcloud
COPY wait_for.sh /wait_for.sh
COPY src/main/resources/application-dev.properties /quickcloud/config/

ENV SPRING_PROFILES_ACTIVE=dev \
        SPRING_CONFIG_LOCATION=classpath:/,file:/quickcloud/config/
EXPOSE 8082
ARG SENDGRID_API_KEY
ARG SPRING_MAIL_USERNAME
ARG SPRING_MAIL_PASSWORD

ENV SENDGRID_API_KEY=$SENDGRID_API_KEY
ENV SPRING_MAIL_USERNAME=$SPRING_MAIL_USERNAME
ENV SPRING_MAIL_PASSWORD=$SPRING_MAIL_PASSWORD

RUN chmod +x /wait_for.sh
CMD ["/wait_for.sh", "cloud-service:8081", "--","java", "-jar", "NotificationService-0.0.1-SNAPSHOT.jar"]