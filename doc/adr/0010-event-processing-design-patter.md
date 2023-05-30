# 10 Event Processing Design Pattern

Date: 2020-05-23

## Status

Accepted

## Context

We need to define how the design pattern of the general event processing is defined.  
 

## Decision

We decided on the Single-Event Processing in a first step. As it is the most basic pattern, we are able to process each event in isolation. It is easy to recover from application failures and it is a straight forward approach to handle simple producer and consumer.

In a second step, after merging the data we decided on processing with local state with window aggregation to group aggregates and prepare it for the monitoring UI.
Interactive Queries are another enabler for the UI to reading the data directly from the output topic.


## Consequences

These architectural decisions promote scalability, fault tolerance, and usability. Single-event processing provides a straightforward and resilient approach to handling individual events. Incorporating local state and window aggregation enables effective data summarization and preparation for monitoring purposes. The utilization of interactive queries enhances the UI experience by providing direct access to the processed data, enabling timely insights, monitor the right energy consumption / production to the specific customer ID and decision-making.
