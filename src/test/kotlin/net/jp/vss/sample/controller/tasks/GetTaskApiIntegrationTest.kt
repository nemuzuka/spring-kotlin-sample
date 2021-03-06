package net.jp.vss.sample.controller.tasks

import net.jp.vss.sample.controller.exceptions.HttpNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import javax.validation.ConstraintViolationException

/**
 * GetTaskApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class GetTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks/{task_code}"
    }

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var flyway: Flyway

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
    fun testGetTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, request)

        // execution
        mockMvc.perform(get(PATH, request.taskCode)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo(request.taskCode)))
            .andExpect(jsonPath("$.status", equalTo("OPEN")))
            .andExpect(jsonPath("$.attributes.hoge", equalTo("hage")))
    }

    @Test
    fun testGetTask_NotFoundTask_404() {
        // execution
        val actual = mockMvc.perform(get(PATH, "absent_task_code")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.NOT_FOUND.value())
        val exception = actual.resolvedException as HttpNotFoundException
        assertThat(exception.message).isEqualTo("Task(absent_task_code) は存在しません")
    }

    @Test
    fun testGetTask_InvalidParameter_400() {
        // execution
        val actual = mockMvc.perform(get(PATH, "_TASK_001")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn()

        // verify
        assertThat(actual.response.status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        val exception = actual.resolvedException as ConstraintViolationException
        assertThat(exception.message).isEqualTo("getTask.taskCode: must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }

    @Test
    fun testGetNewTask() {
        // execution
        mockMvc.perform(get(PATH, "_new")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(jsonPath("$.task_code", equalTo("")))
            .andExpect(jsonPath("$.title", equalTo("")))
            .andExpect(jsonPath("$.content", equalTo("")))
            .andExpect(jsonPath("$.attributes", nullValue()))
            .andExpect(jsonPath("$.deadline", nullValue()))
    }
}
