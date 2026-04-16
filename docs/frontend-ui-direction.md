# FitFatLab Frontend UI Direction

## Objective

Definir una dirección visual clara para el futuro frontend web/mobile de FitFatLab, alineada con el backend ya implementado y con la preferencia estética del producto:

- dark mode con negro profundo real
- sans-serif moderna y bold
- layout limpio y minimalista
- verde como color principal
- pasteles elegantes como apoyo
- acentos neón sutiles
- vibra fitness / performance / gym premium sin exagerar

---

## Product Context

Hoy el repositorio implementa el backend de:

- autenticación
- usuarios
- ejercicios
- rutinas
- dieta
- progreso físico

El frontend todavía no existe, así que esta guía sirve como base de diseño para construir una experiencia coherente desde el inicio.

---

## Visual North Star

FitFatLab debe sentirse como una mezcla entre:

- app de fitness premium
- dashboard de producto digital moderno
- interfaz mobile-first con look web limpio

La sensación buscada es:

- fuerte
- enfocada
- atlética
- elegante
- tecnológica
- limpia

No debe verse:

- gamer exagerada
- startup genérica
- recargada
- gris tibio
- demasiado colorida

---

## Design Principles

1. **Dark first**  
   La base visual debe ser negra, profunda y limpia.

2. **Bold hierarchy**  
   Títulos con presencia, números grandes, KPIs claros y buen contraste.

3. **Minimal but energetic**  
   Mucho aire, pocos elementos, pero con detalles visuales que transmitan performance.

4. **App feel over landing page feel**  
   La UI debe parecer producto usable, no una página marketing.

5. **Controlled personality**  
   El verde, los pasteles y el neón deben acentuar, no dominar.

---

## Suggested Palette

### Base

- `#050505` — negro profundo principal
- `#0B0F0C` — superficie oscura secundaria
- `#111713` — cards y paneles
- `#171F19` — bordes suaves / elevación mínima

### Primary

- `#59FFA0` — verde principal brillante
- `#2FD97A` — verde funcional / estados activos
- `#1DB954` — variante sólida para CTAs

### Elegant pastels

- `#C7F5D9` — verde pastel claro
- `#D8D4FF` — lavanda pastel elegante
- `#FFD9E8` — rosa pastel suave
- `#CFF4FF` — azul pastel frío

### Accent neon

- `#7CFFB2` — glow/acento puntual
- `#A8FF60` — highlight muy controlado

### Text

- `#F5F7F5` — texto principal
- `#B8C0BA` — texto secundario
- `#7E8A82` — texto terciario

### Feedback

- `#34D399` — success
- `#FBBF24` — warning
- `#FB7185` — error

---

## Typography

### Recommended direction

- Sans-serif grotesk o neo-grotesk
- Títulos: bold / semibold
- UI text: regular / medium

### Good references

- Inter
- Satoshi
- General Sans
- Geist
- Manrope

### Usage

- Hero / screen titles: bold, compactos, limpios
- KPIs: grandes y muy legibles
- Labels y captions: sobrios, sin ruido

---

## Layout Direction

- Grid limpio y modular
- Mucho espacio negativo
- Cards oscuras con contraste sutil
- Bordes suaves, no redondeo exagerado
- Sombras mínimas; mejor usar contraste de superficie
- Separación clara entre navegación, contenido y acciones

### Visual language

- fondos limpios
- bloques sólidos
- acentos lineales o glow muy sutil
- charts simples y premium
- iconografía simple, atlética y moderna

---

## Core Components

### Navigation

- Sidebar oscura para desktop
- Bottom navigation o tab bar para mobile
- Indicador activo en verde

### Buttons

- Primary: verde sólido o verde brillante controlado
- Secondary: oscuro con borde fino
- Tertiary: ghost limpio

### Cards

- Fondo: casi negro
- Borde: muy sutil
- Estados hover: ligera elevación visual o borde verde suave

### Inputs

- Altamente legibles sobre fondo oscuro
- Focus state verde
- Labels claras, sin ruido decorativo

### Data blocks

- KPIs con números grandes
- métricas de peso, calorías, progreso y sets/reps como protagonistas

---

## Screen Map Based on Current Backend

