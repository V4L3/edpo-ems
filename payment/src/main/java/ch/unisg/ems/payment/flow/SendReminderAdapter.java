package ch.unisg.ems.payment.flow;

import ch.unisg.ems.payment.domain.Invoice;
import ch.unisg.ems.payment.flow.payload.SendInvoiceNotificationToLegalTeamCommandPayload;
import ch.unisg.ems.payment.flow.payload.SendReminderToClientCommandPayload;
import ch.unisg.ems.payment.messages.Message;
import ch.unisg.ems.payment.messages.MessageSender;
import ch.unisg.ems.payment.persistence.InvoiceRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Component
public class SendReminderAdapter {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @ZeebeWorker(type = "send-reminder")
    public void handle(JobClient client, ActivatedJob job) {

        OfferFlowContext context = OfferFlowContext.fromMap(job.getVariablesAsMap());
        Invoice invoice = invoiceRepository.findById(context.getInvoiceId()).get();

        HashMap<String, String> newVariables = new HashMap<>();

        // if reminders are sent 5 times and the payment has still not arrived, complete job gracefully and send event to legal team
        if (context.getReminderSent().equals("true") && context.getReminderCounter() > 4) {
            SendInvoiceNotificationToLegalTeamCommandPayload payload = new SendInvoiceNotificationToLegalTeamCommandPayload();
            payload.setOfferId(invoice.getOfferId());
            payload.setInvoiceId(invoice.getId());
            payload.setClientEmail(invoice.getClientEmail());
            payload.setMessage("Reminder to pay invoice");

            messageSender.send(new Message<SendInvoiceNotificationToLegalTeamCommandPayload>(
                    "SendInvoiceNotificationToLegalTeam",
                    payload
            ), "ems-notification");
            System.out.println("Sending reminder to legal team: " + payload);

            // mark process as complete when reminder is sent to legal team
            client.newCompleteCommand(job);
        } else {
            SendReminderToClientCommandPayload payload = new SendReminderToClientCommandPayload();
            payload.setOfferId(invoice.getOfferId());
            payload.setInvoiceId(invoice.getId());
            payload.setClientEmail(invoice.getClientEmail());
            payload.setMessage("Reminder to pay invoice");

            messageSender.send(new Message<SendReminderToClientCommandPayload>(
                    "SendInvoiceReminderToClientCommand",
                    payload
            ), "ems-notification");

            newVariables.put("reminderSent","true");
            newVariables.put("reminderCounter","1");
            System.out.println("Sending reminder to client: " + payload);
        }


        System.out.println("Invoice reminder is sent");

        client.newCompleteCommand(job.getKey())
                .variables(newVariables)
                .send()
                .join();

    }
}
