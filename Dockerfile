#Construir el Jar con Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
#Copiar el pom y el codigo fuente
COPY pom.xml .
COPY src ./src
#Compilar el proyecto sin ejecutrar los tests
RUN mvn -B -DskipTests package
#Creacion de imagen ligera para ejecutar Spring Boot Java 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
#Copiar el Jar generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar
#Exponer el puerto 8080
EXPOSE 8080
#Comando para ejecutar la aplicacion
ENTRYPOINT ["java","-jar","app.jar"]