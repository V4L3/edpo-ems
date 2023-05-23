<template>
  <v-container>
    <v-sheet width="50%" class="mx-auto pt-16">
      <v-form fast-fail @submit.prevent="login">
        <h1>Login to emsAI</h1>
        <v-text-field v-model="customerId" label="Customer ID"></v-text-field>
        <h4>
          <v-select v-model="customerId" @update:modelValue="setCustomerId" :items="customers" item-value="id" item-title="name"></v-select>
        </h4>
        <v-btn type="submit" @click="setCustomerId" color="primary" block class="mt-2">Sign in</v-btn>
      </v-form>
    </v-sheet>
  </v-container>
</template>

<script>

export default {
  data() {
    return {
      customerId: '',
      customers: [
        {
          name: 'goeldAI technology',
          id: 'fe49a4f1-eac6-4c50-9451-6c45037223ba'
        },
        {
          name: 'symplAIsAIt güven',
          id: 'fe49a4f1-eac6-4c50-9451-6c45037223bb'
        },
        {
          name: 'berger consultAIng',
          id: 'fe49a4f1-eac6-4c50-9451-6c45037223bc'
        },
      ],
    };
  },
  methods: {
    login() {
      this.$router.push('monitor')
    },
    setCustomerId() {
      localStorage.setItem("customerid", this.customerId);
    },
  },
  async mounted() {
    this.customerId = localStorage.getItem( "customerid" );
    if (this.customerId != null)  {
      this.login()
    }
  }
}
</script>

<style scoped>

</style>
