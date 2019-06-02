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
            <input class="input" type="date" v-model="task.deadline_text" v-on:change="changeDate">
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

  import Moment from 'moment'
  import Uuid from 'uuid/v4'

  export default {
    name: 'task-edit',
    data() {
      return {
        task: {
          task_code: "",
          title: "",
          content: "",
          deadline: null,
          attributes: null,
          deadline_text: ""
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
          if(task.deadline === null) {
            task.deadline_text = ""
          } else {
            const moment = Moment(task.deadline)
            task.deadline_text = moment.format("YYYY-MM-DD")
          }
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
      changeDate:function () {
        const task = this.task;
        const deadlineText = task.deadline_text;
        if(deadlineText === "") {
          task.deadline = null
        } else {
          task.deadline = Date.parse(deadlineText)
        }
      },
      saveTask() {
        const self = this;
        const taskCode = self.task.task_code === "" ? Uuid() : self.task.task_code
        const url = self.task.task_code === "" ? '/api/tasks' : '/api/tasks/' + taskCode
        self.$http.post(url, {
          task_code: taskCode,
          title: self.task.title,
          content: self.task.content,
          deadline: self.task.deadline,
          attributes: self.task.attributes
        }).then(
          (response) => {
            alert("終了したよ!" + response)
            self.$router.push('/')
          }
        )
      }
    }
  }
</script>