### 1. Auth
Backed by:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `POST /api/v1/users/register`

UI direction:

- login/register minimalista
- formulario central limpio
- foco en contraste, tipografía y CTA principal

### 2. Home / Dashboard
Composición frontend sugerida usando:

- rutinas activas
- resumen de dieta del día
- progreso reciente
- accesos rápidos a ejercicios

UI direction:

- dashboard tipo app
- cards con KPIs
- progreso semanal
- CTA para “log today”, “start routine”, “track meal”

### 3. Exercises Explorer
Backed by:

- `GET /api/v1/exercises`
- `GET /api/v1/exercises/by-muscle`
- `GET /api/v1/exercises/{id}`

UI direction:

- catálogo limpio con filtros
- chips por muscle group
- cards con nombre, grupo muscular y detalle

### 4. Routine Builder
Backed by:

- `GET /api/v1/routines`
- `GET /api/v1/routines/{id}`
- `POST /api/v1/routines`
- `PUT /api/v1/routines/{id}`
- `DELETE /api/v1/routines/{id}`

UI direction:

- constructor modular
- bloques por ejercicio
- sets/reps/rest muy visibles
- experiencia mobile-first

### 5. Nutrition Tracking
Backed by:

- `POST /api/v1/diet/entries`
- `GET /api/v1/diet/entries`
- `GET /api/v1/diet/summary`
- `PUT /api/v1/diet/entries/{id}`
- `DELETE /api/v1/diet/entries/{id}`

UI direction:

- timeline o lista diaria
- summary de macros/calorías con mucha claridad
- visual simple, no “food app” recargada

### 6. Progress Tracking
Backed by:

- `PUT /api/v1/progress/today`
- `GET /api/v1/progress/history`
- `GET /api/v1/progress/{id}`
- `DELETE /api/v1/progress/{id}`

UI direction:

- peso y body fat como métricas hero
- charts minimalistas
- comparación semanal/mensual

### 7. Profile / Account
Backed by:

- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`

UI direction:

- settings limpios
- perfil sobrio
- estados de sesión y seguridad bien resueltos

---

## Role-Aware UX

El backend ya diferencia permisos por rol en algunas acciones:

- `USER`
- `TRAINER`
- `ADMIN`

El frontend debe ocultar o adaptar acciones según permisos:

- creación/edición avanzada de ejercicios para trainer/admin
- creación global de ejercicios sólo para admin
- vistas de administración separadas del flujo principal del usuario final

---

## Motion and Interaction

- microinteracciones rápidas
- transiciones cortas y suaves
- hover/focus con borde o glow muy leve
- evitar animaciones pesadas

La sensación debe ser:

- precisa
- rápida
- premium

---

## AI Design Prompt

> Diseña el frontend de FitFatLab como una app web/mobile premium de fitness y performance. Debe tener una identidad visual bold, sans-serif, minimalista y muy limpia. La base visual debe ser dark, usando negro profundo real en lugar de gris tibio. El estilo debe sentirse atlético, elegante, tecnológico y enfocado, con una vibra de producto digital moderno más cercana a una app usable que a una landing page. Usa verde como color protagonista, acompañado por pasteles elegantes y acentos neón sutiles. Mantén contraste alto, jerarquía fuerte, mucho aire y componentes tipo app. El producto debe contemplar pantallas para login, registro, dashboard, ejercicios, constructor de rutinas, dieta, progreso y perfil. Evita ornamentos excesivos, look gamer exagerado, grises sucios o estética startup genérica. Busca una mezcla entre lujo moderno, fitness premium y software dark clean.

---

## First Frontend Build Recommendation

Si se empieza el frontend ahora, el orden sugerido es:

1. auth + sesión
2. app shell (sidebar/topbar/bottom nav)
3. dashboard
4. exercises explorer
5. routines CRUD
6. diet tracking
7. progress tracking
8. profile/settings

---

## Notes for Implementation

- Diseñar primero mobile-first y luego expandir a desktop
- Mantener tokens, estados vacíos y errores con el mismo lenguaje visual
- Reusar una misma escala de spacing y radios
- Priorizar consistencia sobre variedad visual
- Tratar métricas de fitness como elementos hero del producto
