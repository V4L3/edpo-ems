# 10 JSON Serialization for Kafka Streams

Date: 2020-05-23

## Status

Accepted

## Context

Our energy management software employs Kafka streams for real-time data processing. A key part of this process is data serialization, and we've been considering JSON versus Avro.

## Decision

We've decided to use JSON for serialization in our Kafka streams.

The main reasons for this decision are:

- **Ease of Use:** JSON's syntax is simple, human-readable, and universally accepted.
- **Compatibility:** JSON's native support in many languages eliminates the need for external libraries.
- **Schema Evolution:** JSON provides flexibility in handling evolving data models without upfront schema knowledge.
- **No Schema Registry:** JSON doesn't require a schema registry, reducing complexity and eliminating a potential single point of failure.

## Consequences

- **Size and Speed:** JSON is more verbose than Avro, which may affect performance in high volume data streams.
- **Lack of Schema Enforcement:** JSON doesn't enforce schemas as strictly as Avro, necessitating extra measures to ensure data consistency.
- **Future Migration Potential:** Changing needs might necessitate a shift to a different serialization format, which would require considerable resources.
