package ch.unisg.ems.eventprocessor.model.join;

import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.serialization.ProductionEvent;

public class ProductionEventWithCustomer {

    private ProductionEvent entityProductionEvent;
    private Customer customer;

    public ProductionEventWithCustomer(ProductionEvent entityProductionEvent, Customer customer) {
        this.entityProductionEvent = entityProductionEvent;
        this.customer = customer;
    }

    public ProductionEvent getEntityProductionEvent() {
        return entityProductionEvent;
    }

    public void setEntityProductionEvent(ProductionEvent entityProductionEvent) {
        this.entityProductionEvent = entityProductionEvent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "{" + " scoreEvent='" + getEntityProductionEvent() + "'" + ", player='" + getCustomer() + "'" + "}";
    }
}
