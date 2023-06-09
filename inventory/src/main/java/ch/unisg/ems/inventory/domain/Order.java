package ch.unisg.ems.inventory.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.IOException;

@Entity(name="OrderEntity")
public class Order {

    @Id
    protected String offerId;

    private String customerEmail;
    private String customerName;
    private String batterySize;

    public Order() {}

    // Needs refactor
    public Order(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = objectMapper.readValue(json, Order.class);
        this.offerId = order.getOfferId();
        this.customerName = order.customerName;
        this.customerEmail = order.customerEmail;
        this.batterySize = order.batterySize;
    }

    public String getOfferId() {
        return offerId;
    }

    @JsonProperty("orderId")
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBatterySize() {
        return batterySize;
    }

    public void setBatterySize(String batterySize) {
        this.batterySize = batterySize;
    }
}
