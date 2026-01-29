# 🛒 Order Management Microservice

![Java](https://img.shields.io/badge/Java-21-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Build](https://img.shields.io/badge/Build-Maven-success)
![Tests](https://img.shields.io/badge/Test%20Coverage-~100%25-success)
![License](https://img.shields.io/badge/License-MIT-blue)

A clean, maintainable **Spring Boot microservice** for managing **Products** and **Orders**, implemented as part of a technical challenge.  
The project demonstrates modern Java practices, layered architecture, secure API design, and a strong focus on testability and maintainability.

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** (leverages modern JVM features)
- **Maven 3.x** or the included **Maven Wrapper** (`./mvnw`)

---

### 🔧 Build & Run

1. **Clone the repository**

   ```bash
   git clone https://github.com/hannovdw/pollinate-assessment.git
   cd challenge
   
2. **Build the project**

   ```bash
   ./mvnw clean install
      
3. **Run the project**

   ```bash
   ./mvnw spring-boot:run

## 🔐 Access & Credentials

The application is secured using **Spring Security Basic Authentication**.

### Authentication
- **Username:** `admin`
- **Password:** `password`

### Public Endpoints
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console
- **Actuator Health:** http://localhost:8080/actuator/health
- **Actuator Metrics:** http://localhost:8080/actuator/metrics

### H2 Database Access
- **Driver Class Name:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:mem:orderdb`
- **Username:** `sa`
- **Password:** _(none)_

## 🏗 Architecture & Design Decisions

### Layered Architecture

The application follows a **classic layered architecture** to promote clarity, testability, and long-term maintainability.

- **Controller Layer**
    - Responsible for HTTP request handling and API contracts (DTOs)
    - Performs input validation and delegates business logic to the service layer

- **Service Layer**
    - Encapsulates all core business logic
    - Handles validation, pricing rules, and transactional boundaries
    - Designed to be framework-agnostic and highly unit-testable

- **Repository Layer**
    - Manages persistence using **Spring Data JPA**
    - Abstracts database interactions away from business logic

This structure ensures a clear separation of concerns and aligns with Spring Boot best practices for microservices.

---

### Domain Model & Persistence

- **Many-to-Many Relationship**
    - An **Order** can contain multiple **Products**
    - A **Product** can exist in multiple **Orders**
    - Implemented via a join table: `order_products`

- **Monetary Precision**
    - All price-related fields use **`BigDecimal`**
    - Prevents rounding and precision issues inherent in `double` and `float`

---
## 🔐 Security

The application is secured using **Spring Security** with **HTTP Basic Authentication**, as required by the technical challenge.

### Authentication & Authorization

- **HTTP Basic Authentication** is enforced for all business-related API endpoints
- A dedicated `SecurityFilterChain` configuration defines access rules explicitly
- Credentials are defined in-memory for simplicity and clarity within the challenge scope

### Endpoint Access Rules

- **Publicly accessible endpoints**
    - Swagger UI and OpenAPI documentation
    - H2 Console (development-only)
    - Actuator Health endpoint

- **Secured endpoints**
    - All Product and Order management APIs

### Security Trade-offs

- Basic Authentication was chosen to meet the challenge requirements and keep the implementation focused
- In a production environment, this would typically be replaced with:
    - OAuth2 / OpenID Connect
    - JWT-based authentication
    - External identity providers

---

## 📊 Observability

The application integrates **Spring Boot Actuator** to provide insight into runtime health and operational metrics, demonstrating production-aware design.

### Actuator Integration

- **Spring Boot Actuator** is enabled to expose operational endpoints
- Provides visibility into:
    - Application health
    - JVM and system metrics
    - Resource usage

---

## 🧪 Testing Strategy

The project emphasizes a strong testing discipline to ensure correctness, maintainability, and confidence in behavior.  
Business logic is covered extensively, achieving **near-100% test coverage** for core services.

### Unit Tests

- Focused on validating business rules in isolation
- Implemented using **JUnit 5** and **Mockito**
- Service-layer tests include:
    - `OrderService`
    - `ProductService`
- Covers edge cases such as:
    - Missing or invalid products
    - Pricing and total calculation logic
    - Validation failures

### Integration Tests

- Implemented using `@SpringBootTest` and **MockMvc**
- Validate the application end-to-end, including:
    - Spring Security filter configuration
    - REST controller mappings and DTO serialization
    - Persistence with the in-memory **H2** database

### Test Execution

Run the full test suite with:

```bash
./mvnw test
