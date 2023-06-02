# 13 Stateful Streaming Processors

Date: 2020-05-23

## Status

Accepted

## Context

As Stateful Processing bring additional abstraction for representing data, data can be understood using more sophisticated mental models. There is the prossibility of taking snapshots (tables).

## Decision

We decided on using Event Grouper, Event Aggregator and Event Joiner as Stateful Streaming Processors

The event grouper is responsible for grouping related events based on certain criteria or shared attributes, in our example we group by customer ID. This grouping allows for processing events that are associated or related in some way, enabling more meaningful analysis or calculations.

The event aggregator performs aggregations on the grouped events. It calculates summary statistics, such as averages, totals, counts, or other aggregate values, within each group. The aggregator is responsible to aggregate new the values to an average load and maximum load.

The event joiner (Windowed Join) combines events from multiple streams or sources based on common attributes or keys. By joining events, you can correlate data from different sources, enabling more comprehensive analysis or combining information for a holistic view. Therefore, we use the Event Joiner to join the data of a producer to the consumer of a specific customer ID, to get insight of their energy management.

## Consequences

These stateful streaming processors collectively provide powerful capabilities for analyzing, summarizing, and correlating data. The event grouper allows for logical grouping of events, the event aggregator enables efficient computation of aggregate values within each group, and the event joiner facilitates combining data from multiple sources for a broader perspective. By employing these processors, we can derive more insightful and meaningful results from our streaming data, enabling advanced analysis and decision-making.
