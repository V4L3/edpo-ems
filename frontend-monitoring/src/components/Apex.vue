<template>
    <v-container>
        <v-row>
            <v-col cols="12">
                <v-row>
                    <v-col cols="12">
                        <h1 class="font-weight-black pb-10">
                            Energy Management of customer ID: {{ customerId }}
                        </h1>
                    </v-col>
                    <v-col cols="6">
                        <v-card>
                            <v-card-title>
                                Total energy production [kW]
                            </v-card-title>
                            <v-card-subtitle>
                                monitor your energy production
                            </v-card-subtitle>
                            <v-card-text>
                                <v-row>
                                    <v-col>
                                        <apexchart width="100%" height="500" type="line" :options="optionsProduction"
                                                   :series="seriesProduction"></apexchart>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                            <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn variant="text" color="primary" @click="detail = true">Show producers</v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-col>
                    <v-col cols="6">
                        <v-card>
                            <v-card-title>
                                Total energy consumption [kW]
                            </v-card-title>
                            <v-card-subtitle>
                                monitor your energy consumption
                            </v-card-subtitle>
                            <v-card-text>
                                <v-row>
                                    <v-col>
                                        <apexchart width="100%" height="500" type="line" :options="optionsConsumption"
                                                   :series="seriesConsumption"></apexchart>
                                    </v-col>
                                </v-row>
                            </v-card-text>
                            <v-card-actions>
                            </v-card-actions>
                        </v-card>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
        <div>
            <v-dialog
                    v-model="detail"
                    fullscreen
                    :scrim="false"
            >

                    <v-card loading="fetchingData">
                      <v-toolbar
                          dark
                          color="primary"
                      >
                        <v-btn
                            icon
                            dark
                            @click="detail = false"
                        >
                          <v-icon>mdi-close</v-icon>
                        </v-btn>
                        <v-toolbar-title>
                          Energy production by producer</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-toolbar-items>
                        </v-toolbar-items>
                      </v-toolbar>
                        <v-progress-linear color="primary" indeterminate></v-progress-linear>
                      <v-spacer></v-spacer>

                        <v-card-subtitle>
                            Energy production by production device like photovoltaic plants. Production in kW.
                        </v-card-subtitle>
                        <v-card-text>
                            <v-row v-for="detail in seriesProductionDetail">
                                <v-col>
                                    <h3 class="my-3">
                                        Energy Producer ID: {{ detail.name }}
                                    </h3>
                                    <apexchart width="100%" height="500" type="line"
                                               :options="optionsProduction"
                                               :series="[detail]"></apexchart>
                                </v-col>
                            </v-row>
                        </v-card-text>
                        <v-card-actions>
                            <v-btn class="mt-4" color="primary" block @click="detail = false">Close Detail</v-btn>
                        </v-card-actions>
                    </v-card>
            </v-dialog>
        </div>
    </v-container>
</template>

