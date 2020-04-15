![Java CI with Gradle](https://github.com/princebansal/trappd/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

# trappd
Backend repository for trappd

### Build commands
Build project using
./gradlew clean build

## Docker commands
### Trappd Server
Command to build docker image <br>
`sudo docker build --build-arg JAR_FILE=server/build/libs/*.jar -t trappd/server .`
Command to run docker image <br>
`sudo docker run -p 8080:8080 -t trappd/server`
### Trappd Data Service
Command to build docker image <br>
`sudo docker build --build-arg JAR_FILE=data-service/build/libs/*.jar -t trappd/data-service .`
Command to run docker image <br>
`sudo docker run -p 8080:8081 -t trappd/data-service`