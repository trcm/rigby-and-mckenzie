create-test-queues:
	aws --debug --endpoint-url=http://localhost:4566 sqs create-queue --queue-name church.fifo --attributes FifoQueue=true

build-docker:
	sbt "project rigby" docker
	sbt "project mckenzie" docker

deploy:
	HASH=$(shell git rev-parse HEAD) \
	SERVICES="sqs" \
	docker-compose -f docker-compose.yml up

teardown:
	docker-compose -f docker-compose.yml down
