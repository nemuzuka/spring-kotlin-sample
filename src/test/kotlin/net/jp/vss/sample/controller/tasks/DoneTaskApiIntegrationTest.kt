package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import net.jp.vss.sample.controller.exceptions.HttpConflictException
import net.jp.vss.sample.controller.exceptions.HttpNotFoundException
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.infrastructure.tasks.JdbcTaskRepositry
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import javax.validation.ConstraintViolationException

/**
 * DoneTaskApiController の IntegrationTest.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class DoneTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}/_done"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var flyway: Flyway

    @Autowired
    private lateinit var jdbcTaskRepo: JdbcTaskRepositry

    @Autowired
    private val taskIntegrationHelper = TaskIntegrationHelper()

    private lateinit var mockMvc: MockMvc

    @Before
    fun setUp() {
        flyway.clean()
        flyway.migrate()

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    @Test
    fun testDoneTask() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        // execution
        val path = "$PATH?version=0"
        mockMvc.perform(MockMvcRequestBuilders.post(path, taskCode)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(taskCode)))
            .andExpect(jsonPath("$.status", equalTo("DONE")))
            .andExpect(jsonPath("$.title", equalTo(createRequest.title)))
            .andExpect(jsonPath("$.content", equalTo(createRequest.content)))
            .andExpect(jsonPath("$.deadline", equalTo(createRequest.deadline)))
            .andExpect(jsonPath("$.version", equalTo(1)))

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
        // execution
        val actual = mockMvc.perform(MockMvcRequestBuilders.post(PATH, "absent_task_code")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        assertThat(exception.message).isEqualTo("Task(absent_task_code) は存在しません")
    }

    @Test
    fun testDoneTask_InvalidVersion_409() {
        // setup
        val createRequest = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, createRequest)
        val taskCode = createRequest.taskCode

        // execution
        val path = "$PATH?version=1" // invalid version
        val actual = mockMvc.perform(MockMvcRequestBuilders.post(path, taskCode)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.CONFLICT.value())
        val exception = actual.resolvedException as HttpConflictException
        assertThat(exception.message).isEqualTo("指定した version が不正です")
    }

    @Test
    fun testDoneTask_InvalidPathParameter_400() {
        // execution
        val actual = mockMvc.perform(MockMvcRequestBuilders.post(PATH, "_TASK_001")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as ConstraintViolationException
        assertThat(exception.message).isEqualTo("doneTask.taskCode: must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }
}
