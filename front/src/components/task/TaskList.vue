<template>
  <div>
    <div v-if="tasks.length > 0">
      <div class="columns is-multiline">
        <task-item v-for="task in tasks" :task="task" :key="task.task_code"></task-item>
      </div>
    </div>

    <div v-if="tasks.length <= 0">
      <article class="message is-danger">
        <div class="message-body">表示する Task がありません</div>
      </article>
    </div>
  </div>
</template>

<script>
  import TaskItem from './TaskItem'
  export default {
    components: {
      TaskItem
    },
    name: 'task-list',
    props:["tasks"]
  }
</script>
