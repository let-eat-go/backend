#!/bin/bash

# === for local test ===
API_NAME='leteatgo-backend-dev'
NGINX_NAME='leteatgo-nginx-dev'
RABBITMQ_NAME='leteatgo-rabbitmq-dev'
VERSION='latest'

echo "set env"
set -a
source .docker/.env
set +a

echo "> gradle clean build"
./gradlew --build-cache clean build

echo "> Build api docker image"
docker build -f .docker/api/Dockerfile-api -t $DOCKER_REPO/$API_NAME:$VERSION . --build-arg ENV=dev

echo "> Push api docker image to hub"
docker push $DOCKER_REPO/$API_NAME:$VERSION

echo "> Build nginx docker image"
docker build -f .docker/nginx/Dockerfile-nginx -t $DOCKER_REPO/$NGINX_NAME:$VERSION . --build-arg ENV=dev

echo "> Push nginx docker image to hub"
docker push $DOCKER_REPO/$NGINX_NAME:$VERSION

echo "> Build rabbitmq docker image"
docker build -f .docker/rabbitmq/Dockerfile-rabbitmq -t $DOCKER_REPO/$RABBITMQ_NAME:$VERSION .

echo "> Push rabbitmq docker image to hub"
docker push $DOCKER_REPO/$RABBITMQ_NAME:$VERSION