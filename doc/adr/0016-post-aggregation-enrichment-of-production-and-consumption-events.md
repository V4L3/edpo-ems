# 016 Post-Aggregation Enrichment of Production and Consumption Events

Date: 2020-05-27

## Status

Accepted

## Context
Our energy management software uses Kafka streams to handle real-time production events, which are aggregated and enriched with customer data.

## Decision
We've decided to aggregate production events first and then enrich them by joining with customer data.
Our main reason for this decision are the following:

- Performance Efficiency: Aggregating first reduces the computational overhead during the join process.
- Single Point Enrichment: Customer data is added only once, reducing the risk of data inconsistency.
- Data Consistency: Changes in customer data need to update only the latest aggregated records.
- Separate Querying: The ability to query aggregated production events independently from enriched data.

## Consequences
- Delay in Data Enrichment: A slight delay in enrichment due to the initial aggregation step.
- Dependency on Aggregation: If issues occur during aggregation, it could impact the enrichment process.
- Data Storage Requirements: Extra storage capacity is required to store aggregated production events separately.