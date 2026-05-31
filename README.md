# ⚽ Prode Mundial 2026 — Spring Boot + MySQL

## Stack
- Java 17
- Spring Boot 2.7.18
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 8 (base de datos: `mundial2026`)
- Frontend: HTML/CSS/JS puro (sin frameworks)

---

## 1. Requisitos previos
- JDK 17 instalado → https://adoptium.net
- Maven 3.8+ instalado → https://maven.apache.org
- MySQL 8 corriendo (XAMPP o servidor)
- VS Code con extensiones:
  - Extension Pack for Java
  - Spring Boot Extension Pack

---

## 2. Crear la base de datos MySQL

Abrí phpMyAdmin o MySQL Workbench y ejecutá:

```sql
CREATE DATABASE IF NOT EXISTS mundial2026
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

La aplicación crea las tablas automáticamente al arrancar
gracias a `spring.jpa.hibernate.ddl-auto=update`.

---

## 3. Configurar credenciales

Editá `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mundial2026?...
spring.datasource.username=root      # tu usuario MySQL
spring.datasource.password=          # tu contraseña MySQL
```

---

## 4. Compilar y ejecutar

```bash
# Desde la carpeta raíz del proyecto
mvn spring-boot:run
```

O en VS Code: abrí `ProdeApplication.java` y presioná el botón ▶ Run.

La app arranca en → http://localhost:8080

---

## 5. Primer acceso

Al arrancar por primera vez, se crean automáticamente:
- **48 partidos** de fase de grupos + eliminatorias
- **Usuario admin**: `admin@prode.com` / `Gz......`

⚠️ Cambiá la contraseña del admin después del primer login.

---

## 6. Uso de la aplicación

### Usuarios normales
1. Registrate en `/register.html`
2. Iniciá sesión con tu email
3. **Mis Pronósticos** → cargá resultados de los 48 partidos de fase de grupos
4. **Ranking** → posiciones en tiempo real
5. **Comparar** → enfrentate partido a partido con otro jugador

### Administrador
1. Logueate como `admin@prode.com`
2. Aparece la tab **⚙️ Admin**
3. Cargá resultados reales → se calculan puntos automáticamente

---

## 7. Sistema de puntos

| Acierto | Puntos |
|---------|--------|
| 🎯 Resultado exacto (ej: 2-1 vs 2-1) | 3 pts |
| ✅ Ganador correcto (ej: 2-0 vs 3-0)  | 1 pt  |
| ❌ Incorrecto                          | 0 pts |

Configurar en `application.properties` (próximamente).

---

## 8. Estructura del proyecto

```
prode-mundial/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/prode/
    │   ├── ProdeApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java      ← Spring Security + JWT
    │   │   ├── CorsConfig.java
    │   │   └── DataInitializer.java     ← Carga partidos y admin
    │   ├── controller/
    │   │   ├── AuthController.java      ← /api/auth/login, /register
    │   │   └── MatchController.java     ← /api/matches, /predictions, /ranking
    │   ├── dto/                         ← Objetos de transferencia
    │   ├── entity/
    │   │   ├── User.java
    │   │   ├── Match.java
    │   │   └── Prediction.java
    │   ├── repository/                  ← JPA Repositories
    │   ├── security/
    │   │   ├── JwtUtil.java
    │   │   └── JwtAuthFilter.java
    │   └── service/
    │       ├── AuthService.java
    │       ├── MatchService.java
    │       └── UserDetailsServiceImpl.java
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html               ← App principal
            ├── login.html
            ├── register.html
            ├── css/style.css
            └── js/
                ├── api.js               ← Cliente HTTP con JWT
                └── app.js               ← Lógica UI
```

---

## 9. API REST — Endpoints principales

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/auth/register` | No | Registrar usuario |
| POST | `/api/auth/login`    | No | Login → devuelve JWT |
| GET  | `/api/matches/group` | JWT | Partidos fase de grupos |
| GET  | `/api/matches/all`   | JWT | Todos los partidos |
| POST | `/api/predictions`   | JWT | Guardar pronóstico |
| GET  | `/api/ranking`       | JWT | Ranking general |
| GET  | `/api/users`         | JWT | Lista de jugadores |
| GET  | `/api/compare/{id}`  | JWT | Comparar con usuario |
| POST | `/api/admin/result`  | ADMIN | Cargar resultado real |
| GET  | `/api/admin/matches` | ADMIN | Ver todos los partidos |

---

## 10. Deployment en hosting

1. Cambiar en `application.properties` los datos de producción
2. Compilar: `mvn clean package -DskipTests`
3. Subir el `.jar` generado en `target/`
4. Ejecutar: `java -jar prode-mundial-1.0.0.jar`

O configurar como servicio systemd en Linux.
