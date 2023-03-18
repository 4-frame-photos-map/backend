FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/four_cut_photos_map-0.0.1-SNAPSHOT.jar
#ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar","/app.jar"]