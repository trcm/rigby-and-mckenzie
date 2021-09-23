create-test-queues:
	aws --debug --endpoint-url=http://localhost:4566 sqs create-queue --queue-name church.fifo --attributes FifoQueue=true
