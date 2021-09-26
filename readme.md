# Services

Pendula code sample for Tom Midson.

This code uses the following:
- Scala (http4s, cats, circe)
- Docker/Docker compose
- SQS (Localstack)

## Rigby

Rigby is a simple Http4s application that connects to an SQS FIFO queue
and exposes one endpoint at `POST /hook`.

This hook endpoint accepts a JSON payload of the format:

```
    {
       firstName: string,
       lastName: string,
       email: string,
       phone: string,
       postcode: string
    }
```

eg.

```
{
    "firstName": "Paul",
    "lastName": "McCartney",
    "email": "paul@beatles.com",
    "phone": "011 999",
    "postcode": "NW8 DBD"
}
```

Assumptions about the service and its requests are listed below.

## McKenzie

McKenzie is a scala service that will connect to the same SQS FIFO queue
as Rigby. It will use long polling to receive any messages put onto the queue,

Once it has pulled the messages, it can remove them from the queue,
generate the required templates and send (in this case print to stdout) the
templated email response.

McKenzie will continue to loop using 20 second polling on the `church` queue.

One caveat here is that Mckenzie deletes the messages from the queue once it has
pulled them and ensured it can decode them. Ideally we'd want to probably
delete them once we have confirmation that they had been sent or that
they had been passed off to the system that would send them.

# Building

The docker images can be build using `build-docker` make target.
This will create two docker images named `rigby` and `mckenzie` tagged
with the current git revision hash.

# Deploying

The services can be deployed using the `deploy` target. This will
create the SQS service, create the chuck FIFO queue, and spin up
the services.  There is only very primitive service dependency checking,
ie. the services will just try and restart until they can create
a client with the SQS instance.

"Deploying" in this case is just running locally on the machine. In a proper
setup there would be some orchetration of resources needed for the services,
likely through an IaC tool such as terraform or pulumi. This configuration would
ideally create the SQS queue before the services are run for the first time.

The services and be torn down using the `teardown` target.

## Credentials

All AWS credentials have been hard coded to "test" for the services.

# Limitations, Improvements, Assumptions

There are a number of TODOs in the code indicating where there is room for
improvement, however, I've listed a number of items that could be improved
and listed any assumptions made during development.

## Error Handling

The error handling in this application is fairly naive, in a proper application
Throwables would be wrapped and put into and an Error type designed for the
service.

The error handling around the AWS java sdk is also pretty light, it would need
to be improved before deploying.

## Validation

There is very little (read, no) validation for the request payload on the Rigby
service webhook.  The spec indicates that this data is coming from an external
CRM system so I'm assuming well formed data is being sent. Real world there
should probably be a bit more validation on the input.

## Authentication

The webhook endpoint has no authentication on it. Ideally it should be secured
if its a public facing endpoint so the queue doesn't potentially get filled
with garbage data.

## Data Storage

SQS was chosen as the queuing system. Localstack data persistence was used by
specifying the data dir in the compose, but this is handled by amazon in the
real service. The documentation states:

```
Amazon SQS stores all message queues and messages within a single, highly-available AWS region with multiple redundant Availability Zones (AZs), so that no single computer, network, or AZ failure can make messages inaccessible. For more information, see Regions and Availability Zones in the Amazon Relational Database Service User Guide.
```

Another self hosted solution may require more thought into data persistence
and reliability.

## Testing

Ideally there would be tests that tested the end to end functionality of
these services.

## Configuration

Ideally these services would be configurable via environment variables to allow
the queues, ports, etc to be configured when the service is started.

There would also be validation for things such as whether the queue being
passed to the service actually exists, or perhaps some code that would create it.
Ideally this creation would happen as part of an infrastructure deployment.

## Code structure

There is some repeated code for setting up helpers. In a real world application
this would probably be refactored into its own module (ie. SQS Helpers, data types).

## Templating

In a real application a proper templating engine would be used to render a nicer
looking email. In this application just a string template is used.

# Test Payloads

```
curl --location --request POST 'http://localhost:8080/hook' \
--header 'Content-Type: text/plain' \
--data-raw '{
    "firstName": "elenor",
    "lastName": "rigby",
    "email": "erigby@abbey-road.com",
    "phone": "011 999",
    "postcode": "NW8 DBD"
}'
```

This will trigger an "Accepted" template response from McKenzie.

```
curl --location --request POST 'http://localhost:8080/hook' \
--header 'Content-Type: text/plain' \
--data-raw '{
    "firstName": "elenor",
    "lastName": "rigby",
    "email": "erigby@abbey-road.com",
    "phone": "011 999",
    "postcode": "MS8 DBD"
}'
```

This will trigger a "Declined" template response from McKenzie

```
curl --location --request POST 'http://localhost:8080/hook' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Paul",
    "lastName": "McCartney",
    "email": "paul@beatles.com",
    "phone": "011 999"
}'
```

This should return with `400 - Bad Request`
