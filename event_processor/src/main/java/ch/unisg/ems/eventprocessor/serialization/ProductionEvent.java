package ch.unisg.ems.eventprocessor.serialization;

import com.google.gson.annotations.SerializedName;

public class ProductionEvent {
    @SerializedName("reactive")
    boolean reactive;
    @SerializedName("load")
    double load;
    @SerializedName("unit_load")
    String unitLoad;
    @SerializedName("pv_id")
    String pvId;
    @SerializedName("timestamp")
    Long timestamp;
    @SerializedName("unit_reactive")
    String unitReactive;

    @SerializedName("customer_id")
    String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPvId() {
        return pvId;
    }

    public void setPvId(String pvId) {
        this.pvId = pvId;
    }

    public boolean isReactive() {
        return reactive;
    }

    public void setReactive(boolean reactive) {
        this.reactive = reactive;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public String getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(String unitLoad) {
        this.unitLoad = unitLoad;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUnitReactive() {
        return unitReactive;
    }

    public void setUnitReactive(String unitReactive) {
        this.unitReactive = unitReactive;
    }

}
