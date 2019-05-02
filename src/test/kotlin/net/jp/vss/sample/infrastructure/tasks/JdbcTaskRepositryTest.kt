package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.JdbcRepositoryUnitTest
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.exceptions.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.UUID

/**
 * JdbcTaskRepositry のテスト.
 */
@JdbcRepositoryUnitTest
@RunWith(SpringJUnit4ClassRunner::class)
class JdbcTaskRepositryTest {

    @Autowired
    private lateinit var sut: JdbcTaskRepositry

    @Test
    @FlywayTest
    fun testCreateTask() {
        // setup
        val task = TaskFixtures.create()

        // execution
        val actual = sut.createTask(task)

        // verify
        assertThat(actual).isEqualTo(task)
        assertThat(actual).isEqualTo(sut.getTask(task.taskCode))
    }

    @Test
    @FlywayTest
    fun testCreateTask_NullProperties() {
        // setup
        val baseTask = TaskFixtures.create()
        val baseTaskDetail = baseTask.taskDetail
        val task = baseTask.copy(taskDetail = baseTaskDetail.copy(attributes = null))

        // execution
        val actual = sut.createTask(task)

        // verify
        assertThat(actual).isEqualTo(task)
        assertThat(actual).isEqualTo(sut.getTask(task.taskCode))
    }

    @Test
    @FlywayTest
    fun testCreateTask_AlreadyExistTaskCode_DE() {
        // setup
        val baseTask = TaskFixtures.create()
        sut.createTask(baseTask)
        val task = baseTask.copy(Task.TaskId(UUID.randomUUID().toString())) // uuid は自動生成

        // execution
        val actual = catchThrowable { sut.createTask(task) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(DuplicateException::class.java) { e ->
            assertThat(e.message).isEqualTo("Task(${task.taskCode.value}) は既に存在しています")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testGetTask_NotNullProperties() {
        // setup
        val taskCode = Task.TaskCode("task_code_001")

        // execution
        val actual = sut.getTask(taskCode)

        // verify
        val taskId = Task.TaskId("task_id_001")
        val taskDetail = Task.TaskDetail(title = "件名その1",
                content = "内容その1",
                deadline = 1246732800001L,
                attributes = Attributes("""{"hige":"hage"}"""))
        val resourceAttributes = ResourceAttributes(createUserCode = "create_user_001",
            createAt = 1646732800001,
            lastUpdateUserCode = "last_update_user_001",
            lastUpdateAt = 1746732800001,
            version = 1L)
        val expected = Task(taskId = taskId, taskCode = taskCode, status = Task.TaskStatus.valueOf("OPEN"),
                taskDetail = taskDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testGetTask_NullProperties() {
        // setup
        val taskCode = Task.TaskCode("task_code_002")

        // execution
        val actual = sut.getTask(taskCode)

        // verify
        val taskId = Task.TaskId("task_id_002")
        val taskDetail = Task.TaskDetail(title = "件名その2",
                content = "内容その2",
                deadline = 1246732800002L,
                attributes = null)
        val resourceAttributes = ResourceAttributes(createUserCode = "create_user_002",
            createAt = 1646732800002,
            lastUpdateUserCode = "last_update_user_002",
            lastUpdateAt = 1746732800002,
            version = 2L)
        val expected = Task(taskId = taskId, taskCode = taskCode, status = Task.TaskStatus.valueOf("DONE"),
                taskDetail = taskDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testGetTask_NotFoundTask_NFE() {
        // setup
        val taskCode = Task.TaskCode("absent_task_code")

        // execution
        val actual = catchThrowable { sut.getTask(taskCode) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("Task(${taskCode.value}) は存在しません")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testLockTask_NotNullProperties() {
        // setup
        val taskCode = Task.TaskCode("task_code_001")

        // execution
        val actual = sut.lockTask(taskCode)

        // verify
        assertThat(actual).isEqualTo(sut.getTask(taskCode))
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testLockTask_NotFoundTask_NFE() {
        // setup
        val taskCode = Task.TaskCode("absent_task_code")

        // execution
        val actual = catchThrowable { sut.lockTask(taskCode) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("Task(${taskCode.value}) は存在しません")
        }
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testAllTasks() {
        // execution
        val actual = sut.allTasks()

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].taskId).isEqualTo(Task.TaskId("task_id_001"))
        assertThat(actual[1].taskId).isEqualTo(Task.TaskId("task_id_002"))
    }

    @Test
    @FlywayTest
    fun testAllTasks_Empty() {
        // execution
        val actual = sut.allTasks()

        // verify
        assertThat(actual).isEmpty()
    }

    @Test
    @FlywayTest
    fun testUpdateTask() {
        // setup
        val baseTask = TaskFixtures.create()
        sut.createTask(baseTask)

        val taskDetail = baseTask.taskDetail.copy(
            title = "update title",
            content = "update content",
            deadline = 1234567890L,
            attributes = Attributes("""{"update_sum":1}"""))
        val resourceAttributes = baseTask.resourceAttributes.copy(
            createUserCode = "UPDATE_USER_12345",
            createAt = 1234567891L,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            version = 123L)
        val task = baseTask.copy(status = Task.TaskStatus.DONE,
            taskDetail = taskDetail,
            resourceAttributes = resourceAttributes)

        // execution
        val actual = sut.updateTask(task)

        // verify
        val expectedResourceAttributes = resourceAttributes.copy(
            version = resourceAttributes.version + 1) // Repository でインクリメント
        val expected = task.copy(resourceAttributes = expectedResourceAttributes)
        assertThat(actual).isEqualTo(expected)
        assertThat(actual).isEqualTo(sut.getTask(task.taskCode))
    }

    @Test
    @FlywayTest
    fun testUpdateTask_NullProperty() {
        // setup
        val baseTask = TaskFixtures.create()
        sut.createTask(baseTask)

        val taskDetail = baseTask.taskDetail.copy(
            title = "update title",
            content = "update content",
            deadline = 1234567890L,
            attributes = null)
        val resourceAttributes = baseTask.resourceAttributes.copy(
            createUserCode = "UPDATE_USER_12345",
            createAt = 1234567891L,
            lastUpdateUserCode = "UPDATE_USER_12345",
            lastUpdateAt = 1234567892L,
            version = 123L)
        val task = baseTask.copy(status = Task.TaskStatus.DONE,
            taskDetail = taskDetail,
            resourceAttributes = resourceAttributes)

        // execution
        val actual = sut.updateTask(task)

        // verify
        val expectedResourceAttributes = resourceAttributes.copy(
            version = resourceAttributes.version + 1) // Repository でインクリメント
        val expected = task.copy(resourceAttributes = expectedResourceAttributes)
        assertThat(actual).isEqualTo(expected)
        assertThat(actual).isEqualTo(sut.getTask(task.taskCode))
    }

    @Test
    @FlywayTest
    fun testUpdateTask_NotFoundUpdateTarget() {
        // setup
        val task = TaskFixtures.create()

        // execution
        val actual = catchThrowable { sut.updateTask(task) }

        // verify
        assertThat(actual).isInstanceOfSatisfying(NotFoundException::class.java) { e ->
            assertThat(e.message).isEqualTo("Task(${task.taskCode.value}) は存在しません")
        }
    }
}
