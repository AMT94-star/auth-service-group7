LABEL authors="Asli"
FROM eclipse-temurin:21

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 9000

CMD sh -c "java -jar target/*.jar"