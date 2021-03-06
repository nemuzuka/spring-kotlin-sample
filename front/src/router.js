import Vue from 'vue';
import VueRouter from 'vue-router';
import Top from './components/Top.vue';
import Login from './components/Login.vue';
import UserSettings from './components/UserSettings.vue';
import Error from './components/Error.vue';
import TaskEdit  from './components/task/Edit.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Top },
  { path: '/login', component: Login },
  { path: '/user-settings', component: UserSettings },
  { path: '/error', component: Error },
  { path: '/edit-task/:task_code', component: TaskEdit },
];

const router = new VueRouter({
  routes,
});

export default router;
