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
              <span class="icon is-small">
                <font-awesome-icon icon="save" />
              </span>
              <span>{{actionTypeName}}する</span>
            </button>
          </p>
        </div>

      </div>
    </div>

    <p class="back"><a @click="moveTop"><font-awesome-icon icon="arrow-left" /></a></p>

  </div>
</template>

<script>

  import Utils from '../../utils'
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
          task.deadline_text = Utils.dateToString(task.deadline)
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
        const task = this.task
        const deadlineText = task.deadline_text
        if(deadlineText === "") {
          task.deadline = null
        } else {
          task.deadline = Date.parse(deadlineText)
        }
      },
      saveTask() {
        const self = this
        const taskCode = self.task.task_code === "" ? Uuid() : self.task.task_code
        const url = self.task.task_code === "" ? '/api/tasks' : '/api/tasks/' + taskCode

        const parameter = {
          task_code: taskCode,
          title: self.task.title,
          content: self.task.content,
          deadline: self.task.deadline,
          attributes: self.task.attributes
        };

        if(self.task.task_code !== "") {
          parameter.is_set_deadline_to_null = self.task.deadline_text === ""
        }

        self.$http.post(url, parameter).then(
          () => {
            self.$toasted.show('処理が終了しました')
            setTimeout(() => {
              self.$router.push('/')
            }, 1500)
          }
        )
      },
      moveTop() {
        const self = this
        self.$router.push('/')
      }
    }
  }
</script>

<style scoped>

  p.back {
    position: fixed;
    left: 15px;
    top: 40%;
    z-index: 10;
  }
  p.back a:hover {
    background: #999;
  }
  p.back a:hover {
    text-decoration: none;
  }
  p.back a {
    background: #666;
    color: #fff;
  }
  p.back a {
    opacity: .75;
    text-decoration: none;
    width: 55.5px;
    height: 55.5px;
    padding: 5px 0;
    text-align: center;
    display: block;
    border-radius: 5px;
    font-size: 200%;
  }
  p.back a i {
    margin-top: 8px;
  }
</style>
