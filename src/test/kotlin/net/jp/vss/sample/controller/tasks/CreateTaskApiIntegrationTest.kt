package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import net.jp.vss.sample.controller.exceptions.HttpConflictException
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.infrastructure.tasks.JdbcTaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.MethodArgumentNotValidException

/**
 * CreateTaskApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class CreateTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcTaskRepo: JdbcTaskRepository

    @Autowired
    private lateinit var taskIntegrationHelper: TaskIntegrationHelper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        flyway.clean()
        flyway.migrate()

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    @Test
    fun testCreateTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(request.taskCode)))
            .andExpect(jsonPath("$.status", equalTo("OPEN")))
            .andExpect(jsonPath("$.attributes.hoge", equalTo("hage")))

        // 永続化していること
        val createdTask = jdbcTaskRepo.getTask(Task.TaskCode(request.taskCode!!))
        assertThat(createdTask)
            .returns(Task.TaskCode(request.taskCode!!), Task::taskCode)
    }

    /**
     * body パラメータの validation でエラー.
     */
    @Test
    fun testCreateTask_InvalidParameter_400() {
        // setup
        val request = CreateTaskApiParameterFixtures.create().copy(taskCode = null)
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("taskCode")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must not be null")
    }

    /**
     * 自前の validate アノテーション でエラー.
     */
    @Test
    fun testCreateTask_InvalidJsonParameter_400() {
        // setup
        val request = CreateTaskApiParameterFixtures.create().copy(attributes = """{hoge:hage}""")
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as MethodArgumentNotValidException
        assertThat(exception.bindingResult.allErrors).hasSize(1)
        assertThat(exception.bindingResult.fieldErrors[0].field).isEqualTo("attributes")
        assertThat(exception.bindingResult.fieldErrors[0].defaultMessage).isEqualTo("must match json string format")
    }

    /**
     * Controller から自前の Exception を throw した.
     */
    @Test
    fun testCreateTask_ExsitsTaskCode_409() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, request)

        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.CONFLICT.value())
        val exception = actual.resolvedException as HttpConflictException
        assertThat(exception.message).isEqualTo("Task(${request.taskCode}) は既に存在しています")
    }
}
