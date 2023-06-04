# Assignment 2: EMSai - EDPO Project Group 3

EMSai is an energy management systems provider.
The project was developed as part of the EDPO course at the University of St. Gallen.
For the second part we implemented several Prducer for Energy Production and Consumption data an Event Processor a monitoring Frontend and an actuator-service.

## Setup

To start the application, you need to have [Docker](https://www.docker.com/) installed.

There are two ways to start the application:

### Docker only setup
run `docker-compose -f docker-compose-part2.yml up --build`.

This should start all the services, the frontend and 3 instances of the energy-production service and 2 instances of the engergy-consumption service.
It might take a while for the services to start up and for the data to be visible in the frontend.

### Docker and IDE setup

run `docker-compose -f docker-compose-kafka.yml up --build` to start Kafka, Zookeeper and Kafdrop.
To ensure correct behavior, make sure all the containers are running before starting the services.

Change the Bootstrap Server Address in the [EventProcessingApp](event_processor/src/main/java/ch/unisg/ems/eventprocessor/EventProcessingApp.java) to `localhost:29092`.
Change the Bootstrap Address in the property files of the producer, consumer and actuator services to `localhost:29092`.

Then start the services individually in the following order:
1. producer-consumption
2. producer-production
3. event-processor
4. actuator
5. frontend

To simulate multiple Producer or Consumer multiple instances of the energy-production or energy-consumption service can be started with different load-profiles.
Load profiles are provided in the ressources folder.

## Architecture

### Architectural Decisions
ADR's can be found in the [doc/adr](doc/adr/) folder (specifically for Part 2 ADR 09 - 14).

### Overview
![architecture.png](doc%2Fimages%2Farchitecture_part2.png)

### Services

#### Energy Producer
[Energy Producer](producer_production) produces event to the kafka topic `pv_production` which are read from a .csv file.
Multiple instances of this service can be started with different load-profiles.

#### Energy Consumer
[Energy Consumer](producer_consumption) produces event to the kafka topic `energy_consumption` which are read from a .csv file.
Multiple instances of this service can be started with different load-profiles.

#### Event Processor
[Event Processor](event_processor) consumes events from the kafka topics `pv_production` and `energy_consumption` and performs various stateless and stateless processing.
The processed events are aggregated and materialized into KTables and produced into the topic `energy_data_by_customer`.

![topology.png](doc%2Fimages%2Ftopology.png)

The materialized tables are queried by the frontend to display the data in the chart.
The data can also be queried via the REST API.

GET `http://localhost:7071/productionMonitor`

GET `http://localhost:7071/consumptionMonitor`

#### Actuator Service
[Actuator](actuator) consumes events from the kafka topic `energy_data_by_customer`, analyzes the data and produces envents onto the kafka topic `actuator` with actions for the energy producer and consumer.

#### Frontend-monitoring
[Frontend-monitoring](frontend-monitoring) handles the frontend of the application.
The frontend periodically requests the event-processor for the latest data and displays it in a chart.
To run the frontend run `npm install` and `npm run dev` in the frontend folder.
The frontend is then available at [localhost:3001](http://localhost:3001/).

## Scenario

1. Start the application (see Setup)
2. Open the frontend at [localhost:3001](http://localhost:3001/)
3. Choose a customer
   ![frontend_customers.png](doc%2Fimages%2Ffrontend_customers.png)
4. Energy production and consumption data is displayed in the chart and updated every 4 seconds
   ![frontend_monitoring.png](doc%2Fimages%2Ffrontend_monitoring.png)
   (Since it is real data recorded over one year there might be longer stretches without and production data at night)
   For the docker only setup producer and consumer are instantiated for the customers "goeldAI technology" and "symplAIsAIt güven"
5. Click on "SHOW PRODUCERS" to examine the individual PV plants.
    ![frontend_producers.png](doc%2Fimages%2Ffrontend_producers.png)
5. The actuator service will send a message to the `actuators` topic
   (Can be examined via [Kafdrop](http://localhost:9000/topic/actuators/messages))
   ![actuators_kafdrop.png](doc%2Fimages%2Factuators_kafdrop.png)
