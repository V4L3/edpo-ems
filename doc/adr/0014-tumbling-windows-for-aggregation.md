# 14 Tumbling Windows for Aggregation

## Context

In our energy management software, we use windows to group events with close temporal proximity for aggregation. Given the various types of windowing strategies, we need to choose the most suitable one for our application. This decision is also influenced by our chosen time semantics.

## Decision

We have decided to use "Tumbling Windows" for event aggregation due to their fixed-size, non-overlapping characteristics. 
These windows are based on the Event Time (as per [ADR 09](0009-Use-of-Event-Time-for-Kafka-Streams-in-Energy-Management-Software.md)) of the events. We have selected a window size of 5 seconds.

## Consequences

By choosing Tumbling Windows, we have adopted a windowing strategy that:

- **Simplifies the Aggregation Process:** Tumbling windows segregate events into distinct time frames, simplifying the aggregation process.

- **Ensures Correct Assignment:** Each event is properly assigned to its appropriate window based on its timestamp, improving the accuracy of our analytics.

- **Impacts Aggregation Frequency:** The selected window size (in this case, 5) affects the frequency and granularity of our aggregations. This choice should be reviewed periodically to ensure it continues to meet our evolving data analysis needs.

- **Requires Timely Event Processing:** With Event Time semantics, delayed or out-of-order events may be assigned to the wrong window, potentially impacting our analyses. Thus, our event-processing pipeline needs to ensure timely event processing.

We will monitor these consequences as we continue to refine our application.
