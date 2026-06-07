# ── ETAPA 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar solo el pom.xml primero para cachear dependencias
# (si no cambiás el pom, esta capa no se reconstruye)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── ETAPA 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Alpine es mucho más liviano (~50MB vs ~200MB)
WORKDIR /app

# Copiar el JAR
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone Spring Boot
EXPOSE 8080

# Flags JVM para arranque rápido en contenedores con poca RAM
# -XX:TieredStopAtLevel=1  →  no compila código que no se usa al inicio
# -Xss512k               →  reduce stack por thread
# -XX:MaxRAM=512m         →  limita RAM (plan free de Render tiene 512MB)
# -Djava.security.egd     →  acelera generación de números aleatorios (JWT)
ENTRYPOINT ["java", \
  "-XX:TieredStopAtLevel=1", \
  "-Xss512k", \
  "-XX:MaxRAM=512m", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
