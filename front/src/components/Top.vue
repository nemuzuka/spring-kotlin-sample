<template>
  <div>
    <div class="box has-text-centered">
      <h1 class="title">タスク一覧</h1>
    </div>

    <task-list :tasks="tasks"></task-list>

    <p class="create-task"><a @click="moveCreateTask"><font-awesome-icon icon="plus" /></a></p>

  </div>
</template>

<script>

import TaskList from './task/TaskList'

export default {
  components: {
    TaskList
  },
  name: 'top',
  data() {
    return {
      tasks: []
    }
  },
  created () {
    const self = this
    this.$http.get('/api/tasks')
      .then(response => {
        const tasks = self.tasks
        const taskElements = response.data.elements

        tasks.splice(0,tasks.length);
        tasks.push(...taskElements);
      })
  },
  methods: {
    moveCreateTask() {
      const self = this
      self.$router.push('/edit-task/_new')
    }
  }
}
</script>

<style scoped>
  p.create-task {
    position: fixed;
    right: 15px;
    top: 15%;
    z-index: 10;
  }
  p.create-task a:hover {
    background: #999;
  }
  p.create-task a:hover {
    text-decoration: none;
  }
  p.create-task a {
    background: #666;
    color: #fff;
  }
  p.create-task a {
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
  p.create-task a i {
    margin-top: 8px;
  }
</style>
