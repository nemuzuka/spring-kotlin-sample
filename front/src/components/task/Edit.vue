<template>
  <div>
    <div class="message is-info">
      <h4 class="message-header">タスクを{{actionTypeName}}します</h4>

      <div class="box">

        <div class="field">
          <label class="label">title</label>
          <div class="control">
            <input class="input" type="text" v-model="task.title" placeholder="e.g. xxx をする">
          </div>
          <p class="help is-info">必須項目</p>
        </div>

        <div class="field">
          <label class="label">内容</label>
          <div class="control">
            <textarea class="textarea" v-model="task.content" placeholder="e.g. A をする"></textarea>
          </div>
          <p class="help is-info">必須項目</p>
        </div>

        <div class="field">
          <label class="label">期限</label>
          <div class="control">
            <input class="input" type="date" v-model="task.deadline">
          </div>
        </div>


        <div class="field">
          <p class="control has-text-right">
            <button class="button is-info" @click="saveTask">
              {{actionTypeName}}する
            </button>
          </p>
        </div>

      </div>
    </div>
  </div>
</template>

<script>

  export default {
    name: 'task-edit',
    data() {
      return {
        task: {
          task_code: "",
          title: "",
          content: "",
          deadline: null,
          attributes: null
        }
      }
    },
    created () {
      const self = this
      const taskCode = self.$route.params.task_code
      self.$http
        .get('/api/tasks/' + taskCode)
        .then(response => {
          const task = self.task
          const responseTask = response.data
          task.task_code = responseTask.task_code
          task.title = responseTask.title
          task.content = responseTask.content
          task.deadline = responseTask.deadline
          task.attributes = responseTask.attributes
        }
      )
    },
    computed: {
      actionTypeName:function () {
        return this.task.task_code === "" ? "登録" : "変更"
      }
    },
    methods: {
      saveTask() {
        // const self = this;
      //   const userCode = self.user.user_code === "" ? Uuid() : self.user.user_code
      //   const url = self.user.user_code === "" ? '/api/users' : '/api/users/' + userCode
      //   self.$http.post(url, {
      //     user_code: userCode,
      //     user_name: self.user.user_name
      //   }).then(
      //     (response) => {
      //       alert("終了したよ!" + response)
      //     }
      //   )
      }
    }
  }
</script>
