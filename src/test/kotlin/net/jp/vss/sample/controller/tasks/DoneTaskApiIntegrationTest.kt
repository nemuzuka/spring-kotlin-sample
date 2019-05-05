package net.jp.vss.sample.controller.tasks

import com.jayway.jsonassert.JsonAssert
import net.jp.vss.sample.IntegrationTestHelper
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.infrastructure.tasks.JdbcTaskRepositry
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * DoneTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class DoneTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}/_done"
        private val log = LoggerFactory.getLogger(DoneTaskApiIntegrationTest::class.java)
    }

    @Autowired(required = false)
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcTaskRepo: JdbcTaskRepositry

    @Autowired
    private val taskIntegrationHelper = TaskIntegrationHelper()

    @Autowired
    private lateinit var integrationTestHelper: IntegrationTestHelper

    @Before
    fun setUp() {
        flyway.clean()
        flyway.migrate()
    }

    @Test
    fun testDoneTask() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(createRequest)
        val taskCode = createRequest.taskCode

        val httpHeaders = integrationTestHelper.csrfHeaders()
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val path = "$PATH?version=0"
        val actual = restTemplate.exchange(path, HttpMethod.POST, getRequestEntity, String::class.java, taskCode)

        // verify
        log.info("DoneTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(taskCode))
            .assertThat("$.status", `is`("DONE"))
            .assertThat("$.title", `is`(createRequest.title))
            .assertThat("$.content", `is`(createRequest.content))
            .assertThat("$.deadline", `is`(createRequest.deadline))
            .assertThat("$.version", `is`(1))

        // 永続化していること
        val updatedTask = jdbcTaskRepo.getTask(Task.TaskCode(taskCode!!))
        assertThat(updatedTask)
            .returns(Task.TaskStatus.DONE, Task::status)
        assertThat(updatedTask.taskDetail)
            .returns(createRequest.title, Task.TaskDetail::title)
            .returns(createRequest.content, Task.TaskDetail::content)
            .returns(Attributes(createRequest.attributes!!), Task.TaskDetail::attributes)
            .returns(createRequest.deadline, Task.TaskDetail::deadline)
        assertThat(updatedTask.resourceAttributes)
            .returns(1L, ResourceAttributes::version)
    }

    @Test
    fun testDoneTask_NotFoundTask_404() {
        // setup
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, getRequestEntity, String::class.java,
            "absent_task_code")

        // verify
        log.info("DoneTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Not Found"))
            .assertThat("$.message", `is`("Task(absent_task_code) は存在しません"))
    }

    @Test
    fun testDoneTask_InvalidVersion_409() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(createRequest)
        val taskCode = createRequest.taskCode

        val httpHeaders = integrationTestHelper.csrfHeaders()
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val path = "$PATH?version=1" // invalid version
        val actual = restTemplate.exchange(path, HttpMethod.POST, getRequestEntity, String::class.java, taskCode)

        // verify
        log.info("DoneTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CONFLICT)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Conflict"))
            .assertThat("$.message", `is`("指定した version が不正です"))
    }

    @Test
    fun testDoneTask_InvalidPathParameter_400() {
        // setup
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, getRequestEntity, String::class.java,
            "_TASK_001")

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.message", `is`("Constraint Violation Exception"))
    }
}
