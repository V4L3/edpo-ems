package ch.unisg.ems.eventprocessor.model.join;

import ch.unisg.ems.eventprocessor.model.aggregations.ConsumptionAggregation;
import ch.unisg.ems.eventprocessor.model.aggregations.ProductionAggregation;
import com.google.gson.annotations.SerializedName;

public class AggregatedProductionConsumption {
    @SerializedName("AggregatedProduction")
    private ProductionAggregation productionAggregation;

    @SerializedName("AggregatedConsumption")
    private ConsumptionAggregation consumptionAggregation;

    public AggregatedProductionConsumption(ProductionAggregation productionAggregation, ConsumptionAggregation consumptionAggregation) {
        this.productionAggregation = productionAggregation;
        this.consumptionAggregation = consumptionAggregation;
    }

    public ProductionAggregation getProductionAggregation() {
        return productionAggregation;
    }

    public void setProductionAggregation(ProductionAggregation productionAggregation) {
        this.productionAggregation = productionAggregation;
    }

    public ConsumptionAggregation getConsumptionAggregation() {
        return consumptionAggregation;
    }

    public void setConsumptionAggregation(ConsumptionAggregation consumptionAggregation) {
        this.consumptionAggregation = consumptionAggregation;
    }

    @Override
    public String toString() {
        return "AggregatedProductionConsumption{" +
                "productionAggregation=" + productionAggregation +
                ", consumptionAggregation=" + consumptionAggregation +
                '}';
    }
}
