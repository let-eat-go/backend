#! /bin/bash

cd ../.docker

# nginx container가 없으면 실행
if [ $(docker ps | grep -c "leteatgo-nginx") -eq 0 ]; then
  echo "### Starting Nginx ###"
  docker-compose -f docker-compose-prod.yml up -d leteatgo-nginx
else
  echo "### Nginx already running ###"
fi

# redis container가 없으면 실행
if [ $(docker ps | grep -c "leteatgo-redis") -eq 0 ]; then
  echo "### Starting redis ###"
  docker-compose -f docker-compose-prod.yml up -d leteatgo-redis
else
  echo "### Redis already running ###"
fi

# rabbitMQ container가 없으면 실행
if [ $(docker ps | grep -c "leteatgo-rabbitmq") -eq 0 ]; then
  echo "### Starting rabbitMQ ###"
  docker-compose -f docker-compose-prod.yml up -d leteatgo-rabbitmq
else
  echo "### RabbitMQ already running ###"
fi

echo

IS_GREEN=$(docker ps | grep -c "green")

if [ "$IS_GREEN" -eq 1 ]; then
  echo "### GREEN => BLUE ###"

  echo "1. blue container up"
  docker-compose -f docker-compose-prod.yml up -d leteatgo-server-blue

  echo "Waiting for the blue application to fully start..."
  sleep 45

  echo "2. reload nginx"
  docker-compose -f docker-compose-prod.yml exec -T leteatgo-nginx /bin/bash -c "cp /etc/nginx/nginx-blue.conf /etc/nginx/conf.d/default.conf && nginx -s reload"

  MAX_ATTEMPTS=10
  ATTEMPTS=0

  while [ $ATTEMPTS -lt $MAX_ATTEMPTS ]; do
    echo "3. blue container health check"
    sleep 3

    REQUEST=$(curl https://backend-prod.leteatgo.site/)
    if [ -n "$REQUEST" ]; then
      echo "4. blue container health check success"
      break
    fi

    ATTEMPTS=$((ATTEMPTS+1))

    if [ $ATTEMPTS -eq $MAX_ATTEMPTS ]; then
      echo "Blue health check failed after $MAX_ATTEMPTS attempts. Reverting Nginx configuration."
      docker-compose -f docker-compose-prod.yml exec -T leteatgo-nginx /bin/bash -c "cp /etc/nginx/nginx-green.conf /etc/nginx/conf.d/default.conf && nginx -s reload"
      exit 1
    fi
  done

  echo "5. green container down"
  docker-compose -f docker-compose-prod.yml stop leteatgo-server-green
else
  echo "### BLUE => GREEN ###"

  echo "1. green container up"
  docker-compose -f docker-compose-prod.yml up -d leteatgo-server-green

  echo "Waiting for the green application to fully start..."
  sleep 45

  echo "2. reload nginx"
  docker-compose -f docker-compose-prod.yml exec -T leteatgo-nginx /bin/bash -c "cp /etc/nginx/nginx-green.conf /etc/nginx/conf.d/default.conf && nginx -s reload"

  MAX_ATTEMPTS=10
  ATTEMPTS=0

  while [ $ATTEMPTS -lt $MAX_ATTEMPTS ]; do
    echo "3. green container health check"
    sleep 3

    REQUEST=$(curl https://backend-prod.leteatgo.site/)
    if [ -n "$REQUEST" ]; then
      echo "4. green container health check success"
      break
    fi

    ATTEMPTS=$((ATTEMPTS+1))

    if [ $ATTEMPTS -eq $MAX_ATTEMPTS ]; then
      echo "Green health check failed after $MAX_ATTEMPTS attempts. Reverting Nginx configuration."
      docker-compose -f docker-compose-prod.yml exec -T leteatgo-nginx /bin/bash -c "cp /etc/nginx/nginx-blue.conf /etc/nginx/conf.d/default.conf && nginx -s reload"
      exit 1
    fi
  done

  echo "5. blue container down"
  docker-compose -f docker-compose-prod.yml stop leteatgo-server-blue
fi