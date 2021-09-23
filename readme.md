# Services

## Rigby

## McKenzie

# Building

# Deploying

## Credentials

# Limitations, Improvements, Assumptions

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
