<template>
  <div>
    <div class="box has-text-centered">
      <h1 class="title">ユーザを{{action_name}}しましょう</h1>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'user-settings',
    data() {
      return {
        action_name:""
      }
    },
    created () {
      this.$http.get('/api/me')
    }
  }
</script>
