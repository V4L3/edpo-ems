package ch.unisg.ems.eventprocessor.model.aggregations;

import com.google.gson.annotations.SerializedName;

public class ConsumptionAggregation {
    @SerializedName("AverageLoad")
    private double averageLoad;
    @SerializedName("MaxLoad")
    private double maxLoad;
    @SerializedName("Count")
    private int count;
    public ConsumptionAggregation(double averageLoad, double maxLoad, int count) {
        this.averageLoad = averageLoad;
        this.maxLoad = maxLoad;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverageLoad() {
        return averageLoad;
    }

    public void setAverageLoad(double averageLoad) {
        this.averageLoad = averageLoad;
    }

    public double getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(double maxLoad) {
        this.maxLoad = maxLoad;
    }

    @Override
    public String toString() {
        return "ConsumptionAggregation{" +
                "averageLoad=" + averageLoad +
                ", maxLoad=" + maxLoad +
                '}';
    }
}
