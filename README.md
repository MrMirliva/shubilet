# ShuBilet

ShuBilet is a **role-aware** (**Customer / Company / Admin**) ticketing platform that models **intercity transportation workflows** using a **microservice topology** built on `Spring Boot` and `Eureka` service discovery.

This repository is intentionally structured as an architectural showcase:
- an **edge orchestrator** (`api-gateway`) is the single client-facing backend entry-point,
- core business capabilities live inside isolated services,
- service-to-service communication is performed via synchronous HTTP with discovery through `eureka-server`.

Frontend is implemented with **React + Vite**, while the backend consists of **Spring Boot microservices**.

---

## Functional Capabilities (High-Level)

### Customer
- Registration / Login / Logout (session-based)
- Expedition search
- Seat availability lookup
- Ticket purchase
- Purchased tickets listing

### Profile & Account Management
- Profile mutation (name, surname, gender, email, password)
- Favorite company association
- Payment card add / deactivate flows (via payment-service)

### Company
- Expedition creation
- Company-scoped expedition listing

### Admin
- Company verification workflow
- Admin verification workflow

---

## System Topology (Concrete)

```text
Browser
  │
  ▼
frontend (React + Vite)
  │  (Vite dev proxy: /api -> api-gateway)
  ▼
api-gateway (edge orchestrator, @LoadBalanced RestTemplate)
  │
  ├─► member-service     (registration, profiles, verification, resources)
  ├─► security-service   (session lifecycle + session validation)
  ├─► expedition-service (expeditions, seats, reservation + ticketing)
  └─► payment-service    (card registry + pseudo payment processing)
         ▲
         └──── consumed by expedition-service and member-service

eureka-server (service discovery)
postgres (expeditionDB + memberDB + securityDB + paymentDB)
pgadmin (DB UI)
```

### Discovery & Client-Side Load Balancing

- Backend services register themselves to `eureka-server`.
- The edge layer calls services using service identifiers (e.g. `http://member-service/...`) through a `@LoadBalanced` `RestTemplate`.

This keeps service addressing **indirection-friendly** (host/port changes are absorbed by discovery) while preserving a simple synchronous integration model.

---

## Global Layering Model (Conceptual)

The system can be reasoned about in four coarse layers:

1. **Presentation Layer** → `frontend/` (UI + routing, no backend access except via the gateway)
2. **Edge Layer** → `backend/api-gateway/` (API boundary + orchestration + DTO mapping)
3. **Domain/Application Layer** → `backend/*-service/` (business logic + persistence)
4. **Infrastructure Layer** → PostgreSQL, Docker, Docker Compose, pgAdmin

Inside each microservice, code is further organized in a typical layered structure:

```text
Controller → Service → Repository → Model/Entity
```

---

## Session Model (CookieDTO + Server-Side Session)

ShuBilet uses a **session-oriented** authentication model.

- The `api-gateway` keeps an `HttpSession` and serializes it into an internal `CookieDTO` carrier.
- Authentication/session lifecycle is delegated to `security-service` (`/api/auth/*`), which persists session records (admin/company/customer sessions) in PostgreSQL.
- A scheduled maintenance component (`SessionSweeper`, enabled via `@EnableScheduling`) periodically removes expired sessions.

To improve traceability across hops, the gateway commonly emits an `X-Request-Id` header which is forwarded to downstream services.

---

## Persistence Model (PostgreSQL)

The Docker runtime provisions a single PostgreSQL instance and creates two databases via `db/init.sql`:

- `expeditionDB`: used by `expedition-serivce`
- `memberDB`: used by `member-service`
- `securityDB`: used by `security-service`
- `paymentDB`: used by `payment-service`

Schema evolution in the local environment is handled by JPA with:
- `spring.jpa.hibernate.ddl-auto=update`

---

## Technology Stack (As Used In This Repo)

### Frontend
- React 19
- Vite
- React Router DOM

### Backend
- Java 21
- Spring Boot 3.3.4
- Spring Cloud Netflix Eureka (client/server) 2023.0.3
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- RestTemplate + Spring Cloud LoadBalancer (`@LoadBalanced`)
- Spring Scheduling (security-service)
- MapStruct (api-gateway DTO mapping)

### Infrastructure / DevOps
- PostgreSQL 15
- Docker & Docker Compose
- pgAdmin 4 (Docker image)
- Maven (wrapper in each backend service)

---

## Quick Start (Docker Compose)

### Prerequisites
- Docker Engine / Docker Desktop

### Run

```bash
docker compose up --build
```

### Access Points (Host)

- Frontend: `http://localhost:3000` (admin user: `shubilet@example.com`, password `SecurePassword123!`)
- API Gateway: `http://localhost:8080`
- Eureka Dashboard: `http://localhost:8761`
- pgAdmin: `http://localhost:5051` (user: `admin@example.com`, password: `admin`)
- PostgreSQL: `localhost:5432` (user: `postgres`, password: `123`)

### Stop / Reset

```bash
docker compose down
```

```bash
docker compose down -v
```

> Security note: Default credentials in `docker-compose.yml` are for local development only.

---

## API Surface (Gateway)

All external backend access is funneled through `api-gateway` (`http://localhost:8080`).

Primary endpoint groups exposed by the edge layer:

- `POST /api/auth/*` (register/login/logout)
- `POST /api/expedition/*` (search expeditions, seats, create expedition, list company expeditions)
- `POST /api/ticket/*` (buy ticket, list tickets)
- `POST /api/profile/*` (profile mutation, favorites, cards)
- `POST /api/auth/verify/*` (company/admin verification)

---

## Repository Layout

```text
.
├── backend/
│   ├── api-gateway
│   ├── eureka-server
│   ├── member-service
│   ├── security-service
│   ├── expedition-service
│   └── payment-service
├── frontend/
├── db/
│   └── init.sql
└── docker-compose.yml
```

---

## Authors

- Abdullah Gündüz (Mirliva) — https://github.com/MrMirliva
- Murat Furkan Şen (Fixie) — https://github.com/MuratFurkanSen
- Ömer Tahsin Taşkın — https://github.com/omertahsintaskin
