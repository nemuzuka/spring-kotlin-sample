import Vue from 'vue';
import VueRouter from 'vue-router';
import Top from './components/Top.vue';
import Login from './components/Login.vue';
import GoogleUserSettings from './components/usersettings/Google.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Top },
  { path: '/login', component: Login },
  { path: '/google-user-settings', component: GoogleUserSettings },
];

const router = new VueRouter({
  routes,
});

export default router;
