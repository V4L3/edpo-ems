package ch.unisg.ems.eventprocessor.model.aggregations;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ProductionAggregation {
    @SerializedName("AverageLoad")
    private double averageLoad;
    @SerializedName("MaxLoad")
    private double maxLoad;
    @SerializedName("Count")
    private int count;
    @SerializedName("PVList")
    private HashMap<String, Double> pvList;
    @SerializedName("PVCounts")
    private HashMap<String, Integer> pvCounts;

    public ProductionAggregation(double averageLoad, double maxLoad, int count, HashMap<String, Double> pvList, HashMap<String, Integer> pvCounts) {
        this.averageLoad = averageLoad;
        this.maxLoad = maxLoad;
        this.count = count;
        this.pvList = pvList;
        this.pvCounts = pvCounts;
    }

/*    public void updatePVList(String pvId, double load) {
        if (pvList.containsKey(pvId)) {
            double currentLoad = pvList.get(pvId);
            double updatedLoad = currentLoad + load;
            pvList.put(pvId, updatedLoad);
        } else {
            pvList.put(pvId, load);
        }
    }*/

    public HashMap<String, Integer> getPvCounts() {
        return pvCounts;
    }

    public void setPvCounts(HashMap<String, Integer> pvCounts) {
        this.pvCounts = pvCounts;
    }

    public HashMap<String, Double> getPvList() {
        return pvList;
    }

    public void setPvList(HashMap<String, Double> pvList) {
        this.pvList = pvList;
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
        return "ProductionAggregation{" +
                "averageLoad=" + averageLoad +
                ", maxLoad=" + maxLoad +
                '}';
    }
}
