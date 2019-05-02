package net.jp.vss.sample.controller.tasks

import com.jayway.jsonassert.JsonAssert
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.infrastructure.tasks.JdbcTaskRepositry
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * UpdateTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class UpdateTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}"
        private val log = LoggerFactory.getLogger(UpdateTaskApiIntegrationTest::class.java)
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcTaskRepo: JdbcTaskRepositry

    private val taskIntegrationHelper = TaskIntegrationHelper()

    @Before
    fun setUp() {
        flyway.clean()
        flyway.migrate()
    }

    @Test
    fun testUpdateTask() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(restTemplate, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameterFixtures.create()
            .copy(isSetDeadlineToNull = false) // deadline の更新を確認したい
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val path = "$PATH?version=0"
        val actual = restTemplate.exchange(path, HttpMethod.POST, postRequestEntity, String::class.java, taskCode)

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(taskCode))
            .assertThat("$.status", `is`("OPEN"))
            .assertThat("$.title", `is`(request.title))
            .assertThat("$.content", `is`(request.content))
            .assertThat("$.deadline", `is`(request.deadline))
            .assertThat("$.attributes.hoge", `is`("super_hoge"))
            .assertThat("$.version", `is`(1))

        // 永続化していること
        val updatedTask = jdbcTaskRepo.getTask(Task.TaskCode(taskCode!!))
        assertThat(updatedTask)
            .returns(Task.TaskStatus.OPEN, Task::status)
        assertThat(updatedTask.taskDetail)
            .returns(request.title, Task.TaskDetail::title)
            .returns(request.content, Task.TaskDetail::content)
            .returns(Attributes(request.attributes!!), Task.TaskDetail::attributes)
            .returns(request.deadline, Task.TaskDetail::deadline)
        assertThat(updatedTask.resourceAttributes)
            .returns(1L, ResourceAttributes::version)
    }

    /**
     * null 可能なプロパティが全て null.
     */
    @Test
    fun testUpdateTask_NullProperties() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(restTemplate, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameter(isSetDeadlineToNull = false) // null にされると困るので false
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java, taskCode)

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(taskCode))
            .assertThat("$.status", `is`("OPEN"))
            .assertThat("$.title", `is`(createRequest.title))
            .assertThat("$.content", `is`(createRequest.content))
            .assertThat("$.deadline", `is`(createRequest.deadline))
            .assertThat("$.attributes.hoge", `is`("hage"))
            .assertThat("$.version", `is`(1))

        // 永続化していること
        val updatedTask = jdbcTaskRepo.getTask(Task.TaskCode(taskCode!!))
        assertThat(updatedTask)
            .returns(Task.TaskStatus.OPEN, Task::status)
        assertThat(updatedTask.taskDetail)
            .returns(createRequest.title, Task.TaskDetail::title)
            .returns(createRequest.content, Task.TaskDetail::content)
            .returns(Attributes(createRequest.attributes!!), Task.TaskDetail::attributes)
            .returns(createRequest.deadline, Task.TaskDetail::deadline)
        assertThat(updatedTask.resourceAttributes)
            .returns(1L, ResourceAttributes::version)
    }

    /**
     * isSetDeadlineToNull を true にした.
     */
    @Test
    fun testUpdateTask_SetDeadlineToNullTrue() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(restTemplate, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameter(isSetDeadlineToNull = true)
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java, taskCode)

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(taskCode))
            .assertThat("$.status", `is`("OPEN"))
            .assertThat("$.title", `is`(createRequest.title))
            .assertThat("$.content", `is`(createRequest.content))
            .assertThat("$.deadline", `is`(nullValue()))
            .assertThat("$.attributes.hoge", `is`("hage"))
            .assertThat("$.version", `is`(1))

        // 永続化していること
        val updatedTask = jdbcTaskRepo.getTask(Task.TaskCode(taskCode!!))
        assertThat(updatedTask.taskDetail)
            .returns(null, Task.TaskDetail::deadline)
    }

    @Test
    fun testUpdateTask_NotFoundTask_404() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create()
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java,
            "absent_task_code")

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Not Found"))
            .assertThat("$.message", `is`("Task(absent_task_code) は存在しません"))
    }

    @Test
    fun testUpdateTask_InvalidVersion_409() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(restTemplate, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameterFixtures.create()
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val path = "$PATH?version=1" // invalid version
        val actual = restTemplate.exchange(path, HttpMethod.POST, postRequestEntity, String::class.java, taskCode)

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CONFLICT)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Conflict"))
            .assertThat("$.message", `is`("指定した version が不正です"))
    }

    @Test
    fun testUpdateTask_InvalidJsonParameter_400() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create().copy(attributes = """{hoge:hage}""")
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java,
            "TASK_A_0001")

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.errors[0].field", `is`("attributes"))
            .assertThat("$.errors[0].defaultMessage", `is`("must match json string format"))
    }

    @Test
    fun testUpdateTask_InvalidPathParameter_400() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create()
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java,
            "_TASK_001")

        // verify
        log.info("UpdateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.message", `is`("Constraint Violation Exception"))
    }
}
