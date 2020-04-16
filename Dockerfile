FROM openjdk:8-jdk-alpine
RUN addgroup -S trappd && adduser -S trappd -G trappd
USER trappd:trappd
# Setup Working Directory
#RUN mkdir -p /trappd
#WORKDIR /trappd

#ARG DEPENDENCY
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app

# Copy app jar file from build command inside image
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# Start the Java process
ENTRYPOINT java -Dspring.profiles.active=$PROFILE_NAME -jar app.jar
#ENTRYPOINT ["java", "-Dspring.profiles.active=$PROFILE_NAME", "-cp", ":app/lib","com.easycompany.trappd.TrappdApplication"]

EXPOSE 8080
EXPOSE 6565

