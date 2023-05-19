package ch.unisg.ems.eventprocessor.model.join;

import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.serialization.ConsumptionEvent;

public class ConsumptionEventWithCustomer {

    private ConsumptionEvent entityConsumptionEvent;
    private Customer customer;

    public ConsumptionEventWithCustomer(ConsumptionEvent entityConsumptionEvent, Customer customer) {
        this.entityConsumptionEvent = entityConsumptionEvent;
        this.customer = customer;
    }

    public ConsumptionEvent getEntityConsumptionEvent() {
        return entityConsumptionEvent;
    }

    public void setEntityProductionEvent(ConsumptionEvent entityConsumptionEvent) {
        this.entityConsumptionEvent = entityConsumptionEvent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "{" + " scoreEvent='" + getEntityConsumptionEvent() + "'" + ", player='" + getCustomer() + "'" + "}";
    }
}
