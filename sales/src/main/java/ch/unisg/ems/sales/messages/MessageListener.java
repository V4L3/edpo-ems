package ch.unisg.ems.sales.messages;

import ch.unisg.ems.sales.domain.Offer;
import ch.unisg.ems.sales.domain.OfferReply;
import ch.unisg.ems.sales.flow.OfferFlowContext;
import ch.unisg.ems.sales.persistence.OfferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Component
public class MessageListener {

    private final String salesTopic = "ems-sales";

    @Autowired
    private OfferRepository offerRepository;

    @Qualifier("zeebeClientLifecycle")
    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(id = "sales", topics = salesTopic)
    public void messageReceived(String messagePayload, @Header("type") String messageType) throws Exception{
        System.out.println("Message received: SalesTopic" );

        if("OfferRequestedEvent".equals(messageType)) {
            offerRequested(messagePayload);
        }
        else if ("ClientAnswerReceivedEvent".equals(messageType)) {
            clientAnswerReceived(messagePayload);
        }
        else {
            System.out.println("Ignored message of type " + salesTopic );
        }
    }

    @Transactional
    public void offerRequested(String messagePayload) {
        System.out.println("New offer requested: " + messagePayload);
        try {
            Offer offer = new Offer(messagePayload);

            offerRepository.save(offer);

            String traceId = UUID.randomUUID().toString();
            OfferFlowContext context = new OfferFlowContext();
            context.setOfferId(offer.getId());
            context.setTraceId(traceId);
            context.setOfferMessage("");
            context.setLoadProfileConsumption(offer.getLoadProfileConsumption());
            context.setLoadProfileProduction(offer.getLoadProfileProduction());
            context.setOfferAccepted(false);
            context.setNewOfferRequested(false);
            context.setReminderSent(false);


            zeebe.newCreateInstanceCommand() //
                    .bpmnProcessId("sales-service") //
                    .latestVersion() //
                    .variables(context.asMap()) //
                    .send().join();

        } catch (IOException e) {
            System.out.println("Error while creating the offer: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while starting the bpmn process: " + e.getMessage());
        }
    }

    @Transactional
    public void clientAnswerReceived(String messagePayload) throws Exception {
        System.out.println("Payment received: " + messagePayload);

        OfferReply offerReply = new OfferReply(messagePayload);

        String clientReply = offerReply.getOfferAccepted() ? "accepted" : offerReply.getNewOfferRequested() ? "newOfferRequested" :"rejected";

        Offer offer = offerRepository.findById(offerReply.getOfferId()).get();
        offer.setOfferStatus(clientReply);
        offerRepository.save(offer);

        HashMap<String, String> newVariables = new HashMap<>();
        newVariables.put("offerStatus",clientReply);

        zeebe.newPublishMessageCommand() //
                .messageName("ClientResponseReceivedEvent")
                .correlationKey(offerReply.getOfferId())
                .variables(newVariables)
                .send().join();

        System.out.println("Correlated " + offerReply.getOfferId());
    }

}