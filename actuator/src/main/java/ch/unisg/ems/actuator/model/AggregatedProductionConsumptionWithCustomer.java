package ch.unisg.ems.actuator.model;

import ch.unisg.ems.actuator.model.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AggregatedProductionConsumptionWithCustomer {

    @JsonProperty("aggregated_production_consumption")
    private AggregatedProductionConsumption aggregatedProductionConsumption;

    @JsonProperty("customer")
    private Customer customer;

    public AggregatedProductionConsumptionWithCustomer() {
    }

    public Boolean consumptionTooHigh () {
        return this.aggregatedProductionConsumption.getAggregatedProduction().getAverageLoad() < 50 &&
                this.aggregatedProductionConsumption.getAggregatedConsumption().getAverageLoad() > 200;
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

    private static class AggregatedProductionConsumption {
        @JsonProperty("AggregatedProduction")
        private AggregatedProduction aggregatedProduction;

        @JsonProperty("AggregatedConsumption")
        private AggregatedConsumption aggregatedConsumption;

        public AggregatedProductionConsumption() {
        }

        public AggregatedProduction getAggregatedProduction() {
            return aggregatedProduction;
        }

        public void setAggregatedProduction(AggregatedProduction aggregatedProduction) {
            this.aggregatedProduction = aggregatedProduction;
        }

        public AggregatedConsumption getAggregatedConsumption() {
            return aggregatedConsumption;
        }

        public void setAggregatedConsumption(AggregatedConsumption aggregatedConsumption) {
            this.aggregatedConsumption = aggregatedConsumption;
        }

        private static class Aggregation {
            @JsonProperty("AverageLoad")
            private long averageLoad;
            @JsonProperty("MaxLoad")
            private long maxLoad;
            @JsonProperty("Count")
            private int count;

            public long getAverageLoad() {
                return averageLoad;
            }

            public void setAverageLoad(long averageLoad) {
                this.averageLoad = averageLoad;
            }

            public long getMaxLoad() {
                return maxLoad;
            }

            public void setMaxLoad(long maxLoad) {
                this.maxLoad = maxLoad;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }
        private static class AggregatedProduction extends Aggregation {
            public AggregatedProduction() {

            }
        }

        private static class AggregatedConsumption extends Aggregation {
            public AggregatedConsumption() {

            }

        }
    }

    @Override
    public String toString() {
        return "AggregatedProductionConsumptionWithCustomer{" +
                "aggregatedProductionConsumption=" + aggregatedProductionConsumption +
                ", customer=" + customer +
                '}';
    }
}
