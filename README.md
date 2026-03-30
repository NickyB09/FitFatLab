# FitFatLab — Gestión Integral de Entrenamiento Físico 

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![React Native](https://img.shields.io/badge/React_Native-Expo-61DAFB?style=for-the-badge&logo=react&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

> **FitFatLab** es una plataforma web fullstack diseñada para la administración avanzada de entrenamiento físico, nutrición y seguimiento de progreso. Este proyecto ha sido concebido bajo un enfoque de **Software Architecture**, priorizando la escalabilidad y el mantenimiento.

---

## Arquitectura del Sistema

El proyecto implementa una arquitectura de **Modular Monolith** (Monolito Modular). Esta elección permite una separación lógica clara por dominios de negocio, facilitando una futura migración a microservicios si el sistema lo requiere.

### Principios y Patrones Aplicados
* **SOLID Principles:** Aplicación rigurosa de los cinco principios para asegurar un código robusto.
* **Clean Code:** Enfoque en legibilidad, métodos atómicos y eliminación de redundancia (**DRY**).
* **Layered Architecture:** Separación estricta de responsabilidades (Controller, Service, Repository, Model).
* **Design Patterns:** Implementación de *Repository Pattern*, *Service Layer*, *DTO Pattern* e *Inyección de Dependencias*.

---

## Stack Tecnológico

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje Backend** | Java 17+ |
| **Framework Backend** | Spring Boot 3.x |
| **Seguridad** | Spring Security + JWT (Stateless) |
| **Persistencia** | Spring Data JPA / Hibernate |
| **Base de Datos** | PostgreSQL (Supabase) |
| **Frontend** | React Native + Expo (TypeScript) |
| **Documentación** | Swagger / OpenAPI |

---

## Estructura del Proyecto (Project Structure)

La organización de archivos sigue estándares internacionales, utilizando nomenclatura en **inglés** para la estructura técnica.

### Backend (Modular Structure)
```text
src/
 ├── modules/
 │    ├── user/          # Identity and Access Management
 │    │    ├── controller
 │    │    ├── service
 │    │    ├── repository
 │    │    ├── model
 │    │    └── dto
 │    ├── routine/       # Training plans logic
 │    ├── exercise/      # Exercises catalog
 │    ├── diet/          # Nutritional planning
 │    └── progress/      # Metrics and tracking
 ├── security/           # JWT and Security filters
 ├── config/             # General configurations
 └── common/             # Shared utilities and exceptions
```
### Frontend
```text
src/
 ├── screens/            # Application views
 ├── components/         # Reusable UI components
 ├── services/           # API communication (Axios/Fetch)
 ├── hooks/              # Custom React hooks
 ├── navigation/         # Routing logic
 └── types/              # TypeScript interfaces/types
```
# Configuración e Instalación

### Requisitos Previos
- JDK 17 o superior.
- Node.js y npm/yarn.
- Instancia de PostgreSQL (o credenciales de Supabase).

### Backend
1. Clonar el repositorio.
2. Configurar las variables de entorno en:
   `src/main/resources/application.properties`
3. Ejecutar el proyecto:

```bash 
./mvnw spring-boot:run
```

### Frontend
1. Navegar a la carpeta del frontend.
2. Instalar dependencias:
```bash
npm install
```
3. Iniciar el entorno de Expo:
```bash
npx expo start
```
# Objetivos del Proyecto

### FitFatLab no es solo una herramienta funcional, es un caso de estudio arquitectónico que demuestra:

- Capacidad para diseñar sistemas desacoplados y modulares.
- Implementación de seguridad perimetral avanzada.
- Uso de tipos de datos complejos y relaciones en bases de datos relacionales.
- Construcción de una base de código preparada para entornos empresariales.

## Autor
**Nicolás Andrés Betancur Ardila, Software Engineer**

#### Contacto: Palaciosnico2@gmail.com
