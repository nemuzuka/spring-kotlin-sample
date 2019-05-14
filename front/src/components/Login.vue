<template>
  <div>

    <div class="message is-info">
      <h4 class="message-header">ログインするアカウントを選択してください</h4>

      <div class="box has-text-centered">
        <client-registration-item v-for="row in elements" :row="row" :key="row.registration_id"></client-registration-item>
      </div>

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
        self.elements.splice(0,self.elements.length)
        self.elements.push(...elements)
      })
  }
}
</script>
