package net.jp.vss.sample.controller.tasks

import com.jayway.jsonassert.JsonAssert
import net.jp.vss.sample.IntegrationTestHelper
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
 * CreateTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class CreateTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks"
        private val log = LoggerFactory.getLogger(CreateTaskApiIntegrationTest::class.java)
    }

    @Autowired(required = false)
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcTaskRepo: JdbcTaskRepositry

    @Autowired
    private lateinit var integrationTestHelper: IntegrationTestHelper

    @Autowired
    private lateinit var tokenStore: TokenStore

    @Before
    fun setUp() {
        flyway.clean()
        flyway.migrate()

        val token = DefaultOAuth2AccessToken("FOO")
        val client = BaseClientDetails("client", null, "read", "client_credentials", "ROLE_CLIENT")
        val authentication = OAuth2Authentication(
            TokenRequest(null, "client", null, "client_credentials").createOAuth2Request(client), null)

        tokenStore.storeAccessToken(token, authentication)
    }

    @Test
    fun testCreateTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java)

        // verify
        log.info("CreateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.OK)

        JsonAssert.with(actual.body)
            .assertThat("$.task_code", `is`(request.taskCode))
            .assertThat("$.status", `is`("OPEN"))
            .assertThat("$.attributes.hoge", `is`("hage"))

        // 永続化していること
        val createdTask = jdbcTaskRepo.getTask(Task.TaskCode(request.taskCode!!))
        assertThat(createdTask)
            .returns(Task.TaskCode(request.taskCode!!), Task::taskCode)
    }

    @Test
    fun testCreateTask_InvalidParameter_400() {
        // setup
        val request = CreateTaskApiParameterFixtures.create().copy(taskCode = null)
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java)

        // verify
        log.info("CreateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.errors[0].field", `is`("taskCode"))
            .assertThat("$.errors[0].defaultMessage", `is`("must not be null"))
    }

    @Test
    fun testCreateTask_InvalidJsonParameter_400() {
        // setup
        val request = CreateTaskApiParameterFixtures.create().copy(attributes = """{hoge:hage}""")
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val postRequestEntity = HttpEntity(request, httpHeaders)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java)

        // verify
        log.info("CreateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Bad Request"))
            .assertThat("$.errors[0].field", `is`("attributes"))
            .assertThat("$.errors[0].defaultMessage", `is`("must match json string format"))
    }

    @Test
    fun testCreateTask_ExsitsTaskCode_409() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        val httpHeaders = integrationTestHelper.csrfHeaders()
        val postRequestEntity = HttpEntity(request, httpHeaders)
        restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java)

        // execution
        val actual = restTemplate.exchange(PATH, HttpMethod.POST, postRequestEntity, String::class.java)

        // verify
        log.info("CreateTask response={}", actual)
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CONFLICT)

        JsonAssert.with(actual.body)
            .assertThat("$.error", `is`("Conflict"))
            .assertThat("$.message", `is`("Task(${request.taskCode}) は既に存在しています"))
    }
}
