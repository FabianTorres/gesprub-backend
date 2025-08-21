# Usa una imagen base de Java 21, que coincide con tu pom.xml
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR de tu aplicación. El nombre del JAR se basa en tu pom.xml.
# Necesitarás compilarlo primero: mvn package
COPY target/gesprub-backend-1.0.jar app.jar

# Expone el puerto 8090, que coincide con tu server.port
EXPOSE 8090

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]