package ch.unisg.ems.eventprocessor.topology;

import ch.unisg.ems.eventprocessor.loadprofile.UnitConverter;
import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.model.aggregations.ConsumptionAggregation;
import ch.unisg.ems.eventprocessor.model.aggregations.ProductionAggregation;
import ch.unisg.ems.eventprocessor.model.join.AggregatedProductionConsumption;
import ch.unisg.ems.eventprocessor.model.join.ConsumptionEventWithCustomer;
import ch.unisg.ems.eventprocessor.model.join.ProductionEventWithCustomer;
import ch.unisg.ems.eventprocessor.serialization.ConsumptionEvent;
import ch.unisg.ems.eventprocessor.serialization.ProductionEvent;
import ch.unisg.ems.eventprocessor.serialization.json.ConsumptionEventSerdes;
import ch.unisg.ems.eventprocessor.serialization.json.JsonSerdes;
import ch.unisg.ems.eventprocessor.serialization.json.ProductionEventSerdes;
import ch.unisg.ems.eventprocessor.timestampExtractors.ConsumptionTimestampExtractor;
import ch.unisg.ems.eventprocessor.timestampExtractors.ProductionTimestampExtractor;
import com.mitchseymour.kafka.serialization.avro.AvroSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;

public class EmsTopology {
    private static final Double MAX_PRODUCTION_LOAD = 100.0;
    private static final Double MAX_CONSUMPTION_LOAD = 100.0;
    private static final Double MIN_CONSUMPTION_LOAD = 0.0;

    private static UnitConverter unitConverter = new UnitConverter();

