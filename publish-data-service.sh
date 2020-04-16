./gradlew clean build
sudo docker build --build-arg JAR_FILE=data-service/build/libs/*.jar -t trappd/data-service .
sudo docker tag trappd/data-service:latest princebansal94/trappd:trappd-data-service-$1
sudo docker push princebansal94/trappd:trappd-data-service-$1

