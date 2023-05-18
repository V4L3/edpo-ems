package ch.unisg.ems.eventprocessor.serialization.json;

import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.model.aggregations.ProductionAggregation;
import ch.unisg.ems.eventprocessor.model.join.ProductionEventWithCustomer;
import ch.unisg.ems.eventprocessor.serialization.ProductionEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

    public static Serde<Customer> Customer() {
        JsonSerializer<Customer> serializer = new JsonSerializer<>();
        JsonDeserializer<Customer> deserializer = new JsonDeserializer<>(Customer.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ProductionEventWithCustomer> ProductionEventWithCustomer() {
        JsonSerializer<ProductionEventWithCustomer> serializer = new JsonSerializer<>();
        JsonDeserializer<ProductionEventWithCustomer> deserializer = new JsonDeserializer<>(ProductionEventWithCustomer.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ProductionAggregation> ProductionAggregation() {
        JsonSerializer<ProductionAggregation> serializer = new JsonSerializer<>();
        JsonDeserializer<ProductionAggregation> deserializer = new JsonDeserializer<>(ProductionAggregation.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ProductionEvent> ProductionEvent() {
        JsonSerializer<ProductionEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<ProductionEvent> deserializer = new JsonDeserializer<>(ProductionEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}