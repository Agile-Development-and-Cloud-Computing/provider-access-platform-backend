## Provider Access Platform – Backend

### Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Directory Structure](#directory-structure)
4. [Setup Instructions](#setup-instructions)
5. [Technologies Used](#technologies-used)
6. [Development Process](#development-process)
7. [Deployment](#deployment)
8. [Contributing](#contributing)
9. [License](#license)

---

## Project Overview

The **Provider Access Platform** backend is a core component of a cloud-ready provider management system. Built using **Java Spring Boot**, the backend handles secure authentication, REST API endpoints, and persistent data storage, designed to interact seamlessly with microservices and frontend applications.

The platform follows **MVC architecture** and uses **JWT** for access control. It supports a multi-role system and exposes RESTful APIs to handle provider-user interactions, service lifecycle, and order processing.

---

## Features

* **Spring Boot REST API** with secure, scalable endpoints
* **JWT Authentication & Role-Based Authorization**
* **MySQL Integration** with efficient query design
* **MVC Architecture** for modular and maintainable code
* **Service Management**: Orders, Requests, and Offers
* **CI/CD Pipeline Ready**: GitHub Actions for automated deployment
* **Structured Logging** for easier debugging and traceability
* **Dockerized Microservice**: Container-ready with Azure deployment support

---

## Directory Structure

```
src/
├── main/
│   ├── java/com/fuas/provider_access_platform/
│   │   ├── config/              # Security and application configurations
│   │   ├── controller/          # REST API controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # Entity classes and domain models
│   │   ├── repository/          # JPA repositories for data access
│   │   ├── security/            # Security configuration (JWT, filters, etc.)
│   │   ├── service/             # Business logic and service layer
│   │   └── BackendApplication.java  # Main Spring Boot application
│   └── resources/
│       ├── application.properties # Environment and DB configuration



```

---

## Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/Agile-Development-and-Cloud-Computing/provider-access-platform-backend.git
   cd provider-access-platform-backend
   ```

2. **Configure the database**
   Update your `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/providerdb
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   ```

3. **Run the application**
   You can run it directly via Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Test APIs**
   Use tools like **Postman** or **Swagger UI** to test the endpoints.

---

## Technologies Used

* **Java 21**
* **Spring Boot**
* **Spring Data JPA**
* **MySQL**
* **JWT (JSON Web Token)**
* **Docker & Azure Container Registry**
* **GitHub Actions (CI/CD)**
* **Postman** for API documentation
* **Logback** for structured application logging

---

## Development Process

The backend was developed following **Agile principles** with multiple iterations and code reviews. Key development practices:

* **Secure by Design**: Authentication and authorization integrated early
* **Modular Services**: Independent components with clear responsibilities
* **Cloud-Ready**: Dockerized for deployment in Azure via Container Instances
* **Unit Testing**: Implemented using JUnit for critical business logic

---

## Deployment

The backend service is containerized using **Docker**, stored in **Azure Container Registry**, and deployed using **Azure Container Instances**. CI/CD pipelines are managed via **GitHub Actions** for automatic testing and deployment.

---

## License

This project is a university project under the Agile Development and Cloud Computing module. Distributed for educational purposes only.

---
