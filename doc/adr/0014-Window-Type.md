# 14 Window Type

Date: 2020-05-23

## Status

Accepted

## Context

Windows are used to group events with close temporal proximity. There are several Window Types. Therefore, our group has to choose the most suitable for the aggregation.

## Decision

We decided on the "Tumbling Windows", due to the fixed-sized windows that never overlaps and are based on a single property. The decided window size is 5.

## Consequences

By opting for Tumbling Windows, the group has selected a windowing strategy that provides regular, fixed-sized windows without overlap. This decision simplifies the aggregation process and ensures that events are correctly assigned to the appropriate window for analysis. The choice of a specific window size, in this case, 5, depends on the specific context and requirements of the application.
