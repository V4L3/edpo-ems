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
                Consumer: Solar Panel
              </v-card-title>
              <v-card-subtitle>
                monitor your energy production
              </v-card-subtitle>
              <div class="container">
                <apexchart width="500" height="500" type="line" :options="options"
                           :series="seriesProduction"></apexchart>
              </div>
            </v-card>
          </v-col>
          <v-col cols="6">
            <v-card>
              <v-card-title>
                Producer: Charging Station
              </v-card-title>
              <v-card-subtitle>
                monitor your energy consumption
              </v-card-subtitle>
              <div class="container">
                <apexchart width="500" height="500" type="line" :options="options"
                           :series="seriesConsumption"></apexchart>
              </div>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'HelloWorld',
  data: () => ({
    customerId: "",
    options: {
      chart: {
        id: 'vuechart-example',
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
        categories: [
          "1",
          "2",
          "3",
          "4",
          "5",
          "6",
          "7",
          "8",
          "9",
          "10",
          "11",
          "12",
          "1",
          "2",
          "3",
          "4",
          "5",
          "6",
          "7",
          "8",
          "9",
          "10",
          "11",
          "12"
        ]
      },
      yaxis: {
        max: 100,
        min: 0,
      },
      legend: {
        show: false
      },
    },
    seriesProduction: [{
      name: 'average load',
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    },
    {
      name: 'max load',
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    }],
    seriesConsumption: [{
      name: 'average load',
      data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    },
      {
        name: 'max load',
        data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
      }]
  }),
  mounted() {
    this.customerId = localStorage.getItem("customerid");
    setInterval(() => {
      this.fetchData()
    }, 1000)
  },
  methods: {
    getCustomerId() {
      this.customerid = localStorage.getItem("customerid");
    },
    fetchData() {
      const url = 'http://localhost:7070/productionMonitor'

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

            let averageLoad = data[this.customerId].averageLoad;
            this.seriesProduction[0].data.push(averageLoad.toFixed(2));
            this.seriesProduction[0].data.shift();

            let maxLoad = data[this.customerId].maxLoad;
            this.seriesProduction[1].data.push(maxLoad.toFixed(2));
            this.seriesProduction[1].data.shift();

            this.options.xaxis.categories.push(currentTimeString);
            this.options.xaxis.categories.shift();
          })
    }
  }
}
</script>