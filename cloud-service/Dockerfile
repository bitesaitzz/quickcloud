FROM openjdk:21-jdk-slim
WORKDIR /quickcloud
COPY target/CloudService-0.0.1-SNAPSHOT.jar /quickcloud
COPY src/main/resources/application-dev.properties /quickcloud/config/
EXPOSE 8081
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver \
    SPRING_PROFILES_ACTIVE=dev \
    SPRING_CONFIG_LOCATION=classpath:/,file:/quickcloud/config/ \
    SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect \
    SPRING_JPA_HIBERNATE_DDL_AUTO=none \
    SPRING_JPA_SHOW_SQL=true \
    SPRING_MVC_HIDDENMETHOD_FILTER_ENABLED=true \
    SPRING_CACHE_TYPE=redis \
    SPRING_LIQUIBASE_ENABLED=false \
#    SPRING_LIQUIBASE_CHANGE_LOG=classpath:db/changelog/changelog-master.xml \
    SPRING_RABBITMQ_USERNAME=guest \
    SPRING_RABBITMQ_PASSWORD=guest \
    SERVER_PORT=8081 \
    SPRING_APPLICATION_NAME=cloud-service \
    QUEUE_NAME=message-queue

ARG DB_URL
ARG DB_USER
ARG DB_PASSWORD
ENV SPRING_DATASOURCE_URL=$DB_URL
ENV SPRING_DATASOURCE_USERNAME=$DB_USER
ENV SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD

RUN mkdir -p /quickcloud/uploads
ENTRYPOINT ["java", "-jar", "CloudService-0.0.1-SNAPSHOT.jar"]