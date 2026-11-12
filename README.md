# Smart Public Healthcare Appointment and Queue Management System

**Team**: Team E10
**Domain**: Public Healthcare Management
**Project Status**: Phases 1-3 complete: baseline UI, MySQL entity mapping, Spring Data repositories, and service layer.

The current web application serves a landing page with sample department and queue data. Patient registration, appointment booking, and queue operations exist at the service layer but are not exposed as user-facing web workflows. Authentication/RBAC and external notification delivery are not implemented.

---

## 1. Project Overview

The Smart Public Healthcare Appointment and Queue Management System is an administrative web application designed to help public hospitals and outpatient departments (OPD) coordinate patient scheduling, minimize waiting room congestion, issue digital queue tokens, and provide transparent clinic schedules.

> **Note**: This system is strictly an administrative scheduling and queue coordination tool; it does not provide clinical diagnoses, emergency triage, or electronic prescription management.

---

## 2. Technology Stack

* **Language**: Java 21 (Eclipse Temurin LTS)
* **Framework**: Spring Boot 3.3.5
* **Web & MVC**: Spring MVC (`spring-boot-starter-web`)
* **View Engine**: Thymeleaf (`spring-boot-starter-thymeleaf`)
* **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
* **Persistence & ORM**: Spring Data JPA / Hibernate Core 6.5 (`spring-boot-starter-data-jpa`)
* **Database**: MySQL 8.x (`mysql-connector-j`)
* **Testing**: JUnit 5, AssertJ, Spring Boot Test (`spring-boot-starter-test`)
* **Frontend UI**: Bootstrap 5, Bootstrap Icons, Custom CSS3, Vanilla JavaScript
* **Build System**: Maven Wrapper (`mvnw.cmd`)
* **Development Environment**: Eclipse IDE / Google Antigravity

---

## 3. Database Architecture & 9 Core Entities (Phase 2)

The application models 9 relational database tables in `healthcare_db` with enterprise constraints, foreign keys, and optimized indexing:

1. **`users` (`User`)**:
   System account records, contact details, account status, and role values (`ADMIN`, `DOCTOR`, `RECEPTIONIST`, `PATIENT`). Authentication and password hashing are not implemented.
2. **`departments` (`Department`)**:
   Outpatient medical specialties, clinical departments, room locations, and status.
3. **`doctors` (`Doctor`)**:
   Physician profiles linked 1-to-1 to user accounts, department associations, qualifications, experience, and consultation rooms.
4. **`patients` (`Patient`)**:
   Patient demographic records, unique hospital identifiers (`patient_identifier`), contact numbers, blood groups, and emergency contacts.
5. **`doctor_schedules` (`DoctorSchedule`)**:
   Doctor clinic operating sessions, days of the week, session start/end times, slot durations, and outpatient session capacities.
6. **`appointments` (`Appointment`)**:
   Outpatient booking records with unique references (`appointment_reference`), date, time, status, and a compound uniqueness constraint `(doctor_id, appointment_date, appointment_time)` to eliminate duplicate slot bookings.
7. **`queue_tokens` (`QueueToken`)**:
   Digital token numbers (`token_number`), daily token dates, 1-to-1 appointment bindings, queue progression statuses, and a uniqueness constraint `(doctor_id, token_date, token_number)`.
8. **`notifications` (`Notification`)**:
   Patient and staff alerts, appointment confirmations, reminders, rescheduled/cancelled notices, and delivery tracking (`PENDING`, `SENT`, `FAILED`).
9. **`audit_logs` (`AuditLog`)**:
   System-wide audit trail recording administrative actions, modified entities, performed usernames, timestamps, and client IP addresses.

The Phase 3 repositories and service layer are implemented. The homepage's department listings and token board remain sample data; they are not connected to persisted MySQL records. Notification records remain `PENDING` until a delivery provider confirms success; no delivery provider is configured.

---

## 4. Layered Package Architecture

The project uses the following package structure under the base package `com.teame10.healthcare`:

```text
com.teame10.healthcare
├── HealthcareApplication.java        # Spring Boot main runner
├── controller/                       # Spring MVC landing-page controller (HomeController)
├── entity/                           # JPA Database Entities (9 core models & 6 enums)
├── exception/                        # Global & domain exception handlers
├── repository/                       # Spring Data JPA interfaces (Phase 3 complete)
└── service/                          # Business logic service interfaces & implementations (Phase 3 complete)
```

