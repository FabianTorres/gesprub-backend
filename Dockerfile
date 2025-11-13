# --- Etapa 1: Construcción con Maven ---
# Usamos una imagen que contiene Maven y JDK 21 para compilar el proyecto.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo
WORKDIR /workspace/app

# Copiamos el pom.xml y descargamos las dependencias primero para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente y construimos el proyecto
COPY src ./src
RUN mvn package -DskipTests

# --- PASO DE DEPURACIÓN: Listamos los archivos en la carpeta target ---
RUN ls -l /workspace/app/target/

# --- Etapa 2: Imagen Final ---
# Usamos una imagen base de Java 21 muy ligera para la ejecución.
FROM openjdk:21-jdk-slim

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos únicamente el JAR construido desde la etapa 'build'
COPY --from=build /workspace/app/target/gesprub-backend-*.jar app.jar

# Exponemos el puerto 8090, que coincide con tu server.port
EXPOSE 8090

# Comando para ejecutar la aplicación
#ENTRYPOINT ["java","-jar","/app.jar"]
#ENTRYPOINT ["ls", "-lR", "/app"]
ENTRYPOINT ["java","-jar","/app/app.jar"]
