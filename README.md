![Java CI with Gradle](https://github.com/princebansal/trappd/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

# trappd
Backend repository for trappd

### Build commands
Build project using
./gradlew clean build

## Docker commands
### Trappd Server
Command to build docker image <br>
`docker build --build-arg JAR_FILE=server/build/libs/*.jar -t trappd/server .`
Command to run docker image <br>
`docker run -p 8080:8080 -t trappd/server` <br>
Command to tag docker image <br>
`docker tag trappd/server:latest princebansal94/trappd:trappd-server-{version}` <br>
Command to push docker image to hub <br>
`docker push princebansal94/trappd:trappd-server-{version}` <br>
### Trappd Data Service <br>
Command to build docker image <br>
`docker build --build-arg JAR_FILE=data-service/build/libs/*.jar -t trappd/data-service .` <br>
Command to run docker image <br>
`docker run -p 8080:8081 -t trappd/data-service` <br>
Command to tag docker image <br>
`docker tag trappd/data-service:latest princebansal94/trappd:trappd-data-service-{version}}` <br>
Command to push docker image to hub <br>
`docker push princebansal94/trappd:trappd-data-service-{version}` <br>
### Trappd Engine
Command to build docker image <br>
`docker build -t trappd/engine .`
Command to run docker image <br>
`docker run -e DATA_SERVICE_URL={DATA_SERVICE_DOCKER_URL} trappd/engine` <br>
Command to tag docker image <br>
`docker tag trappd/engine:latest princebansal94/trappd:trappd-engine-{version}}` <br>
Command to push docker image to hub <br>
`docker push princebansal94/trappd:trappd-engine-{version}` <br>

### EC2
Command to login to EC2 instance <br>
`ssh -i <PEM_FILE_LOCATION> ec2-user@ec2-13-127-157-72.ap-south-1.compute.amazonaws.com` <br>

### RDS
Command to login to RDS
`mysql -h trappd-db-prod.cwr1iqiduzzs.ap-south-1.rds.amazonaws.com -uroot -p`

## Deploy
Deploy trappd-server
`docker pull princebansal94/trappd:trappd-server-{version}`
`docker run -p 80:8080 -e PROFILE_NAME -e AWS_ACCESS_KEY -e AWS_SECRET_KEY -e RDS_KEY -v /tmp:/tmp -d princebansal94/trappd:trappd-server-{version}`
Deploy trappd-data-service
`docker pull princebansal94/trappd:trappd-data-service-{version}`
`docker run -p 8081:8081 -e PROFILE_NAME -e AWS_ACCESS_KEY -e AWS_SECRET_KEY -e RDS_KEY -v /tmp:/tmp -d princebansal94/trappd:trappd-data-service-{version}`
Deploy trappd-engine
`docker pull princebansal94/trappd:trappd-engine-{version}`
`docker run -e DATA_SERVICE_URL={DATA_SERVICE_DOCKER_URL} princebansal94/trappd:trappd/engine`

## Logs
docker logs {container_name}

## Important Links
https://linuxconfig.org/how-to-install-docker-in-rhel-8
https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html
https://spring.io/guides/gs/spring-boot-docker/
https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Categories=categories%23compute&awsm.page-all-free-tier=1&awsf.Free%20Tier%20Types=*all
https://medium.com/dailyjs/a-guide-to-deploying-your-react-app-with-aws-s3-including-https-a-custom-domain-a-cdn-and-58245251f081
https://linuxconfig.org/how-to-install-docker-in-rhel-8