---

## 5. Database Configuration & Environment Variables

Environment variables can override the existing local-development database defaults in `application.properties`. Keep those defaults limited to local development; use environment-specific credentials outside it.

| Property Variable | Environment Variable | Default (Dev Fallback) |
| :--- | :--- | :--- |
| Database URL | `DB_URL` / `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/healthcare_db?...` |
| Username | `DB_USERNAME` / `SPRING_DATASOURCE_USERNAME` | `root` |
| Password | `DB_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` | Local dev password |

### TiDB Cloud

The optional `tidb` Spring profile loads `application-tidb.properties`; the default local MySQL profile and its settings remain unchanged. Configure these environment variables in the process or IDE launch configuration before starting the application:

| Environment Variable | Value |
| :--- | :--- |
| `DB_HOST` | TiDB Cloud endpoint hostname, without a scheme or port |
| `DB_USERNAME` | Exact username shown in the TiDB Cloud connection details |
| `DB_PASSWORD` | TiDB Cloud password |
| `DB_NAME` | Database name; defaults to `carequeue` |

Keep the password out of project files and source control. The TiDB profile uses TLS with hostname verification and connects on port `4000`.

In PowerShell, activate the profile for the current session and run the existing Maven wrapper:
```powershell
$env:SPRING_PROFILES_ACTIVE = 'tidb'
.\mvnw.cmd spring-boot:run
```

Set the TiDB variables in the same process environment or IDE launch configuration. If TiDB reports access denied, confirm the username and password from the current cluster connection details and verify the cluster permits connections from the current client IP.

---

## 6. Build and Execution Instructions

### Prerequisites
* Windows 10 / 11
* Java 21 LTS configured in `JAVA_HOME`
* MySQL 8.x service running on `localhost:3306`

### Running Tests
Execute the Maven wrapper clean verify command:
```cmd
mvnw.cmd clean verify
```

### Running the Application
Launch the Spring Boot development server:
```cmd
mvnw.cmd spring-boot:run
```

Once started, open your web browser and navigate to:
```text
http://localhost:8080
```

### Running the H2 demo profile (PowerShell)
Set the demo profile in the current PowerShell session and start Spring Boot:
```powershell
$env:SPRING_PROFILES_ACTIVE = 'demo'
.\mvnw.cmd spring-boot:run
```

The demo profile uses an in-memory H2 database and serves the application at:
```text
http://localhost:8081
```

### Deploying to Render (free Web Service)
1. Push this project to a Git provider and create a **Web Service** on Render from that repository.
2. Select **Docker** as the runtime. Render will build and start the included `Dockerfile`; no custom build or start command is needed.
3. The container activates the `demo` profile. The Docker build excludes the MySQL and TiDB property files, so only the H2 demo database is configured in the image. Render supplies `PORT`; the application binds to `0.0.0.0` and uses that port (8081 by default for local runs). Do not configure MySQL or TiDB for this demo.

The demo database is in-memory H2. Its data is temporary and is lost when the service restarts or is redeployed. Do not add credentials or `.env` files to the repository; local log files and credential files are excluded by `.gitignore` and `.dockerignore`.

---

## 7. Development Roadmap

1. **Phase 1**: Project setup, Maven wrapper, package structure, and baseline UI. *(Completed)*
2. **Phase 2**: MySQL database configuration and 9 core JPA entities. *(Completed)*
3. **Phase 3**: Spring Data JPA repositories and service layer implementation. *(Completed; not all service operations have user-facing web routes.)*
4. **Phase 4**: Spring Security, password hashing, and role-based access control. *(Not implemented.)*
5. **Phase 5**: Patient and doctor management workflows. *(Service operations exist; web workflows are not connected.)*
6. **Phase 6**: Appointment booking and slot management workflows. *(Service operations exist; web workflows and concurrency verification remain.)*
7. **Phase 7**: Persisted digital queue display and live status integration. *(Token service operations exist; the homepage board is sample data.)*
8. **Phase 8**: Role-specific portals (Admin, Doctor, Receptionist, Patient). *(Not implemented.)*
9. **Phase 9**: Notification delivery, audit integration, and reporting. *(Notification persistence exists; no delivery provider is configured.)*
10. **Phase 10**: End-to-end integration testing, system verification, and viva documentation. *(Remaining.)*
