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

Set env vars (Render) and use profile `prod`. See Deploy on Render section below.

---

## URLs

| Page | URL |
| --- | --- |
| Home | http://localhost:8080/ |
| Vehicles | http://localhost:8080/vehicles |
| Admin Login | http://localhost:8080/login |
| Admin Dashboard | http://localhost:8080/admin/dashboard |

---

## Database (Supabase PostgreSQL)

Defaults match your Supabase project. Password is **required** via env:

| Variable | Default / example |
| --- | --- |
| `DB_HOST` | `db.tqdduurvsqqnfqqdgfbx.supabase.co` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `postgres` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | *(set in Render / local env — never commit)* |

On first startup, Hibernate creates the `vehicles` table (`ddl-auto=update`).

---

## Admin Configuration

### Local / Dev (`spring.profiles.active=dev`)
Defaults (no env vars needed):

| Username | Password |
| --- | --- |
| `admin` | `admin123` |

Override with `ADMIN_USERNAME` / `ADMIN_PASSWORD` if you want.

### Production / Render (`spring.profiles.active=prod`)
**Required** environment variables (no defaults — app will not start without them):

| Variable | Example |
| --- | --- |
| `ADMIN_USERNAME` | `admin` |
| `ADMIN_PASSWORD` | strong password or BCrypt hash |

Never commit production passwords.

---

## Deploy on Render (Docker)

1. Push this project to GitHub.
2. Render → **New → Web Service** → Docker runtime.
3. Environment variables:

| Key | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `COOKIE_SECURE` | `true` |
| `ADMIN_USERNAME` | your admin username (**required**) |
| `ADMIN_PASSWORD` | your strong password (**required**) |
| `SPRING_DATASOURCE_URL` | Supabase **Session pooler** JDBC URL (**required**, see below) |
| `DB_USER` | `postgres.<project-ref>` (Session pooler user) |
| `DB_PASSWORD` | your Supabase database password |

**Do not use** `db.<project>.supabase.co` on Render — it is often IPv6-only and the deploy hangs with “No open ports detected”.

### Supabase Session pooler URL

In Supabase: **Project Settings → Database → Connection string → Session pooler**.

Example:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require
DB_USER=postgres.tqdduurvsqqnfqqdgfbx
DB_PASSWORD=your-supabase-db-password
```

4. Health Check Path: `/`
5. Deploy → open `https://<your-app>.onrender.com/`

If deploy still times out, check Render logs for Hikari/DB errors (wrong password, missing env vars, or still using the direct `db.*` host).

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
