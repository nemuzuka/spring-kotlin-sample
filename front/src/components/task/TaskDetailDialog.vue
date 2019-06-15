<template>
  <div class="modal" id="task-detail-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title dialog-title"><span>{{task.title}}</span></p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">
        <div class="content">
          <span class="tag" :class="{'is-info': isOpen, 'is-dark': isDone}">{{task.status}}</span>

          <h1 class="title">{{task.title}}</h1>

          <div>{{task.content}}</div>

          <div>{{task.deadline_text}}</div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="moveEdit">
          <span class="icon is-small">
            <font-awesome-icon icon="edit" />
          </span>
          <span>変更</span>
        </a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>
  import Utils from '../../utils'
  export default {
    name: 'task-detail-dialog',
    data() {
      return {
        activeClass: "tag",
        task: {
          task_code: "",
          title: "",
          content: "",
          status: "",
          deadline: null,
          attributes: null,
          deadline_text: ""
        }
      }
    },
    methods: {
      openDetailDialog(targetTask) {
        const self = this
        const task = self.task
        task.task_code = targetTask.task_code
        task.title = targetTask.title
        task.content = targetTask.content
        task.status = targetTask.status
        task.deadline = targetTask.deadline
        task.deadline_text = Utils.dateToString(task.deadline)
        task.attributes = targetTask.attributes

        Utils.openDialog('task-detail-dialog')
      },
      closeDialog() {
        Utils.closeDialog('task-detail-dialog')
      },
      moveEdit() {

      }
    },
    computed: {
      isOpen() {
        const self = this
        const task = self.task
        return task.status === 'OPEN'
      },
      isDone() {
        const self = this
        const task = self.task
        return task.status === 'DONE'
      }
    }
  }
</script>

<style scoped>
  .dialog-title {
    overflow: hidden;
    width: 80%;
  }
  .dialog-title span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .content h1.title {
    margin-top: 0;
  }
</style>
