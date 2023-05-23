package ch.unisg.ems.actuator.messages;

import ch.unisg.ems.actuator.messages.MessageSender;
import ch.unisg.ems.actuator.model.AggregatedProductionConsumptionWithCustomer;
import ch.unisg.ems.actuator.model.ConsumerShutdownRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageListener {

    private final String productionTopic = "ems_data_by_customer";

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(id = "pv_prod", topics = productionTopic)
//    public void messageReceived(ConsumerRecord<String, String> record) throws Exception{
     public void messageReceived(String payload) throws Exception{
        System.out.println("Message received: ems_data_by_customer topic" );

        AggregatedProductionConsumptionWithCustomer obj = null;
        try {
            obj = objectMapper.readValue(payload, AggregatedProductionConsumptionWithCustomer.class);
        }
        catch (Exception ex) {
            System.out.println("Actuator message on topic ems_data_by_customer: Message Payload could not be deserialized");
        }

        System.out.println("Message key: " + payload);

        if(obj != null && obj.consumptionTooHigh()) {
            // todo add customer id to processing service
            ConsumerShutdownRequest c = new ConsumerShutdownRequest(obj.getCustomer().getName(), "Consumption too high for customer, shut off consumers");

           messageSender.send(new Message<ConsumerShutdownRequest>(c));
        }

    }

}