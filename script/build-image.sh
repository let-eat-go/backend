#!/bin/bash

VERSION="0.0.1"

echo "Building image : api"
cd ../
./gradlew clean build -x test
#./gradlew build
echo "Dockerfile을 이용하여 이미지 생성"
docker build -f .docker/api/Dockerfile-dev -t "leteatgo-dev:$VERSION" .
echo "생성된 이미지를 docker hub에 업로드"
docker tag "leteatgo-dev:$VERSION" "kingseungil/leteatgo-dev:$VERSION"
echo "docker push"
docker push "kingseungil/leteatgo-dev:$VERSION"

echo "image built successfully."docker pull kingseungil/leteatgo-dev