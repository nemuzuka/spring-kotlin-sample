<template>
  <div class="container">
    <app-menu></app-menu>
    <div class="main-contents">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
  import Menu from "./components/header/Menu"

  export default {
    name: 'app',
    components: {
      'app-menu' : Menu,
    }
  }
</script>

<style scoped>
  .main-contents {
    background: darkseagreen;
    padding: 3em;
  }
</style>
