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
 * ListTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class ListTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks"
        private val log = LoggerFactory.getLogger(ListTaskApiIntegrationTest::class.java)
    }

    @Autowired(required = false)
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
    fun testListTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(request)

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.GET, getRequestEntity, String::class.java)

        // verify
        log.info("ListTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.list.length()", `is`(1)) // JsonAssert の function を使用
            .assertThat("$.list[0].task_code", `is`(request.taskCode))
            .assertThat("$.list[0].status", `is`("OPEN"))
            .assertThat("$.list[0].attributes.hoge", `is`("hage"))
    }

    @Test
    fun testListTask_Empty() {
        // setup
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val getRequestEntity = HttpEntity<String>(httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.GET, getRequestEntity, String::class.java)

        // verify
        log.info("ListTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.list.length()", `is`(0))
    }
}
