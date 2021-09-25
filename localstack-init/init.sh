#!/usr/bin/env bash

# Ensure localstack creates the queue on startup

AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test aws --region us-east-1 --endpoint-url=http://localhost:4566 sqs create-queue --queue-name church.fifo --attributes FifoQueue=true,RecieveMessageWaitTimeSeconds=20
