package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import net.jp.vss.sample.controller.exceptions.HttpConflictException
import net.jp.vss.sample.controller.exceptions.HttpNotFoundException
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.infrastructure.tasks.JdbcTaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.web.bind.MethodArgumentNotValidException
import javax.validation.ConstraintViolationException

/**
 * UpdateTaskApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class UpdateTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}"
        private val log = LoggerFactory.getLogger(UpdateTaskApiIntegrationTest::class.java)
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
    fun testUpdateTask() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameterFixtures.create().copy(isSetDeadlineToNull = false) // deadline の更新を確認したい
        val content = objectMapper.writeValueAsString(request)

        // execution
        val path = "$PATH?version=0"
        mockMvc.perform(post(path, taskCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(taskCode)))
            .andExpect(jsonPath("$.status", equalTo("OPEN")))
            .andExpect(jsonPath("$.title", equalTo(request.title)))
            .andExpect(jsonPath("$.content", equalTo(request.content)))
            .andExpect(jsonPath("$.deadline", equalTo(request.deadline)))
            .andExpect(jsonPath("$.attributes.hoge", equalTo("super_hoge")))
            .andExpect(jsonPath("$.version", equalTo(1)))

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
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameter(isSetDeadlineToNull = false) // null にされると困るので false
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(PATH, taskCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(taskCode)))
            .andExpect(jsonPath("$.status", equalTo("OPEN")))
            .andExpect(jsonPath("$.title", equalTo(createRequest.title)))
            .andExpect(jsonPath("$.content", equalTo(createRequest.content)))
            .andExpect(jsonPath("$.deadline", equalTo(createRequest.deadline)))
            .andExpect(jsonPath("$.attributes.hoge", equalTo("hage")))
            .andExpect(jsonPath("$.version", equalTo(1)))

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
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameter(isSetDeadlineToNull = true)
        val content = objectMapper.writeValueAsString(request)

        // execution
        mockMvc.perform(post(PATH, taskCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(taskCode)))
            .andExpect(jsonPath("$.status", equalTo("OPEN")))
            .andExpect(jsonPath("$.title", equalTo(createRequest.title)))
            .andExpect(jsonPath("$.content", equalTo(createRequest.content)))
            .andExpect(jsonPath("$.deadline", nullValue()))
            .andExpect(jsonPath("$.attributes.hoge", equalTo("hage")))
            .andExpect(jsonPath("$.version", equalTo(1)))

        // 永続化していること
        val updatedTask = jdbcTaskRepo.getTask(Task.TaskCode(taskCode!!))
        assertThat(updatedTask.taskDetail)
            .returns(null, Task.TaskDetail::deadline)
    }

    @Test
    fun testUpdateTask_NotFoundTask_404() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "absent_task_code")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        assertThat(exception.message).isEqualTo("Task(absent_task_code) は存在しません")
    }

    @Test
    fun testUpdateTask_InvalidVersion_409() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        val request = UpdateTaskApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val path = "$PATH?version=1" // invalid version
        val actual = mockMvc.perform(post(path, taskCode)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.CONFLICT.value())
        val exception = actual.resolvedException as HttpConflictException
        assertThat(exception.message).isEqualTo("指定した version が不正です")
    }

    @Test
    fun testUpdateTask_InvalidJsonParameter_400() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create().copy(attributes = """{hoge:hage}""")
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "TASK_A_0001")
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

    @Test
    fun testUpdateTask_InvalidPathParameter_400() {
        // setup
        val request = UpdateTaskApiParameterFixtures.create()
        val content = objectMapper.writeValueAsString(request)

        // execution
        val actual = mockMvc.perform(post(PATH, "_TASK_001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as ConstraintViolationException
        assertThat(exception.message).isEqualTo("updateTask.taskCode: must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
