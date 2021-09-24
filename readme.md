# Services

## Rigby

## McKenzie

# Building

# Deploying

## Credentials

# Limitations, Improvements, Assumptions

## Error Handling

The error handling in this application is fairly naive, in a proper application
Throwables would be wrapped and put into and an Error type designed for the
service.

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

## Code structure

There is some repeated code for setting up helpers. In a real world application
this would probably be refactored into its own module (ie. SQS Helpers, data types).

## Templating

In a real application a proper templating engine would be used to render a nicer
looking email. In this application just a string template is used.

# Test Payloads

```
{
    "firstName": "elenor",
    "lastName": "rigby",
    "email": "erigby@abbey-road.com",
    "phone": "0118 999",
    "postcode": "NW8 9BD"
}
```
