package net.jp.vss.sample.controller.tasks

import com.jayway.jsonassert.JsonAssert
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
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * GetTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class GetTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}"
        private val log = LoggerFactory.getLogger(GetTaskApiIntegrationTest::class.java)
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var taskIntegrationHelper: TaskIntegrationHelper

    @Before
    fun setUp() {
        flyway.clean()
        flyway.migrate()
    }

    @Test
    fun testGetTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(request)

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.GET, getRequestEntity, String::class.java, request.taskCode)

        // verify
        log.info("GetTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(request.taskCode))
            .assertThat("$.status", `is`("OPEN"))
            .assertThat("$.attributes.hoge", `is`("hage"))
    }

    @Test
    fun testGetTask_NotFoundTask_404() {
        // setup
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.GET, getRequestEntity, String::class.java,
            "absent_task_code")

        // verify
        log.info("GetTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Not Found"))
            .assertThat("$.message", `is`("Task(absent_task_code) は存在しません"))
    }

    @Test
    fun testGetTask_InvalidParameter_400() {
        // setup
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.GET, getRequestEntity, String::class.java,
            "_TASK_001")

        // verify
        log.info("GetTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.message", `is`("Constraint Violation Exception")) // もうちょっとかっこよくしたい
    }
}
