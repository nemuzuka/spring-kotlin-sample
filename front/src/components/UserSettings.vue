<template>
  <div>
    <div class="message is-info">
      <h4 class="message-header">ユーザを{{actionTypeName}}します</h4>

      <div class="box">
        <div class="field">
          <label class="label">氏名</label>
          <div class="control">
            <input class="input" type="text" v-model="user.user_name" placeholder="e.g 山田 太郎">
          </div>
          <p class="help is-info">必須項目</p>
        </div>

        <div class="field">
          <p class="control has-text-right">
            <button class="button is-info" @click="saveUser">
              {{actionTypeName}}する
            </button>
          </p>
        </div>

      </div>
    </div>
  </div>
</template>

<script>

  import Uuid from 'uuid/v4'

  export default {
    name: 'user-settings',
    data() {
      return {
        user: {
          user_code: "",
          user_name: ""
        }
      }
    },
    created () {
      const self = this;
      self.$http.get('/api/me').then(
        (response) => {
          self.user.user_code = response.data.user_code
          self.user.user_name = response.data.user_name
        }
      )
    },
    computed: {
      actionTypeName:function () {
        return this.user.user_code === "" ? "登録" : "変更"
      }
    },
    methods: {
      saveUser() {
        const self = this;
        const userCode = self.user.user_code === "" ? Uuid() : self.user.user_code
        const url = self.user.user_code === "" ? '/api/users' : '/api/users/' + userCode
        self.$http.post(url, {
          user_code: userCode,
          user_name: self.user.user_name
        }).then(
          (response) => {
            alert("終了したよ!" + response)
          }
        )
      }
    }
  }
</script>
