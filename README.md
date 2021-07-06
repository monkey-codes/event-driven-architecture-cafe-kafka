# Event Driven Architecture Cafe
Sample project that demonstrates an event driven architecture backed by kafka. The system consists
of 4 microservices: *waiter, kitchen, stockroom and cashier.* Each service is built
on top of the [Axon Framework](https://axoniq.io/) and uses [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
for persistence.

![](https://res.cloudinary.com/monkey-codes/image/upload/v1617752028/event-driven-architecture-cafe/cafe.gif)

## Architecture 

Integration between the services are achieved via Kafka Topics. Each service produces public events by publishing to
an events-topic for the given service in the [Avro](https://avro.apache.org/docs/current/) format (e.g. waiter-events-value). Each topic
can hold multiple event types and each event has a separate versioned Avro schema. The schema for the topic then uses 
[a union with schema references](https://www.confluent.io/blog/multiple-event-types-in-the-same-kafka-topic/)  to define the set of event
types that can be published on the topic. The benefit of mixing event types on the same topic is that events related to
a specific aggregate/entity will be ordered within the topic. Services are free to collaborate by consuming events from the topics of other services.

![](https://res.cloudinary.com/monkey-codes/image/upload/v1625540227/event-driven-architecture-cafe/event-driven-cafe-kafka-architecture.png)

## Build & Run

[Batect System Requirements](https://batect.dev/docs/getting-started/requirements) include:
* Docker
* Java 8 or newer
* Bash
* curl

Ensure that docker has at least 4GB of memory available, the batect command will launch a kafka server with schema registry and a separate
docker container for each service.
```
$ ./batect go
```

The UI is available at [http://localhost:4200](http://localhost:4200).
Kafka/Schema registry UI can be found at [http://localhost:3030](http://localhost:3030).