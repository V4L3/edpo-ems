package ch.unisg.ems.payment.messages;

import ch.unisg.ems.payment.domain.Invoice;
import ch.unisg.ems.payment.domain.Offer;
import ch.unisg.ems.payment.domain.PaymentReceived;
import ch.unisg.ems.payment.flow.OfferFlowContext;
import ch.unisg.ems.payment.persistence.InvoiceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Component
public class MessageListener {

    private final String paymentRequiredTopic = "payment-service";
    private final String inventoryTopic = "ems-inventory";

    private final String salesTopic = "ems-sales";

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Qualifier("zeebeClientLifecycle")
    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Transactional
    @KafkaListener(id = "payment", topics = paymentRequiredTopic)
    public void messageReceived(String messagePayload, @Header("type") String messageType) throws Exception {

        if("PaymentReceivedEvent".equals(messageType)) {
            paymentReceived(messagePayload);
        }
        /*else if ("ClientAnswerReceivedEvent".equals(messageType)) {
            paymentReceived(messagePayload);
        }*/
        else {
            System.out.println("Ignored message of type " + messageType );
        }
    }

    @Transactional
    @KafkaListener(id = "sales-payment", topics = salesTopic)
    public void salesMessageReceived(String messagePayload, @Header("type") String messageType) throws Exception {

        if("OrderPlacedEvent".equals(messageType)) {
//            initPayment(messagePayload);
        }
        else {
            System.out.println("Ignored message of type " + messageType );
        }
    }

    @Transactional
    @KafkaListener(id = "inventory-payment", topics = inventoryTopic)
    public void inventoryMessageReceived(String messagePayload, @Header("type") String messageType) throws Exception {

        if("InstallationCompleteEvent".equals(messageType)) {
            initPayment(messagePayload);
        }
        else {
            System.out.println("Ignored message of type " + messageType );
        }
    }

    @Transactional
    public void initPayment(String messagePayload) {
        System.out.println("New payment requested: " + messagePayload);
        try {
            JsonNode message = objectMapper.readTree(messagePayload);

            Invoice invoice = new Invoice(message.get("data").get("offerId").toString().replace("\"", ""), false, message.get("data").get("clientEmail").toString().replace("\"", ""));

//            Invoice invoice = new Invoice("offer 3", false, "fabio@geolditechnology.ch");

            invoiceRepository.save(invoice);


            String traceId = UUID.randomUUID().toString();
            OfferFlowContext context = new OfferFlowContext();
            context.setOfferId(invoice.getOfferId());
            context.setInvoiceId(invoice.getId());
            context.setTraceId(traceId);
            context.setClientEmail(invoice.getClientEmail());
            context.setAmount(invoice.getAmount());
            context.setReminderCounter(0);
            context.setReminderSent("false");

            zeebe.newCreateInstanceCommand() //
                    .bpmnProcessId("payment-service") //
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
    public void paymentReceived(String messagePayload) throws Exception {
        // Here you would maybe we should read something from the payload:
        System.out.println("Payment received: " + messagePayload);

        PaymentReceived paymentReceived = new PaymentReceived(messagePayload);

//        HashMap<String, String> newVariables = new HashMap<>();
//        newVariables.put("offerStatus",offerReply.getOfferAccepted() ? "accepted" : offerReply.getNewOfferRequested() ? "newOfferRequested" :"rejected");

        zeebe.newPublishMessageCommand() //
                .messageName("PaymentReceivedEvent")
                .correlationKey(paymentReceived.getInvoiceId())
                .send().join();

        System.out.println("Correlated " + paymentReceived.getInvoiceId());
    }



}