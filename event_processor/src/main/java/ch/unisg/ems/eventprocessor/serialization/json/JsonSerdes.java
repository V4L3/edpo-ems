package ch.unisg.ems.eventprocessor.serialization.json;

import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.model.aggregations.ConsumptionAggregation;
import ch.unisg.ems.eventprocessor.model.aggregations.ProductionAggregation;
import ch.unisg.ems.eventprocessor.model.join.AggregatedProductionConsumption;
import ch.unisg.ems.eventprocessor.model.join.AggregatedProductionConsumptionWithCustomer;
import ch.unisg.ems.eventprocessor.model.join.ConsumptionEventWithCustomer;
import ch.unisg.ems.eventprocessor.model.join.ProductionEventWithCustomer;
import ch.unisg.ems.eventprocessor.serialization.ConsumptionEvent;
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

    public static Serde<ConsumptionEventWithCustomer> ConsumptionEventWithCustomer() {
        JsonSerializer<ConsumptionEventWithCustomer> serializer = new JsonSerializer<>();
        JsonDeserializer<ConsumptionEventWithCustomer> deserializer = new JsonDeserializer<>(ConsumptionEventWithCustomer.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ProductionAggregation> ProductionAggregation() {
        JsonSerializer<ProductionAggregation> serializer = new JsonSerializer<>();
        JsonDeserializer<ProductionAggregation> deserializer = new JsonDeserializer<>(ProductionAggregation.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
    public static Serde<ConsumptionAggregation> ConsumptionAggregation() {
        JsonSerializer<ConsumptionAggregation> serializer = new JsonSerializer<>();
        JsonDeserializer<ConsumptionAggregation> deserializer = new JsonDeserializer<>(ConsumptionAggregation.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<AggregatedProductionConsumption> AggregatedProductionConsumption() {
        JsonSerializer<AggregatedProductionConsumption> serializer = new JsonSerializer<>();
        JsonDeserializer<AggregatedProductionConsumption> deserializer = new JsonDeserializer<>(AggregatedProductionConsumption.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<AggregatedProductionConsumptionWithCustomer> AggregatedProductionConsumptionWithCustomer() {
        JsonSerializer<AggregatedProductionConsumptionWithCustomer> serializer = new JsonSerializer<>();
        JsonDeserializer<AggregatedProductionConsumptionWithCustomer> deserializer = new JsonDeserializer<>(AggregatedProductionConsumptionWithCustomer.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<ProductionEvent> ProductionEvent() {
        JsonSerializer<ProductionEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<ProductionEvent> deserializer = new JsonDeserializer<>(ProductionEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
    public static Serde<ConsumptionEvent> ConsumptionEvent() {
        JsonSerializer<ConsumptionEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<ConsumptionEvent> deserializer = new JsonDeserializer<>(ConsumptionEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static <T> Serde<T> jsonSerde(Class<T> valueType) {
        JsonSerializer<T> serializer = new JsonSerializer<>();
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(valueType);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}