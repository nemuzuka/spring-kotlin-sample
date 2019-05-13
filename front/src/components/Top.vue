<template>
  <div id="top-content" class="hide">
    <h1 class="title">TOP画面</h1>
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
