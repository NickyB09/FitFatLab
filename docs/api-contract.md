# FitFatLab API Contract

Esta guía resume los endpoints principales para que el frontend pueda empezar sin depender de leer todo el código backend.

## Base URL local

```text
http://localhost:8080
```

## Auth

### Login

`POST /api/v1/auth/login`

```json
{
  "email": "user@mail.com",
  "password": "password123"
}
```

Respuesta:

```json
{
  "token": "jwt-access-token",
  "refreshToken": "opaque-refresh-token",
  "email": "user@mail.com",
  "fullName": "Fit User",
  "roles": ["ROLE_USER"],
  "expiresIn": 86400000
}
```

### Refresh session

`POST /api/v1/auth/refresh`

```json
{
  "refreshToken": "opaque-refresh-token"
}
```

Respuesta: mismo formato que login.

### Logout

`POST /api/v1/auth/logout`

```json
{
  "refreshToken": "opaque-refresh-token"
}
```

Respuesta: `204 No Content`

## Users

### Register

`POST /api/v1/users/register`

### Current user

- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`

Body update:

```json
{
  "fullName": "Nuevo Nombre",
  "password": "newpassword123"
}
```

## Exercises

- `GET /api/v1/exercises`
- `GET /api/v1/exercises/by-muscle?muscleGroup=chest`
- `GET /api/v1/exercises/{id}`
- `POST /api/v1/exercises`
- `POST /api/v1/exercises/global`
- `PUT /api/v1/exercises/{id}`
- `DELETE /api/v1/exercises/{id}`

## Routines

- `GET /api/v1/routines`
- `GET /api/v1/routines/{id}`
- `POST /api/v1/routines`
- `PUT /api/v1/routines/{id}`
- `DELETE /api/v1/routines/{id}` (soft delete / deactivate)

Body create/update:

```json
{
  "name": "Push Day",
  "description": "Upper body routine",
  "exercises": [
    {
      "exerciseId": "uuid",
      "sets": 4,
      "reps": 10,
      "restSeconds": 90
    }
  ]
}
```

## Diet

- `POST /api/v1/diet/entries`
- `GET /api/v1/diet/entries?start=2026-04-01&end=2026-04-06`
- `GET /api/v1/diet/entries/{id}`
- `PUT /api/v1/diet/entries/{id}`
- `DELETE /api/v1/diet/entries/{id}`
- `GET /api/v1/diet/summary?date=2026-04-06`

## Progress

- `PUT /api/v1/progress/today`
- `GET /api/v1/progress/history?start=2026-04-01&end=2026-04-06`
- `GET /api/v1/progress/{id}`
- `DELETE /api/v1/progress/{id}`

## Authorization from frontend

Enviar el access token así:

```http
Authorization: Bearer <token>
```

## Error shape

Cuando hay errores controlados, la API responde con estructura tipo:

```json
{
  "status": 400,
  "error": "Validation failed",
  "message": "One or more fields are invalid",
  "path": "/api/v1/auth/login",
  "timestamp": "2026-04-06 11:00:00",
  "fieldErrors": [
    {
      "field": "email",
      "rejectedValue": "bad-email",
      "message": "must be a well-formed email address"
    }
  ]
}
```
