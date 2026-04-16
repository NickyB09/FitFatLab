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
  "expiresIn": 900000
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

## Coaching

- `POST /api/v1/coaching/relationships`
- `GET /api/v1/coaching/relationships/coach`
- `GET /api/v1/coaching/relationships/student`
- `PATCH /api/v1/coaching/relationships/{id}/status`
- `PATCH /api/v1/coaching/relationships/{id}/meal-permissions`

Create relationship:

```json
{
  "studentId": "uuid"
}
```

Update status:

```json
{
  "status": "ACTIVE"
}
```

Meal permissions:

```json
{
  "allowStudentMealEdits": true
}
```

## Routine Planning

- `POST /api/v1/planning/routines/templates`
- `GET /api/v1/planning/routines/templates`
- `POST /api/v1/planning/routines/assignments`
- `GET /api/v1/planning/routines/assignments/student`
- `GET /api/v1/planning/routines/assignments/coach`
- `PATCH /api/v1/planning/routines/assignments/{id}/complete`
- `POST /api/v1/planning/routines/assignments/{id}/reuse`

Template body:

```json
{
  "name": "PPL Push",
  "description": "Chest, shoulders and triceps",
  "templateType": "GENERIC",
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

Assignment body:

```json
{
  "studentId": "uuid",
  "name": "Week 1 Strength",
  "description": "Heavy compound focus",
  "periodType": "WEEK",
  "scheduledDate": "2026-04-20",
  "exercises": [
    {
      "exerciseId": "uuid",
      "sets": 5,
      "reps": 5,
      "restSeconds": 120
    }
  ]
}
```

## Meal Planning

- `POST /api/v1/planning/meals`
- `GET /api/v1/planning/meals/student`
- `GET /api/v1/planning/meals/coach`
- `PATCH /api/v1/planning/meals/meals/{mealId}`

Create meal plan:

```json
{
  "studentId": "uuid",
  "name": "Week 1 Nutrition",
  "description": "High protein week",
  "periodType": "WEEK",
  "startDate": "2026-04-20",
  "endDate": "2026-04-26",
  "allowStudentEdits": true,
  "meals": [
    {
      "mealName": "Breakfast",
      "plannedDate": "2026-04-20",
      "calories": 500,
      "proteinG": 30,
      "carbsG": 55,
      "fatG": 12
    }
  ]
}
```

Update planned meal as student:

```json
{
  "mealName": "Edited Breakfast",
  "calories": 520,
  "proteinG": 35,
  "carbsG": 50,
  "fatG": 14
}
```

## Schedule Planning

- `POST /api/v1/planning/schedule`
- `GET /api/v1/planning/schedule/coach`
- `GET /api/v1/planning/schedule/student`

Create informative slot:

```json
{
  "studentId": "uuid",
  "weekday": "MONDAY",
  "startTime": "18:00:00",
  "note": "Post-work training"
}
```

## Security notes for frontend

- Access token must be sent as `Authorization: Bearer <token>`
- Access token expires quickly (`900000` ms by default)
- Use `/api/v1/auth/refresh` to rotate session
- Backend stores refresh tokens hashed; frontend only keeps the raw token returned by auth endpoints
