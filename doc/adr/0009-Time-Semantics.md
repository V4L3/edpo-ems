# 09 Time Semantics

Date: 2020-05-23

## Status

Accepted

## Context

As time is one of the most important concepts in stream processing, we need to define when we are using which time semantic, that the flow can continue.

## Decision

We decided on the Event time, when the event happened, due to the additional information needed when the energy was produced or consumed. If there are further implementations in the future of the application, we know exactly at which time the energy was produced to, per example calculate the energy price.

## Consequences

As we have a time stamp in the provided data, this decision has no meaningful consequences to the implementation of the whole application. As it is the most typical implementation type of time semantic, developers are used to handle it.
