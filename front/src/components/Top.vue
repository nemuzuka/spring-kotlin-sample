<template>
  <div>
    <div class="box has-text-centered">
      <h1 class="title">TOP画面</h1>
    </div>
  </div>
</template>

<script>
export default {
  name: 'top',
  created () {
    this.$http.get('/api/tasks')
  }
}
</script>
