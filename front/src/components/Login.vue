<template>
  <div>
    <h1 class="title">ログインするアカウントを選択してください</h1>

    <div class="columns is-centered">
      <client-registration-item v-for="row in elements" :row="row" :key="row.registration_id"></client-registration-item>
    </div>

  </div>
</template>

<script>
import ClientRegistrationItem from "./oauth2/ClientRegistrationItem"

export default {
  name: 'login',
  components: {
    'client-registration-item' : ClientRegistrationItem,
  },
  data() {
    return {
      elements:[]
    }
  },
  created () {
    const self = this
    this.$http
      .get('/api/open-id-connects')
      .then(response => {
        const elements = response.data.elements
        self.elements.splice(0,self.elements.length);
        self.elements.push(...elements);
      })
  }
}
</script>
