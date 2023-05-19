package ch.unisg.ems.eventprocessor.timestampExtractors;
import ch.unisg.ems.eventprocessor.serialization.ConsumptionEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class ConsumptionTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        ConsumptionEvent entityConsumptionEvent = (ConsumptionEvent) record.value();
        return entityConsumptionEvent.getTimestamp();
    }
}