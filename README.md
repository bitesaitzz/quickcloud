# QuickCloud

**quickcloud.xyz**

QuickCloud is a cloud service that allows users to upload files and information to the cloud and receive a unique code for future access to this data from any device.

## Architecture

The project consists of three microservices:

- **Cloud Service**: The main service that handles data storage and access logic.
- **Notification Service**: A service for sending access codes to users via email (with future support for Telegram notifications).
- **Gateway Service**: A gateway service that manages request routing between microservices.

## Technology Stack

- **Backend**: Java (Spring Boot)
- **Database**: PostgreSQL
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes (deployed on DigitalOcean)
- **CI/CD**: Jenkins
- **Caching and Request Control**: Redis
- **Message Queues**: RabbitMQ