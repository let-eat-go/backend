#!/bin/bash

DOCKER_HUB_ID="kingseungil"
VERSION=$(git log -1 --pretty=%H)
ENV="dev"
BASENAME_SERVER="leteatgo-backend"
BASENAME_NGINX="leteatgo-nginx"
NAME_SERVER="$BASENAME_SERVER-$ENV"
NAME_NGINX="$BASENAME_NGINX-$ENV"
IMAGE_COMMIT_SERVER="$DOCKER_HUB_ID/$NAME_SERVER:$VERSION"
IMAGE_LATEST_SERVER="$DOCKER_HUB_ID/$NAME_SERVER:latest"
IMAGE_COMMIT_NGINX="$DOCKER_HUB_ID/$NAME_NGINX:$VERSION"
IMAGE_LATEST_NGINX="$DOCKER_HUB_ID/$NAME_NGINX:latest"

echo "Building image : api"
cd ../
./gradlew clean build -x test
#./gradlew build
docker build -f .docker/api/Dockerfile-api --build-arg ENV="$ENV" -t "$NAME_SERVER:$VERSION" .
docker tag "$NAME_SERVER:$VERSION" "$IMAGE_COMMIT_SERVER"
docker tag "$NAME_SERVER:$VERSION" "$IMAGE_LATEST_SERVER"
docker push "$IMAGE_COMMIT_SERVER"
docker push "$IMAGE_LATEST_SERVER"
echo "image built successfully."

echo "Building image : nginx"
docker build -f .docker/nginx/Dockerfile-nginx --build-arg ENV="$ENV" -t "$NAME_NGINX:$VERSION" .
docker tag "$NAME_NGINX:$VERSION" "$IMAGE_COMMIT_NGINX"
docker tag "$NAME_NGINX:$VERSION" "$IMAGE_LATEST_NGINX"
docker push "$IMAGE_COMMIT_NGINX"
docker push "$IMAGE_LATEST_NGINX"
echo "image built successfully."