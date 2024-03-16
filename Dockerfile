FROM  eclipse-temurin:17-jdk-focal

WORKDIR /app

COPY build/libs/*.jar /app/app.jar
RUN ls -la /app/

EXPOSE 8098
ENTRYPOINT ["java", "-jar", "app.jar"]