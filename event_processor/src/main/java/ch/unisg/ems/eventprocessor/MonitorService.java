package ch.unisg.ems.eventprocessor;

import ch.unisg.ems.eventprocessor.model.aggregations.ProductionAggregation;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.*;


public class MonitorService {

    private final HostInfo hostInfo;
    private final KafkaStreams streams;

    // Logger
    /*private static final Logger log = LoggerFactory.getLogger(MonitorService.class);*/

    MonitorService(HostInfo hostInfo, KafkaStreams streams) {
        this.hostInfo = hostInfo;
        this.streams = streams;
    }




    // Start the Javalin web server and configure routes
    void start() {

        // Create and start a Javalin server instance with the specified port
        Javalin app = Javalin.create().start(hostInfo.port());

        // Define a route for querying in the key-value store
        app.get("/productionMonitor", this::getProductionAggregations);
        app.get("/productionEvents", this::getProductionEvents);
        /*app.get("/fixationClickMonitor", this::getFixationClickCount);*/

        // Make it accessible through a GUI
        /*app.config.addStaticFiles("/public");*/
    }


    void getProductionAggregations(Context ctx) {
        Map<String, ProductionAggregation> monitor = new HashMap<>();

        ReadOnlyWindowStore<String, ProductionAggregation> store =
                streams.store(StoreQueryParameters.fromNameAndType("productionAggregations", QueryableStoreTypes.windowStore()));


        KeyValueIterator<Windowed<String>, ProductionAggregation> range = store.all();
        while (range.hasNext()) {
            KeyValue<Windowed<String>, ProductionAggregation> next = range.next();
            String aoi = next.key.key();
            ProductionAggregation fixationStats = next.value;
            monitor.put(aoi, fixationStats);
        }
        range.close();
        ctx.json(monitor);
    }


   void getProductionEvents(Context ctx) {
        Map<String, Long> monitor = new HashMap<>();

        ReadOnlyWindowStore<String, Long> store = streams.store(
                StoreQueryParameters.fromNameAndType(
                        "productionEvents",
                        QueryableStoreTypes.windowStore()));

        KeyValueIterator<Windowed<String>, Long> range = store.all();
        while (range.hasNext()) {
            KeyValue<Windowed<String>, Long> next = range.next();
            String aoi = next.key.key();
            long count = next.value;
            monitor.put(aoi, count);
        }
        range.close();
        ctx.json(monitor);
    }

/*
    void getFixationClickCount(Context ctx) {
        Map<String, FixationClick> monitor = new HashMap<>();

        ReadOnlyKeyValueStore<String, FixationClick> store = streams.store(
                StoreQueryParameters.fromNameAndType(
                        "FixationClickStats",
                        QueryableStoreTypes.keyValueStore()));

        KeyValueIterator<String, FixationClick> range = store.all();
        while (range.hasNext()) {
            KeyValue<String, FixationClick> next = range.next();
            String aoi = next.key;
            monitor.put(aoi, next.value);
        }
        range.close();
        ctx.json(monitor);
    }*/

}