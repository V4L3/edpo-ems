package ch.unisg.ems.actuator.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;


/**
 * Helper to send messages, currently nailed to Kafka, but could also send via AMQP (e.g. RabbitMQ) or
 * any other transport easily
 */
@Component
public class MessageSender {

    public static final String TOPIC_NAME = "actuators";

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public NewTopic autoCreateTopicOnStartupIfNotExistant() {
        return TopicBuilder.name(TOPIC_NAME).partitions(1).replicas(1).build();
    }

    public void send(Message<?> message) {
        try {
            // avoid too much magic and transform ourselves
            String jsonMessage = objectMapper.writeValueAsString(message);

            byte[] messageBytes = jsonMessage.getBytes(StandardCharsets.UTF_8);
            ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>("actuators", messageBytes);
            // record.headers().add("type", m.getBytes());
            // and send it
            kafkaTemplate.send(record);
        } catch (Exception e) {
            throw new RuntimeException("Could not transform and send message: "+ e.getMessage(), e);
        }
    }
}
