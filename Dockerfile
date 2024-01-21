FROM  eclipse-temurin:17-jdk-focal

RUN mkdir /app

#COPY app.jar /app/app.jar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
COPY docker-compose.yml /app/docker-compose.yml

WORKDIR /app

EXPOSE 8098

ENTRYPOINT ["java","-jar","app.jar"]



