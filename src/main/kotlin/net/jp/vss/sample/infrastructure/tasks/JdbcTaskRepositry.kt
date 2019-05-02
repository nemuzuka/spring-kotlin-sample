package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskRepositry
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.exceptions.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * RDBMS にアクセスする TaskRepositry の実装.
 *
 * 流石に JdbcTemplate は使いづらいね...
 */
@Repository
class JdbcTaskRepositry(private val jdbcTemplate: JdbcTemplate) : TaskRepositry {

    private val rowMapper = RowMapper { rs, _ ->
        val taskId = Task.TaskId(rs.getString("task_id"))
        val taskCode = Task.TaskCode(rs.getString("task_code"))
        val status = Task.TaskStatus.valueOf(rs.getString("status"))
        val taskDetail = Task.TaskDetail(title = rs.getString("title"),
                content = rs.getString("content_value"),
                deadline = getLong(rs, "deadline"),
                attributes = Attributes.of(rs.getString("attributes")))
        val resourceAttributes = ResourceAttributes(createUserCode = rs.getString("create_user_code"),
            createAt = rs.getLong("create_at"),
            lastUpdateUserCode = rs.getString("last_update_user_code"),
            lastUpdateAt = rs.getLong("last_update_at"),
            version = rs.getLong("version_no"))
        Task(taskId = taskId, taskCode = taskCode, status = status, taskDetail = taskDetail,
                resourceAttributes = resourceAttributes)
    }

    // ResultSet 使うもんじゃない...
    private fun getLong(rs: ResultSet, columnLabel: String): Long? {
        val value = rs.getLong(columnLabel)
        return if (rs.wasNull()) null else value
    }

    companion object {
        private val log = LoggerFactory.getLogger(JdbcTaskRepositry::class.java)
    }

    override fun createTask(task: Task): Task {
        val taskDetail = task.taskDetail
        val resourceAttributes = task.resourceAttributes

        try {
            jdbcTemplate.update("INSERT INTO tasks(" +
                    "task_id, task_code, status, title, content_value, deadline, attributes, " +
                    "create_user_code, create_at, last_update_user_code, last_update_at, version_no) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    task.taskId.value, task.taskCode.value, task.status.name,
                    taskDetail.title, taskDetail.content, taskDetail.deadline,
                    taskDetail.attributes?.value,
                    resourceAttributes.createUserCode, resourceAttributes.createAt,
                    resourceAttributes.lastUpdateUserCode, resourceAttributes.lastUpdateAt,
                    resourceAttributes.version)
            return task
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "Task(${task.taskCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
    }

    override fun getTask(taskCode: Task.TaskCode): Task = getTask(taskCode, false)

    private fun getTask(taskCode: Task.TaskCode, isLock: Boolean): Task {
        val updateString = if (isLock) "FOR UPDATE" else ""
        val sql = "SELECT * FROM tasks WHERE task_code = ? $updateString"
        return jdbcTemplate.query(sql, rowMapper, taskCode.value)
            .firstOrNull() ?: throw NotFoundException("Task(${taskCode.value}) は存在しません")
    }

    override fun lockTask(taskCode: Task.TaskCode): Task = getTask(taskCode, true)

    override fun updateTask(task: Task): Task {

        val taskDetail = task.taskDetail
        val baseResourceAttributes = task.resourceAttributes
        val resourceAttributes = baseResourceAttributes.copy(version = baseResourceAttributes.version + 1)

        val sql = """
            | UPDATE tasks SET
            |   status = ?,
            |   title = ?,
            |   content_value = ?,
            |   deadline = ?,
            |   attributes = ?,
            |   create_user_code = ?,
            |   create_at = ?,
            |   last_update_user_code = ?,
            |   last_update_at = ?,
            |   version_no = ?
            | WHERE task_id = ?
        """.trimMargin()

        val updatedCount = jdbcTemplate.update(sql,
            task.status.name,
            taskDetail.title,
            taskDetail.content,
            taskDetail.deadline,
            taskDetail.attributes?.value,
            resourceAttributes.createUserCode,
            resourceAttributes.createAt,
            resourceAttributes.lastUpdateUserCode,
            resourceAttributes.lastUpdateAt,
            resourceAttributes.version,
            task.taskId.value)
        if (updatedCount != 1) {
            val message = "Task(${task.taskCode.value}) は存在しません"
            throw NotFoundException(message)
        }
        return task.copy(resourceAttributes = resourceAttributes)
    }

    override fun allTasks(): List<Task> = jdbcTemplate.query("SELECT * FROM tasks ORDER BY task_id", rowMapper)
}
