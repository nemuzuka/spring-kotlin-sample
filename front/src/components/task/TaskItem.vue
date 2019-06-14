<template>
  <div class="column is-3">
    <div class="card" style="cursor: pointer;">
      <div class="card-content">
        <p class="title">
          {{task.title}}
        </p>
        <div class="subtitle">
          <p>{{task.status}}</p>
          <p>{{viewDeadline}}</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  import Moment from 'moment'
  export default {
    name: 'task-item',
    props: ["task"],
    computed:{
      viewDeadline() {
        const self = this
        const deadline = self.task.deadline
        if(deadline === null) {
          return ""
        } else {
          const moment = Moment(deadline)
          return moment.format("YYYY-MM-DD")
        }
      }
    }
  }
</script>
