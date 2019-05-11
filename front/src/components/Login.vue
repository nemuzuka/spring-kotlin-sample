<template>
  <div>
    <h1>Loginしましょう</h1>

    <div class="card events-card">
      <header class="card-header">
        <p class="card-header-title">
          OpenID Connect を使用して Login
        </p>
      </header>
      <div class="card-table">
        <div class="content">
          <table class="table is-fullwidth is-striped">
            <tbody>
              <client-registration-item v-for="row in elements" :row="row"></client-registration-item>
            </tbody>
          </table>
        </div>
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
        self.elements.splice(0,self.elements.length);
        self.elements.push(...elements);
      })
  }
}
</script>
