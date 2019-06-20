import Vue from 'vue'
import App from './App.vue'
import Axios from './axios'
import router from './router'
import 'bulma/css/bulma.css'
import './app.css'
import Toasted from 'vue-toasted'
import ja from 'vee-validate/dist/locale/ja'
import VeeValidate, { Validator } from 'vee-validate'

import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { fab } from '@fortawesome/free-brands-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

library.add(fas, far, fab)

Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.prototype.$http = Axios
Vue.config.productionTip = false

Vue.use(VeeValidate)
Validator.localize('ja', ja)

const toastOption = {
  theme: "bubble",
  position: "top-right",
  duration : 2000
}
Vue.use(Toasted, toastOption)

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
