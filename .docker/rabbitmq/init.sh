#!/bin/sh

set -a
source /.env
set +a

# Create Default RabbitMQ setup
( sleep 30; \
# Create users
rabbitmqctl add_user $RABBITMQ_USER $RABBITMQ_PASS; \

# Set user rights
rabbitmqctl set_user_tags $RABBITMQ_USER administrator; \

# Set vhost permissions
rabbitmqctl set_permissions -p / $RABBITMQ_USER ".*" ".*" ".*"; \
) &

rabbitmq-server $@