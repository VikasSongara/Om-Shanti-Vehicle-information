# Vehicle Information System

Spring Boot web application for community vehicle records. Data is stored in **Supabase PostgreSQL**. Public users can search; admins manage records after login. Excel import remains available as an admin tool.

---

## Technology

- Java 17 LTS
- Spring Boot 3.5.x (MVC, Security, Validation, JPA)
- PostgreSQL (Supabase)
- Thymeleaf + Bootstrap 5 + Bootstrap Icons
- Apache POI (Excel **import** only)
- Maven

---

## How to Run (local)

### Prerequisites

- JDK 17+
- Maven 3.9+
- SQL Server (local) / Supabase PostgreSQL (production)

### Local (SQL Server — profile `dev`)

Defaults are in `application-dev.properties`:

```text
jdbc:sqlserver://192.168.100.55:1440;databaseName=vs_Test;encrypt=true;trustServerCertificate=true
username=PTDev
password=PTDev
```

```bash
mvn clean spring-boot:run
```

Profile `dev` is active by default (`spring.profiles.active=dev`).

### Production (PostgreSQL Supabase — profile `prod`)

Settings are in `application-prod.properties` (DB pooler, admin login, cookies). No environment variables required at runtime.

Build the JAR, copy to the server, then:

```bash
java -jar /home/ubuntu/vehicle-information-system.jar --spring.profiles.active=prod
```

Local dev (default profile):

```bash
mvn clean package -DskipTests
java -jar target/vehicle-information-system.jar --spring.profiles.active=dev
```

---

## URLs

| Page | URL |
| --- | --- |
| Home | http://localhost:8080/ |
| Vehicles | http://localhost:8080/vehicles |
| Admin Login | http://localhost:8080/login |
| Admin Dashboard | http://localhost:8080/admin/dashboard |

---

## Database

| Profile | Config file | Database |
| --- | --- | --- |
| `dev` | `application-dev.properties` | SQL Server (local) |
| `prod` | `application-prod.properties` | Supabase PostgreSQL (Session pooler) |

Edit the matching properties file, rebuild the JAR, and redeploy. Hibernate creates/updates tables (`ddl-auto=update`).

**Security:** If the repo is public, do not commit real DB or admin passwords—keep secrets only on the server or use a private repo.

---

## Admin login

| Profile | Username | Password |
| --- | --- | --- |
| `dev` (local) | `admin` | `admin#123` |
| `prod` (Render) | from env `ADMIN_USERNAME` | from env `ADMIN_PASSWORD` |

---

## Optional: Render (Docker)

If you deploy with Docker on Render, set only:

```text
SPRING_PROFILES_ACTIVE=prod
```

All other settings are baked into `application-prod.properties` inside the JAR at build time. Rebuild and push the image after changing production properties.

---

## Excel (Admin)

| Action | Path |
| --- | --- |
| Import | `/vehicles/import` (append or replace into DB) |
| Export | `/vehicles/export` (download all vehicles as `.xlsx`) |
| Demo template | `/vehicles/import/demo` |

Required columns:

```text
Vehicle Type | Vehicle Number | Block No | House No | Name | Mobile Number
```

---

## Security

- Session authentication, BCrypt passwords, CSRF on forms
- Public: `/`, `/vehicles`, `/vehicles/search`, `/login`, static assets
- Admin: `/admin/**`, add/edit/delete/import/export