<script>
export default {
    name: 'Apex',
    data: () => ({
        customerId: "",
        detail: false,
        fetchingData: true,
        optionsProduction: {
            chart: {
                id: 'productionChart',
                animations: {
                    enabled: false,
                    easing: 'linear',
                    dynamicAnimation: {
                        speed: 1000
                    }
                },
                toolbar: {
                    show: false
                },
                zoom: {
                    enabled: false
                }
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth'
            },
            xaxis: {
                categories: ["", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""]
            },
            yaxis: {
                max: 500,
                min: 0,
            },
            legend: {
                show: false
            },
        },
        optionsProductionDetail: {
            chart: {
                id: 'productionChartDetail',
                animations: {
                    enabled: false,
                    easing: 'linear',
                    dynamicAnimation: {
                        speed: 1000
                    }
                },
                toolbar: {
                    show: false
                },
                zoom: {
                    enabled: false
                }
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth'
            },
            xaxis: {
                categories: ["", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""]
            },
            yaxis: {
                max: 100,
                min: 0,
            },
            legend: {
                show: false
            },
        },
        optionsConsumption: {
            chart: {
                id: 'consumptionChart',
                animations: {
                    enabled: false,
                    easing: 'linear',
                    dynamicAnimation: {
                        speed: 1000
                    }
                },
                toolbar: {
                    show: false
                },
                zoom: {
                    enabled: false
                }
            },
            colors: ['#ff9a00', '#ff4d00'],
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth'
            },
            xaxis: {
                categories: ["", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""]
            },
            yaxis: {
                max: 1000,
                min: 0,
            },
            legend: {
                show: false
            },
        },
        seriesProduction: [{
            name: 'total load',
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        },
            {
                name: 'average load',
                data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            }],
        seriesProductionDetail: [],
        seriesConsumption: [{
            name: 'average consumption',
            data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        },
            {
                name: 'max consumption',
                data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
            }]
    }),
    mounted() {
        console.log("mounted - start data fetcher")
        this.customerId = localStorage.getItem("customerid");
        this.dataFetcher = setInterval(() => {
            this.fetchProductionData()
            this.fetchConsumptionData()
        }, 4000)
    },
    methods: {
        fetchProductionData() {
            const url = 'http://localhost:7071/productionMonitor'

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    let currentTime = new Date();
                    let hours = currentTime.getHours();
                    let minutes = currentTime.getMinutes();
                    let seconds = currentTime.getSeconds();
                    hours = (hours < 10 ? "0" : "") + hours;
                    minutes = (minutes < 10 ? "0" : "") + minutes;
                    seconds = (seconds < 10 ? "0" : "") + seconds;
                    let currentTimeString = hours + ":" + minutes + ":" + seconds;

                    if (data[this.customerId]) {
                        let averageLoad = data[this.customerId].averageLoad;
                        this.seriesProduction[0].data.push(averageLoad.toFixed(2));
                        this.seriesProduction[0].data.shift();

                        let maxLoad = data[this.customerId].maxLoad;
                        this.seriesProduction[1].data.push(maxLoad.toFixed(2));
                        this.seriesProduction[1].data.shift();

                        Object.keys(data[this.customerId].pvList).forEach((pvKey) => {
                            const name = pvKey;
                            const value = data[this.customerId].pvList[pvKey];

                            // Find the corresponding series entry in the seriesConsumption array
                            let seriesEntry = this.seriesProductionDetail.find((entry) => entry.name === name);

                            if (!seriesEntry) {
                                // If the series entry doesn't exist, create a new one
                                seriesEntry = {
                                    name: name,
                                    data: [],
                                };
                                this.seriesProductionDetail.push(seriesEntry);
                            }

                            // Add the current value to the data array
                            seriesEntry.data.push(value.toFixed(2));

                            // Remove the oldest value if the data array exceeds 24 elements
                            if (seriesEntry.data.length > 24) {
                                seriesEntry.data.shift();
                            }

                            // Fill with 0 if the data array has less than 24 elements
                            while (seriesEntry.data.length < 24) {
                                seriesEntry.data.unshift(0);
                            }
                        });

                    } else {
                        this.seriesProduction[0].data.push(0);
                        this.seriesProduction[0].data.shift();

                        this.seriesProduction[1].data.push(0);
                        this.seriesProduction[1].data.shift();
                        console.log("Customer not found (production)")
                    }

                    this.optionsProduction.xaxis.categories.push(currentTimeString);
                    this.optionsProduction.xaxis.categories.shift();

                })
                .catch((err) => {
                    console.log("Error could not fetch production data: " + err)
                })
        },
        fetchConsumptionData() {
            const url = 'http://localhost:7071/consumptionMonitor'

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    let currentTime = new Date();
                    let hours = currentTime.getHours();
                    let minutes = currentTime.getMinutes();
                    let seconds = currentTime.getSeconds();
                    hours = (hours < 10 ? "0" : "") + hours;
                    minutes = (minutes < 10 ? "0" : "") + minutes;
                    seconds = (seconds < 10 ? "0" : "") + seconds;
                    let currentTimeString = hours + ":" + minutes + ":" + seconds;

                    if (data[this.customerId]) {
                        let averageLoad = data[this.customerId].averageLoad;
                        this.seriesConsumption[0].data.push(averageLoad.toFixed(2));
                        this.seriesConsumption[0].data.shift();

                        let maxLoad = data[this.customerId].maxLoad;
                        this.seriesConsumption[1].data.push(maxLoad.toFixed(2));
                        this.seriesConsumption[1].data.shift();
                    } else {
                        this.seriesConsumption[0].data.push(0);
                        this.seriesConsumption[0].data.shift();

                        this.seriesConsumption[1].data.push(0);
                        this.seriesConsumption[1].data.shift();
                        console.log("Customer not found (consumption)")
                    }

                    this.optionsConsumption.xaxis.categories.push(currentTimeString);
                    this.optionsConsumption.xaxis.categories.shift();
                })
                .catch((err) => {
                    console.log("Error could not fetch consumption data: " + err)
                })
        }
    },
    beforeUnmount() {
        console.log("Unmounting - clearing interval")
        clearInterval(this.dataFetcher);
    }
}
</script>