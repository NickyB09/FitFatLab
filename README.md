# FitFatLab Backend

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Tests](https://img.shields.io/badge/tests-Maven%20%2B%20JUnit5-blue?style=for-the-badge)

> Backend API para gestión de usuarios, autenticación, rutinas, ejercicios, dieta y progreso físico.
> El frontend **todavía no ha empezado**; este repositorio hoy representa únicamente la base backend del proyecto.

---

## Estado actual

### Ya implementado
- Autenticación con JWT
- Refresh tokens hasheados + logout
- Hardening básico de auth/JWT (secret por entorno, 401 en token inválido, access token corto)
- CORS configurable para frontend local
- Registro y consulta de usuarios
- Catálogo de ejercicios
- Gestión de rutinas personales
- Planning de rutinas coach -> student (templates, assignments, completion, reuse)
- Registro de dieta personal
- Planning nutricional coach -> student con permisos de edición
- Schedule informativo coach -> student
- Seguimiento de progreso
- Swagger / OpenAPI
- Migraciones con Flyway
- Tests de contexto, servicios, controladores y utilidades JWT

### Pendiente
- Frontend web/mobile
- Deploy público

---

## Arquitectura

Este backend sigue un enfoque de **monolito modular**:

```text
src/main/java/com/fitfatlab/fitfatlab_backend
├── common/      # config compartida, errores, utilidades
├── modules/
│   ├── auth/
│   ├── user/
│   ├── exercise/
│   ├── routine/
│   ├── diet/
│   └── progress/
└── security/    # JWT, filtros y configuración de seguridad
```

### Principios aplicados
- Separación Controller / Service / Repository / DTO
- Validación con Bean Validation
- Seguridad stateless
- Configuración por perfiles (`default`, `dev`, `test`)
- Persistencia versionada con Flyway

---

## Stack

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Seguridad | Spring Security + JWT |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | PostgreSQL |
| Testing | JUnit 5, MockMvc, Mockito, H2, Testcontainers |
| Documentación | springdoc OpenAPI / Swagger UI |

---

## Setup local gratis

### Requisitos
- JDK 21+
- Docker Desktop o Docker Engine

### 1. Levantar PostgreSQL local

```bash
cp .env.example .env
docker compose up -d
```

### 2. Ejecutar la app en modo desarrollo

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

O con Makefile:

```bash
make up
make run
```

### 3. Abrir documentación Swagger

```text
http://localhost:8080/swagger-ui/index.html
```

### 4. CORS listo para frontend local

Por defecto el backend acepta peticiones desde:

- `http://localhost:3000`
- `http://localhost:5173`
- `http://localhost:8081`

 Próximamente, si el frontend usa otro origen, cambia `FITFATLAB_ALLOWED_ORIGINS` en `.env`.

---

## Variables de entorno

Puedes usar `.env.example` como referencia local:

| Variable | Propósito | Default local |
|---|---|---|
| `DB_USERNAME` | usuario de PostgreSQL | `fitfatlab` |
| `DB_PASSWORD` | password de PostgreSQL | `fitfatlab123` |
| `JWT_SECRET` | clave base64 para JWT | valor de desarrollo |
| `JWT_EXPIRATION_MS` | duración del access token | `86400000` |
| `JWT_REFRESH_EXPIRATION_MS` | duración del refresh token | `604800000` |
| `FITFATLAB_ALLOWED_ORIGINS` | orígenes permitidos para frontend | `http://localhost:3000,http://localhost:5173,http://localhost:8081` |
| `SEED_ADMIN_ENABLED` | crea admin local automáticamente | `true` en dev |
| `SEED_ADMIN_EMAIL` | correo admin semilla | `admin@fitfatlab.com` |
| `SEED_ADMIN_PASSWORD` | password admin semilla | `Cambiarpassw!` |

> En `test`, el seed de admin está desactivado para que los tests sean reproducibles.

---

## Testing

### Ejecutar tests

```bash
./mvnw test
```

o:

```bash
make test
```

### Qué se está probando hoy
- Arranque del contexto Spring
- Casos clave de servicios (`auth`, `user`, `progress`, `diet`, `coaching`, `routine planning`, `schedule`)
- Validaciones HTTP en controladores (`auth`, `user`)
- Generación, validación y manejo de errores JWT
- Integración MockMvc para permisos y restricción de un entrenador activo por alumno

## CI automática

Workflow en:

```text
.github/workflows/backend-ci.yml
```

Ese pipeline corre automáticamente en GitHub:

- `./mvnw -q test`
- `./mvnw -q -DskipTests verify`
Se hace para confirmar que todo quede bien antes de empezar el front.

## Contrato para frontend

También quedó una guía rápida en:

```text
docs/api-contract.md
```

Aquí tenemos endpoints, payloads y formato de errores para empezar el front sin adivinar estructuras.

## Dirección visual del frontend

También quedó documentada una primera guía de estilo/UI para el futuro frontend en:

```text
docs/frontend-ui-direction.md
```

Esa guía aterriza la identidad visual deseada del producto al dominio real de FitFatLab: auth, ejercicios, rutinas, dieta, progreso y perfil.

---

## Decisiones importantes de configuración

### 1. Tests aislados de PostgreSQL real
Los tests usan **H2 en memoria** con el perfil `test`, para que cualquier persona pueda correr la suite sin pagar servicios ni depender de infraestructura externa.

### 2. Admin semilla controlado por configuración
El admin por defecto ya no se crea siempre. Ahora depende de propiedades, lo cual es más seguro para despliegues futuros.

### 3. `open-in-view` desactivado
Se desactivó para evitar malas prácticas de acceso a base de datos durante la capa web.

### 4. Refresh token persistente
El login ahora devuelve `token` y `refreshToken`. El refresh token se guarda en base de datos, se rota en cada refresh y se revoca en logout.

---

## Comandos útiles

```bash
# levantar db
docker compose up -d

# bajar db
docker compose down

# correr backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# correr tests
./mvnw test
```

---

## Roadmap que se seguirá con el paso del tiempo:

1. Montar CI con GitHub Actions
2. Completar tests de integración por módulo
3. Crear frontend
4. Desplegar backend + demo pública
5. Documentar decisiones arquitectónicas

---

## Autor

**Nicolás Andrés Betancur Ardila**  
Software Engineer en formación con foco en arquitectura de software.

## MVP Coach-Student ya implementado

### Endpoints nuevos para frontend
- `/api/v1/coaching/*` — relaciones coach-student y permisos
- `/api/v1/planning/routines/*` — templates, assignments, completion y reuse
- `/api/v1/planning/meals/*` — meal plans asignados por coach
- `/api/v1/planning/schedule/*` — horarios informativos

### Seguridad aplicada antes del frontend
- Access token corto (`15 min` por default)
- Refresh tokens almacenados como hash SHA-256 en DB
- JWT inválido o expirado devuelve `401`
- `JWT_SECRET` requerido en el perfil base
- Emails normalizados para login, búsqueda y registro
