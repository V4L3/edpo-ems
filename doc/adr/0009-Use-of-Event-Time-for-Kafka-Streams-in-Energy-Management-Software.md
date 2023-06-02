# 09 Use of Event Time for Kafka Streams in Energy Management Software

Date: 2020-05-23

## Status

Accepted

## Context

Our energy management software uses Kafka streams for real-time data processing. 
Given the temporal characteristics of our data, the choice of time semantics is a critical aspect of our system design.

## Decision

We have decided to use Event Time semantics for our Kafka streams topology. This means we will timestamp our events based on when they were recorded, not when they are processed.

## Reasons

- **Real-Life Time Context:** The use of Event Time aligns the data with the real-world timeline, which is meaningful and important for our customers. This helps us ensure that our analytics reflect real-life situations and patterns.

- **Consistency Across Streams:** Event Time enables consistent processing across all streams, even in the face of network latency or system downtime. This means events can be accurately compared and correlated across different streams.

- **Handling of Delayed Events:** Event Time allows for handling of delayed events or data reprocessing in a way that maintains the integrity of our analytics.

## Consequences

- **Delayed Processing:** There may be delays in processing if events arrive late, as we wait for events to fill the time window.

- **Complex Error Handling:** Handling out-of-order events or missed windows can be more complex with Event Time.

- **Need for Accurate Clocks:** Event Time requires the clocks of event-producing systems to be accurately synchronized.

We will continuously review this decision to ensure it aligns with our evolving system requirements and provides the most value for our customers.
