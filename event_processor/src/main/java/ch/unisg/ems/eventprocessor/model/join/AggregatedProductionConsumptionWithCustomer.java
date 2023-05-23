package ch.unisg.ems.eventprocessor.model.join;

import ch.unisg.ems.eventprocessor.model.Customer;
import ch.unisg.ems.eventprocessor.serialization.ConsumptionEvent;

public class AggregatedProductionConsumptionWithCustomer {

    private AggregatedProductionConsumption aggregatedProductionConsumption;

    private Customer customer;

    public AggregatedProductionConsumptionWithCustomer(AggregatedProductionConsumption aggregatedProductionConsumption, Customer customer) {
        this.aggregatedProductionConsumption = aggregatedProductionConsumption;
        this.customer = customer;
    }

    public AggregatedProductionConsumption getAggregatedProductionConsumption() {
        return aggregatedProductionConsumption;
    }

    public void setAggregatedProductionConsumption(AggregatedProductionConsumption aggregatedProductionConsumption) {
        this.aggregatedProductionConsumption = aggregatedProductionConsumption;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "AggregatedProductionConsumptionWithCustomer{" +
                "aggregatedProductionConsumption=" + aggregatedProductionConsumption +
                ", customer=" + customer +
                '}';
    }
}