    public static Topology build() {
        // Serde<EntityProductionEvent> fixationSerde = jsonSerde(Fixation.class);
        // the builder is used to construct the topology
        StreamsBuilder builder = new StreamsBuilder();

        System.out.println("access yolo stream");

        // start streaming production events using our custom value serdes.
        KStream<byte[], ProductionEvent> streamProduction =
                builder.stream("pv_production",
                        Consumed.with(Serdes.ByteArray(), new ProductionEventSerdes())
                                .withTimestampExtractor(new ProductionTimestampExtractor()));
        streamProduction.print(Printed.<byte[], ProductionEvent>toSysOut().withLabel("pv_production-event-stream"));

        // start streaming consumption events using our custom value serdes.
        KStream<byte[], ConsumptionEvent> streamConsumption =
                builder.stream("energy_consumption",
                        Consumed.with(Serdes.ByteArray(), new ConsumptionEventSerdes())
                                .withTimestampExtractor(new ConsumptionTimestampExtractor()));
        streamConsumption.print(Printed.<byte[], ConsumptionEvent>toSysOut().withLabel("energy_consumption-event-stream"));

        /**
         * Consume global ktable to inititalize customer data
         */
        GlobalKTable<String, Customer> customerTable =
                builder.globalTable("customers", Consumed.with(Serdes.String(), JsonSerdes.Customer()));

        // Apply content filter to production events, Keep only relevant attributes
        KStream<byte[], ProductionEvent> contentFilteredProductionEvents =
                streamProduction.mapValues(
                        (event) -> {
                            ProductionEvent contentFilteredProductionEvent = new ProductionEvent();
                            contentFilteredProductionEvent.setPvId(event.getPvId());
                            contentFilteredProductionEvent.setCustomerId(event.getCustomerId());
                            contentFilteredProductionEvent.setLoad(event.getLoad());
                            contentFilteredProductionEvent.setTimestamp(event.getTimestamp());
                            contentFilteredProductionEvent.setUnitLoad(event.getUnitLoad());
                            return contentFilteredProductionEvent;
                        });

        // Apply content filter to production events, keep only relevant attributes
        KStream<byte[], ConsumptionEvent> contentFilteredConsumptionEvents =
                streamConsumption.mapValues(
                        (event) -> {
                            ConsumptionEvent contentFilteredConsumptionEvent = new ConsumptionEvent();
                            contentFilteredConsumptionEvent.setConsumerId(event.getConsumerId());
                            contentFilteredConsumptionEvent.setCustomerId(event.getCustomerId());
                            contentFilteredConsumptionEvent.setLoad(event.getLoad());
                            contentFilteredConsumptionEvent.setTimestamp(event.getTimestamp());
                            contentFilteredConsumptionEvent.setUnitLoad(event.getUnitLoad());
                            return contentFilteredConsumptionEvent;
                        });
        /**
         * Unit converter for production events
         * divide into 2 branches -> convert unit if needed -> merge branches
         */

        // match all events that have kW as unit
        Predicate<byte[], ProductionEvent> unitKW = (key, event) -> event.getUnitLoad().equals("kW");

        // match all other events
        Predicate<byte[], ProductionEvent> unitNotKW = (key, event) -> !event.getUnitLoad().equals("kW");

        // branch based on load unit
        KStream<byte[], ProductionEvent>[] branches = contentFilteredProductionEvents.branch(unitKW, unitNotKW);

        // load unit: kW
        KStream<byte[], ProductionEvent> kWStream = branches[0];
        kWStream.print(Printed.<byte[], ProductionEvent>toSysOut().withLabel("event-kW"));

        // load unit: not kW
        KStream<byte[], ProductionEvent> notKwStream = branches[1];
        notKwStream.print(Printed.<byte[], ProductionEvent>toSysOut().withLabel("events-not-kW"));

        // for events where the load unit is not kW convert the load to kW
        KStream<byte[], ProductionEvent> convertedStream =
                notKwStream.mapValues(
                        (event) -> unitConverter.convertToKW(event));

        // merge the two streams
        KStream<byte[], ProductionEvent> mergedProduction = kWStream.merge(convertedStream);

        /**
         * Filter events that contain not allowed measurements
         */
        // filter out events with a load greater than 100 kW (measurement error since the pv system is only 100 kW)
        KStream<byte[], ProductionEvent> filteredProduction =
                mergedProduction.filterNot(
                        (key, event) -> event.getLoad() > MAX_PRODUCTION_LOAD);


        KStream<byte[], ConsumptionEvent> filteredConsumption =
                contentFilteredConsumptionEvents.filterNot(
                        (key, event) -> event.getLoad() < MIN_CONSUMPTION_LOAD
                );

        // TO TEST OUT JAVALIN - TABLE WITH PRODUCTION EVENTS COUNT
        // Window config for production events
        TimeWindows tumblingWindowClicks =
                TimeWindows.of(Duration.ofSeconds(5)).grace(Duration.ofSeconds(1));

        //Group clicks by AOI, Window by tumblingWindowClicks, Aggregage:count, Materialize, suppress
        KTable<Windowed<String>, Long> productionEvents =
                filteredProduction
                        // group by Customer ID
                        .groupBy((key, value) -> value.getCustomerId(),
                                Grouped.<String, ProductionEvent>with(Serdes.String(), JsonSerdes.ProductionEvent()))
                        // windowing by config
                        .windowedBy(tumblingWindowClicks)
                        // windowed aggregation
                        .count(Materialized.as("productionEvents"))
                        /*.aggregate(
                                productionEventsInitializer,
                                productionEventAggregator,
                                Materialized.<String, ProductionAggregation, WindowStore<Bytes, byte[]>>as("productionEvents")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(JsonSerdes.ProductionAggregation())
                        )*/
                        // suppress
                        .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

        // filter out events with a load greater than 100 kW (measurement error since the pv system is only 100 kW)
        /*KStream<byte[], EntityConsumptionEvent> filteredConsumption =
                contentFilteredConsumptionEvents.filterNot(
                        (key, event) -> event.getLoad() > MAX_CONSUMPTION_LOAD);*/

        /**
         * Join customer information to Production and Consumption streams
         */
        /*COMMENTED THIS BECAUSE JOINING WITH CUSTOMERS DOES NOT WORK YET*/
        KeyValueMapper<byte[], ProductionEvent, String> keyMapperProduction =
                (leftKey, entityProductionEvent) -> String.valueOf(entityProductionEvent.getCustomerId());

        // join the productionevent stream to the customers global ktable
        ValueJoiner<ProductionEvent, Customer, ProductionEventWithCustomer> customerJoinerProduction =
                (entityProductionEvent, customer) -> new ProductionEventWithCustomer(entityProductionEvent, customer);
        KStream<byte[], ProductionEventWithCustomer> productionWithCustomer = filteredProduction.join(customerTable, keyMapperProduction, customerJoinerProduction);
        productionWithCustomer.print(Printed.<byte[], ProductionEventWithCustomer>toSysOut().withLabel("with-customer"));


        KeyValueMapper<byte[], ConsumptionEvent, String> keyMapperConsumption =
                (leftKey, entityConsumptionEvent) -> String.valueOf(entityConsumptionEvent.getCustomerId());

        // join the consumption event stream to the customers global ktable
        ValueJoiner<ConsumptionEvent, Customer, ConsumptionEventWithCustomer> customerJoinerConsumption =
                (entityConsumptionEvent, customer) -> new ConsumptionEventWithCustomer(entityConsumptionEvent, customer);
        KStream<byte[], ConsumptionEventWithCustomer> consumptionWithCustomer = filteredConsumption.join(customerTable, keyMapperConsumption, customerJoinerConsumption);
        consumptionWithCustomer.print(Printed.<byte[], ConsumptionEventWithCustomer>toSysOut().withLabel("with-customer"));


        /**
         * Stateful processing for joined production stream
         * group by customer and aggregate average load over tumbling window
         */
        // Window config for production events
        TimeWindows tumblingWindowProductionEvents =
                TimeWindows.of(Duration.ofSeconds(10)).grace(Duration.ofSeconds(1));
        // aggregation: average load, max load, count
        Initializer<ProductionAggregation> productionEventsInitializer = () -> new ProductionAggregation(0,0, 0);

        Aggregator<String, ProductionEvent, ProductionAggregation> productionEventAggregator = (key, production, productionAggregation) -> {
            int newProductionEventCount = productionAggregation.getCount() + 1;
            double newMaxLoad = Math.max(productionAggregation.getMaxLoad(), production.getLoad());
            double newAverageLoad = (productionAggregation.getAverageLoad() * (newProductionEventCount - 1) + production.getLoad()) / newProductionEventCount;
            return new ProductionAggregation(newAverageLoad, newMaxLoad, newProductionEventCount);
        };

        // Window config for consumption events
        TimeWindows tumblingWindowConsumptionEvents =
                TimeWindows.of(Duration.ofSeconds(10)).grace(Duration.ofSeconds(1));
        // aggregation: average load, max load, count
        Initializer<ConsumptionAggregation> consumptionEventsInitializer = () -> new ConsumptionAggregation(0,0, 0);

        Aggregator<String, ConsumptionEventWithCustomer, ConsumptionAggregation> consumptionEventAggregator = (key, consumption, consumptionAggregation) -> {
            int newConsumptionEventCount = consumptionAggregation.getCount() + 1;
            double newMaxLoad = Math.max(consumptionAggregation.getMaxLoad(), consumption.getEntityConsumptionEvent().getLoad());
            double newAverageLoad = (consumptionAggregation.getAverageLoad() * (newConsumptionEventCount - 1) + consumption.getEntityConsumptionEvent().getLoad()) / newConsumptionEventCount;
            return new ConsumptionAggregation(newAverageLoad, newMaxLoad, newConsumptionEventCount);
        };

        // Group production events by customer id, Window by tumblingWindowFixations, Aggregate, Materialize, suppress
        KTable<Windowed<String>, ProductionAggregation> productionAggregationTable =
                filteredProduction
                        // group by AOI
                        .groupBy((key, value) -> value.getCustomerId(),
                                Grouped.<String, ProductionEvent>with(Serdes.String(), JsonSerdes.ProductionEvent()))
                        // windowing by config
                        .windowedBy(tumblingWindowProductionEvents)
                        // windowed aggregation
                        .aggregate(
                                productionEventsInitializer,
                                productionEventAggregator,
                                Materialized.<String, ProductionAggregation, WindowStore<Bytes, byte[]>>as("productionAggregations")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(JsonSerdes.ProductionAggregation())
                        )
                        // suppress
                        .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

        // Group consumption events by customer id, Window by tumblingWindow, Aggregate, Materialize, suppress
        KTable<Windowed<String>, ConsumptionAggregation> consumptionAggregationTable =
                consumptionWithCustomer
                        // group by customer ID
                        .groupBy((key, value) -> value.getCustomer().getId(),
                                Grouped.<String, ConsumptionEventWithCustomer>with(Serdes.String(), JsonSerdes.ConsumptionEventWithCustomer()))
                        // windowing by config
                        .windowedBy(tumblingWindowConsumptionEvents)
                        // windowed aggregation
                        .aggregate(
                                consumptionEventsInitializer,
                                consumptionEventAggregator,
                                Materialized.<String, ConsumptionAggregation, WindowStore<Bytes, byte[]>>as("consumptionAggregations")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(JsonSerdes.ConsumptionAggregation())
                        )
                        // suppress
                        .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));


        /**
         * Joining Production and Consumption streams
         */

        //convert KTables to Streams
        KStream<String, ConsumptionAggregation> aggregatedConsumptionStream = consumptionAggregationTable
                .toStream()
                .map(
                        (windowedKey, value) -> {
                            return KeyValue.pair(windowedKey.key(), value);
                        });

        KStream<String, ProductionAggregation> aggregatedProuctionStream = productionAggregationTable
                .toStream()
                .map(
                        (windowedKey, value) -> {
                            return KeyValue.pair(windowedKey.key(), value);
                        });

        aggregatedProuctionStream.print(Printed.<String, ProductionAggregation>toSysOut().withLabel("aggregatedProductionStream"));
        aggregatedConsumptionStream.print(Printed.<String, ConsumptionAggregation>toSysOut().withLabel("aggregatedConsumptionStream"));

        StreamJoined<String, ProductionAggregation, ConsumptionAggregation> joinParams =
                StreamJoined.with(Serdes.String(), JsonSerdes.ProductionAggregation(), JsonSerdes.ConsumptionAggregation())
                        .withKeySerde(Serdes.String())
                        .withValueSerde(JsonSerdes.ProductionAggregation());

        JoinWindows joinWindows =
                JoinWindows
                        .of(Duration.ofSeconds(5))
                        .grace(Duration.ofSeconds(1));


        KStream<String, AggregatedProductionConsumption> productionConsumptionJoined =
                aggregatedProuctionStream.join(
                        aggregatedConsumptionStream,
                        (production, consumption) -> new AggregatedProductionConsumption(production, consumption),
                        joinWindows,
                        joinParams

                );

        // send the joined stream to the "pv_production_clean" topic
        productionConsumptionJoined.to(
                "pv_production_clean",
                Produced.with(
                        Serdes.String(),
                        JsonSerdes.AggregatedProductionConsumption()
                        // registryless Avro Serde
                        ));

        return builder.build();
    }
}
