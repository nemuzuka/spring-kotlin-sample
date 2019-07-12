package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskRepository
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.exceptions.NotFoundException
import org.seasar.doma.jdbc.SelectOptions
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする TaskRepository の実装.
 */
@Repository
class JdbcTaskRepository(private val taskDao: TaskDao) : TaskRepository {

    companion object {
        private val log = LoggerFactory.getLogger(JdbcTaskRepository::class.java)
    }

    override fun createTask(task: Task): Task {
        try {
            taskDao.create(toTaskEntity(task))
            return task // 本当は、RDBMS から取得したものを返すのが確実...
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "Task(${task.taskCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
    }

    override fun getTask(taskCode: Task.TaskCode): Task = getTask(taskCode, false)

    private fun getTask(taskCode: Task.TaskCode, isLock: Boolean): Task =
        taskDao.findByTaskCode(taskCode.value,
            if (isLock) SelectOptions.get().forUpdate() else SelectOptions.get())?.toTask()
            ?: throw NotFoundException("Task(${taskCode.value}) は存在しません")

    override fun lockTask(taskCode: Task.TaskCode): Task = getTask(taskCode, true)

    override fun updateTask(task: Task): Task {

        val baseResourceAttributes = task.resourceAttributes
        val resourceAttributes = baseResourceAttributes.copy(version = baseResourceAttributes.version + 1)
        try {
            taskDao.update(toTaskEntity(task))
        } catch (e: OptimisticLockingFailureException) {
            val message = "Task(${task.taskCode.value}) は存在しません"
            throw NotFoundException(message)
        }
        return task.copy(resourceAttributes = resourceAttributes) // 本当は、RDBMS から取得したものを返すのが確実...
    }

    override fun allTasks(): List<Task> = taskDao.findAll().map { v -> v.toTask() }

    private fun toTaskEntity(task: Task): TaskEntity {
        val taskDetail = task.taskDetail
        val resourceAttributes = task.resourceAttributes
        return TaskEntity(
            taskId = task.taskId.value,
            taskCode = task.taskCode.value,
            status = task.status.name,
            title = taskDetail.title,
            contentValue = taskDetail.content,
            deadline = taskDetail.deadline,
            attributes = taskDetail.attributes?.value,
            createUserCode = resourceAttributes.createUserCode,
            createAt = resourceAttributes.createAt,
            lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
            lastUpdateAt = resourceAttributes.lastUpdateAt,
            versionNo = resourceAttributes.version)
    }
}
