# 12 Types of Single Event Processors

Date: 2020-05-23

## Status

Accepted

## Context

There are several types of single event processors. These types are used when incoming data needs to be modified, and preprocessed to get different output data.

## Decision

We decided on four different event processors:

Firstly, we are using the "Content Filter" to get the relevant attributes from the data, as the provided data has unnessecary information included.

Secondly, with the "Event Translator" we map units of the energy consumption / production from different Units to a defined unit, like "kW".

Thirdly, there will be a merging (Event Stream Processor) of the correct and mapped data, that the data can be directed to the aggregator for the next steps.

Lastly, we drop the failure measurement by filtering the values with an "Event Filter" which are not allowed or even possible.

## Consequences

These single event processors improve data quality, reduce unnecessary processing, enhance standardization and comparability, and streamline the overall data processing pipeline. They contribute to more accurate and reliable results, efficient resource utilization, and simplified downstream processing tasks.
