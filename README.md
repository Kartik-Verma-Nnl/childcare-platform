<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Apache%20Kafka-7.4-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka"/>
  <img src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</p>

<p align="center">
  <a href="https://github.com/Kartik-Verma-Nnl/childcare-platform/actions/workflows/build.yml">
    <img src="https://github.com/Kartik-Verma-Nnl/childcare-platform/actions/workflows/build.yml/badge.svg" alt="Build Status"/>
  </a>
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License"/>
</p>

# 🧒 Childcare Platform — Backend API

A **production-grade** evening childcare matching platform built with **Spring Boot**. It connects **parents** looking for trusted evening childcare with **verified caregivers**, powered by real-time chat, event-driven notifications, and a full observability stack.

---

## 📑 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Screenshots](#-screenshots)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Observability](#-observability)
- [CI/CD](#-cicd)
- [Project Structure](#-project-structure)
- [Environment Variables](#-environment-variables)
- [Contributing](#-contributing)

---

## ✨ Features

| Category | Details |
|---|---|
| **🔐 Authentication** | JWT-based stateless auth with BCrypt password hashing. Role-based access control (Parent / Caregiver / Admin) |
| **👶 Booking System** | Full booking lifecycle — create, accept, cancel, complete. Live session status tracking (NOT_STARTED → IN_PROGRESS → COMPLETED) |
| **🔍 Caregiver Search** | Parents can search caregivers by **city** and **date** with paginated results |
| **💬 Real-Time Chat** | WebSocket (STOMP) powered chat between parent and caregiver per booking |
| **⭐ Reviews & Ratings** | Parents can review caregivers after completed bookings. Auto-computed average rating |
| **📧 Email Notifications** | Kafka-driven async email notifications on booking events (created, confirmed, cancelled, completed) |
| **🛡️ Admin Panel** | Verify/unverify caregivers, view all users and bookings with pagination |
| **⚡ Caching** | Redis caching for caregiver listings to reduce DB load |
| **📊 Observability** | Full-stack monitoring: Prometheus + Grafana (metrics), ELK (logs), Jaeger + OpenTelemetry (traces) |
| **📖 API Documentation** | Interactive Swagger UI with OpenAPI 3.0 spec and JWT auth support |

---

## 🛠 Tech Stack

### Core
| Technology | Purpose |
|---|---|
| **Spring Boot 3.2.5** | Application framework |
| **Java 17** | Language runtime |
| **Spring Security** | Authentication & authorization |
| **Spring Data JPA** | Database ORM |
| **Spring Validation** | Request validation |
| **Lombok** | Boilerplate reduction |

### Data & Messaging
| Technology | Purpose |
|---|---|
| **PostgreSQL 15** | Primary relational database |
| **Redis 7** | Caching layer |
| **Apache Kafka** | Event-driven messaging for booking notifications |
| **Spring WebSocket (STOMP)** | Real-time bidirectional chat |

### Observability
| Technology | Purpose |
|---|---|
| **Prometheus** | Metrics collection |
| **Grafana** | Metrics visualization & dashboards |
| **Elasticsearch** | Log storage & indexing |
| **Kibana** | Log visualization & search |
| **Filebeat** | Log shipping to Elasticsearch |
| **Jaeger** | Distributed tracing UI |
| **OpenTelemetry** | Auto-instrumentation for traces |
| **Micrometer** | Metrics bridge for Spring Boot |

### DevOps
| Technology | Purpose |
|---|---|
| **Docker & Docker Compose** | Containerized deployment |
| **GitHub Actions** | CI/CD pipeline |
| **SpringDoc OpenAPI** | Swagger UI & API docs |

---

## 🏗 Architecture

```
┌─────────────┐  ┌──────────────┐  ┌───────────────┐
│  Parent App  │  │ Caregiver App│  │ Admin Dashboard│
└──────┬───────┘  └──────┬───────┘  └──────┬────────┘
       │                 │                  │
       └────────────┬────┴──────────────────┘
                    │
           ┌────────▼────────┐
           │  Spring Boot    │
           │   REST API      │──── JWT Authentication
           │  (Port 8080)    │──── WebSocket (STOMP)
           └───┬──────┬──────┘
               │      │
    ┌──────────┤      ├──────────┐
    │          │      │          │
┌───▼───┐ ┌───▼───┐ ┌▼──────┐ ┌─▼────────┐
│Postgre│ │ Redis │ │ Kafka │ │ Email    │
│  SQL   │ │ Cache │ │       │ │ (SMTP)  │
└────────┘ └───────┘ └───────┘ └──────────┘

          ┌─── Observability Stack ───┐
          │                           │
  ┌───────┴───┐  ┌──────────┐  ┌─────┴──────┐
  │Prometheus │  │   ELK    │  │  Jaeger +  │
  │ + Grafana │  │  Stack   │  │ OpenTelemetry│
  └───────────┘  └──────────┘  └────────────┘
```

---

## 📸 Screenshots

### Swagger UI — Interactive API Documentation
> Access at `http://localhost:8080/swagger-ui/index.html`

![Swagger UI](docs/images/swagger-ui.png)

### Grafana — Metrics Dashboards
> Access at `http://localhost:3000` (admin/admin)

![Grafana Dashboard](docs/images/grafana-dashboard.png)

### Prometheus — Metrics Query Engine
> Access at `http://localhost:9090`

![Prometheus UI](docs/images/prometheus-ui.png)

### Jaeger — Distributed Tracing
> Access at `http://localhost:16686`

![Jaeger UI](docs/images/jaeger-ui.png)

### Kibana — Log Analytics
> Access at `http://localhost:5601`

![Kibana Dashboard](docs/images/kibana-dashboard.png)

---

## 🚀 Getting Started

### Prerequisites

- **Docker** & **Docker Compose** installed
- **Git** installed

### Quick Start (One Command)

```bash
# Clone the repository
git clone https://github.com/Kartik-Verma-Nnl/childcare-platform.git
cd childcare-platform

# Start everything — builds the app + all infrastructure
docker compose up -d --build
```

This single command spins up **11 containers**:

| Container | Port | Description |
|---|---|---|
| `childcare-app` | `8080` | Spring Boot application |
| `childcare-postgres` | `5432` | PostgreSQL database |
| `childcare-redis` | `6379` | Redis cache |
| `childcare-kafka` | `9092` | Kafka broker |
| `childcare-zookeeper` | `2181` | Kafka Zookeeper |
| `childcare-prometheus` | `9090` | Prometheus metrics |
| `childcare-grafana` | `3000` | Grafana dashboards |
| `childcare-elasticsearch` | `9200` | Elasticsearch |
| `childcare-kibana` | `5601` | Kibana log viewer |
| `childcare-filebeat` | — | Log shipper |
| `childcare-jaeger` | `16686` | Jaeger tracing UI |

### Verify

```bash
# Check all containers are running
docker ps

# Test the API
curl http://localhost:8080/swagger-ui/index.html
```

### Local Development (Without Docker)

```bash
# 1. Start infrastructure services
docker compose up -d postgres redis kafka zookeeper

# 2. Create application.yaml from example
cp src/main/resources/application-example.yml src/main/resources/application.yaml
# Edit with your DB credentials, mail settings, and JWT secret

# 3. Run the application
./mvnw spring-boot:run
```

---

## 📡 API Endpoints

### 🔓 Authentication (`/api/auth`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user (Parent/Caregiver) | ❌ |
| `POST` | `/api/auth/login` | Login & receive JWT token | ❌ |

**Register Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePass123",
  "phone": "+1234567890",
  "role": "PARENT"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "PARENT",
  "name": "John Doe"
}
```

---

### 👶 Bookings (`/api/bookings`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/bookings` | Create a new booking | 🔒 Parent |
| `GET` | `/api/bookings/my` | View my bookings as parent | 🔒 Parent |
| `GET` | `/api/bookings/caregiver/my` | View my bookings as caregiver | 🔒 Caregiver |
| `PUT` | `/api/bookings/{id}/accept` | Accept a booking | 🔒 Caregiver |
| `PUT` | `/api/bookings/{id}/cancel` | Cancel a booking | 🔒 Parent/Caregiver |
| `PUT` | `/api/bookings/{id}/complete` | Mark booking complete | 🔒 Caregiver |
| `PUT` | `/api/bookings/{id}/session-status` | Update live session status | 🔒 Caregiver |

---

### 👩‍⚕️ Caregivers (`/api/caregiver`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/caregiver` | List all verified caregivers (paginated) | ❌ |
| `GET` | `/api/caregiver/all` | List all verified caregivers | ❌ |
| `GET` | `/api/caregiver/{id}` | Get caregiver profile | ❌ |
| `PUT` | `/api/caregiver/profile` | Update own profile | 🔒 Caregiver |
| `POST` | `/api/caregiver/slots` | Add availability slot | 🔒 Caregiver |
| `GET` | `/api/caregiver/slots/my` | View own slots | 🔒 Caregiver |
| `DELETE` | `/api/caregiver/slots/{id}` | Delete a slot | 🔒 Caregiver |

---

### 🔍 Parent Search (`/api/parent`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/parent/search?city={city}&date={date}` | Search caregivers by city & date | 🔒 Parent |

---

### ⭐ Reviews (`/api/reviews`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/reviews` | Submit a review for a caregiver | 🔒 Parent |
| `GET` | `/api/reviews/caregiver/{id}` | View all reviews for a caregiver | ❌ |

---

### 💬 Chat (`/api/chat` + WebSocket)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/chat/{bookingId}` | Get chat history for a booking | 🔒 |
| WS | `/app/chat.send` | Send a message (STOMP) | 🔒 |
| WS | `/topic/chat/{bookingId}` | Subscribe to booking chat (STOMP) | 🔒 |

---

### 🛡️ Admin (`/api/admin`)

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/admin/caregivers/unverified` | List unverified caregivers | 🔒 Admin |
| `PUT` | `/api/admin/caregivers/{id}/verify` | Verify a caregiver | 🔒 Admin |
| `PUT` | `/api/admin/caregivers/{id}/unverify` | Revoke verification | 🔒 Admin |
| `GET` | `/api/admin/bookings` | All bookings (paginated) | 🔒 Admin |
| `GET` | `/api/admin/bookings/all` | All bookings | 🔒 Admin |
| `GET` | `/api/admin/users` | All users (paginated) | 🔒 Admin |
| `GET` | `/api/admin/users/all` | All users | 🔒 Admin |

---

## 🗄 Database Schema

```
┌──────────────────┐       ┌──────────────────────┐
│      users       │       │  caregiver_profiles   │
├──────────────────┤       ├──────────────────────┤
│ id (PK)          │──1:1──│ id (PK)              │
│ name             │       │ user_id (FK)         │
│ email (UNIQUE)   │       │ bio                  │
│ password         │       │ hourly_rates         │
│ phone            │       │ experience_years     │
│ role (ENUM)      │       │ specializations      │
│ created_at       │       │ is_verified          │
└──────────────────┘       │ city                 │
        │                  │ average_rating       │
        │                  │ doc_url              │
        │ 1:*              └──────────┬───────────┘
        │                             │ 1:*
┌───────▼──────────┐       ┌──────────▼───────────┐
│    bookings      │       │ availability_slots    │
├──────────────────┤       ├──────────────────────┤
│ id (PK)          │       │ id (PK)              │
│ parent_id (FK)   │       │ caregiver_id (FK)    │
│ caregiver_id (FK)│       │ date                 │
│ slot_id (FK)     │       │ start_time           │
│ status (ENUM)    │       │ end_time             │
│ session_status   │       │ is_booked            │
│ duration_hours   │       └──────────────────────┘
│ total_amount     │
│ notes            │
│ created_at       │
└───────┬──────────┘
        │ 1:1          1:*
┌───────▼──────────┐  ┌──────────────────┐
│    reviews       │  │  chat_messages   │
├──────────────────┤  ├──────────────────┤
│ id (PK)          │  │ id (PK)          │
│ booking_id (FK)  │  │ booking_id (FK)  │
│ caregiver_id (FK)│  │ sender_id (FK)   │
│ parent_id (FK)   │  │ content          │
│ rating           │  │ timestamp        │
│ comment          │  └──────────────────┘
│ created_at       │
└──────────────────┘
```

### Enums

| Enum | Values |
|---|---|
| **Role** | `PARENT`, `CAREGIVER`, `ADMIN` |
| **BookingStatus** | `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED` |
| **SessionStatus** | `NOT_STARTED`, `IN_PROGRESS`, `COMPLETED` |

---

## 📊 Observability

The platform comes with a **full observability stack** out of the box:

### Metrics — Prometheus + Grafana
- **Prometheus** scrapes Spring Boot Actuator metrics every 5 seconds
- **Grafana** provides customizable dashboards
- Metrics exposed at `/actuator/prometheus`
- JVM metrics, HTTP request metrics, custom business metrics

### Logs — ELK Stack
- **Filebeat** ships structured JSON logs to **Elasticsearch**
- **Kibana** provides log search, visualization, and analysis
- Structured logging via `logstash-logback-encoder`

### Traces — Jaeger + OpenTelemetry
- **OpenTelemetry Java Agent** auto-instruments the application
- Traces exported to **Jaeger** via OTLP protocol
- Full distributed tracing across HTTP requests, DB queries, and Kafka events

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics Endpoint
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## 🔄 CI/CD

The project uses **GitHub Actions** for continuous integration:

```yaml
# .github/workflows/build.yml
- Triggers on push/PR to main branch
- Sets up JDK 21 (Temurin)
- Runs: mvn clean test
- Caches Maven dependencies
```

---

## 📁 Project Structure

```
childcare/
├── .github/workflows/          # CI/CD pipeline
│   └── build.yml
├── docs/images/                # README screenshots
├── src/main/java/net/kartikverma/childcare/
│   ├── config/                 # Security, Kafka, Redis, WebSocket, OpenAPI configs
│   ├── controller/             # REST & WebSocket controllers
│   │   ├── AuthController      # Register, Login
│   │   ├── BookingController   # CRUD for bookings
│   │   ├── CaregiverController # Profile & slots management
│   │   ├── ParentController    # Caregiver search
│   │   ├── ChatController      # Chat history REST
│   │   ├── ChatWebSocketCtrl   # Real-time chat (STOMP)
│   │   ├── ReviewController    # Reviews CRUD
│   │   └── AdminController     # Admin operations
│   ├── dto/                    # Request & Response DTOs
│   ├── enums/                  # Role, BookingStatus, SessionStatus
│   ├── exception/              # Global exception handler
│   ├── kafka/                  # Event-driven architecture
│   │   ├── event/              # BookingEvent
│   │   ├── producer/           # Publishes booking events
│   │   └── consumer/           # Consumes & sends email notifications
│   ├── model/                  # JPA entities
│   ├── repository/             # Spring Data repositories
│   ├── security/               # JWT filter, utility, UserDetailsService
│   └── service/                # Business logic layer
├── src/main/resources/
│   ├── application.yaml        # App configuration
│   └── logback-spring.xml      # Structured logging config
├── docker-compose.yml          # Full stack orchestration
├── Dockerfile                  # Multi-stage build
├── prometheus.yml              # Prometheus scrape config
├── filebeat.yml                # Log shipping config
└── pom.xml                     # Maven dependencies
```

---

## ⚙️ Environment Variables

When running with Docker Compose, these can be configured:

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/childcare_db` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | DB password |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka:29092` | Kafka broker address |
| `SPRING_DATA_REDIS_HOST` | `redis` | Redis host |
| `SPRING_DATA_REDIS_PORT` | `6379` | Redis port |
| `SPRING_MAIL_USERNAME` | — | Gmail address for notifications |
| `SPRING_MAIL_PASSWORD` | — | Gmail app password (16-char) |
| `JWT_SECRET` | — | JWT signing secret (min 32 chars) |
| `JWT_EXPIRATION` | `86400000` | JWT token expiry (ms) |
| `OTEL_SERVICE_NAME` | `childcare-platform` | OpenTelemetry service name |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://jaeger:4318` | OTLP trace endpoint |

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/Kartik-Verma-Nnl">Kartik Verma</a>
</p>
