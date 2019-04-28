package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.Attributes
import net.jp.vss.sample.JdbcRepositoryUnitTest
import net.jp.vss.sample.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.exception.DuplicateException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * JdbcTaskRepositry のテスト.
 */
@JdbcRepositoryUnitTest
@RunWith(SpringJUnit4ClassRunner::class)
class JdbcTaskRepositryTest {

    @Autowired
    private lateinit var sut: JdbcTaskRepositry

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
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
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
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
    @FlywayTest(locationsForMigrate = ["/db/fixtures_task"])
    fun testCreateTask_既に存在するtask_code() {
        // setup
        val task = TaskFixtures.create()
        sut.createTask(task)

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
    fun testAllTasks() {
        // execution
        val actual = sut.allTasks()

        // verify
        assertThat(actual).hasSize(2)
        assertThat(actual[0].taskId).isEqualTo(Task.TaskId("task_id_001"))
        assertThat(actual[1].taskId).isEqualTo(Task.TaskId("task_id_002"))
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_empty"])
    fun testAllTasks_Empty() {
        // execution
        val actual = sut.allTasks()

        // verify
        assertThat(actual).isEmpty()
    }
}
