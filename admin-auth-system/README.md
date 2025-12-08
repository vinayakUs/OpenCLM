# Admin Authentication System

This project is a Minimal Production-Ready Authentication System with Admin-only access.

## Tech Stack
- **Frontend**: Angular (Latest)
- **Backend Services**:
  - **Gateway Service** (Port 8080): Spring Cloud Gateway
  - **Auth Server** (Port 9000): Spring Boot + JWT
  - **BFF Service** (Port 8082): Spring Boot + Resource Server
- **Database**: PostgreSQL (Docker)

## Prerequisites
- Docker & Docker Compose
- Java 17+ (JDK)
- Maven
- Node.js & npm

## Setup Instructions

### 1. Start Database
```bash
docker compose up -d
```
Ensure PostgreSQL is running on port 5432.

### 2. Build and Run Services
Open 3 separate terminals for the backend services.

**Auth Server:**
```bash
cd auth-server
mvn spring-boot:run
```

**Gateway Service:**
```bash
cd gateway-service
mvn spring-boot:run
```

**BFF Service:**
```bash
cd bff-service
mvn spring-boot:run
```

### 3. Run Frontend
```bash
cd frontend
npm install
ng serve
```
Access the app at `http://localhost:4200`.

## Architecture Flow
1. **Signup**: Angular -> Gateway -> Auth Server (Creates User + Assigns ROLE_ADMIN).
2. **Login**: Angular -> Gateway -> Auth Server (Returns JWT Access Token).
3. **Home Page**: Angular -> Gateway (Validates JWT) -> BFF (Checks Permissions) -> Returns Data.

## Postman Testing
Import the `postman_collection.json` file into Postman to test the APIs directly via the Gateway.
