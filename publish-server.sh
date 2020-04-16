./gradlew clean build
sudo docker build --build-arg JAR_FILE=server/build/libs/*.jar -t trappd/server .
sudo docker tag trappd/server:latest princebansal94/trappd:trappd-server-$1
sudo docker push princebansal94/trappd:trappd-server-$1

