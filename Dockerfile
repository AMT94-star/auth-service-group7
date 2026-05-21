FROM eclipse-temurin:21
LABEL authors="Asli"

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 9000

CMD sh -c "java -jar target/*.jar"