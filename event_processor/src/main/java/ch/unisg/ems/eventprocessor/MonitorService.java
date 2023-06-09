package ch.unisg.ems.eventprocessor;

import ch.unisg.ems.eventprocessor.model.aggregations.ConsumptionAggregation;
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
        app.get("/consumptionMonitor", this::getConsumptionAggregations);
    }


    void getProductionAggregations(Context ctx) {
        Map<String, ProductionAggregation> monitor = new HashMap<>();

        ReadOnlyWindowStore<String, ProductionAggregation> store =
                streams.store(StoreQueryParameters.fromNameAndType("productionAggregations", QueryableStoreTypes.windowStore()));


        KeyValueIterator<Windowed<String>, ProductionAggregation> range = store.all();
        while (range.hasNext()) {
            KeyValue<Windowed<String>, ProductionAggregation> next = range.next();
            String id = next.key.key();
            ProductionAggregation productionAggregation = next.value;
            monitor.put(id, productionAggregation);
        }
        range.close();
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.json(monitor);
    }
    void getConsumptionAggregations(Context ctx) {
        Map<String, ConsumptionAggregation> monitor = new HashMap<>();

        ReadOnlyWindowStore<String, ConsumptionAggregation> store =
                streams.store(StoreQueryParameters.fromNameAndType("consumptionAggregations", QueryableStoreTypes.windowStore()));


        KeyValueIterator<Windowed<String>, ConsumptionAggregation> range = store.all();
        while (range.hasNext()) {
            KeyValue<Windowed<String>, ConsumptionAggregation> next = range.next();
            String id = next.key.key();
            ConsumptionAggregation consumptionAggregation = next.value;
            monitor.put(id, consumptionAggregation);
        }
        range.close();
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.json(monitor);
    }
}