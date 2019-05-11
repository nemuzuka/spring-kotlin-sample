<template>
  <tr @click.stop="moveAuthorizationUrl">
    <td width="5%"><i class="fa fa-bell-o"></i></td>
    <td>{{row.registration_name}}</td>
  </tr>
</template>

<script>
  import baseURL from '../../url'
  export default {
    name: 'client-registration-item',
    props: ["row"],
    methods: {
      moveAuthorizationUrl() {
        const self = this;
        window.location.href = baseURL + self.row.authorization_url;
      }
    }
  }
</script>
