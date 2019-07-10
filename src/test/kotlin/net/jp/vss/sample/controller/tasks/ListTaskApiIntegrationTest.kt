package net.jp.vss.sample.controller.tasks

import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * ListTaskApiController の IntegrationTest.
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
class ListTaskApiIntegrationTest {

    companion object {
        const val PATH = "/api/tasks"
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
    fun testListTask() {
        // setup
        val request = CreateTaskApiParameterFixtures.create()
        taskIntegrationHelper.createTask(mockMvc, request)

        // execution
        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.elements", hasSize<Int>(1)))
            .andExpect(jsonPath("$.elements[0].task_code", equalTo(request.taskCode)))
            .andExpect(jsonPath("$.elements[0].status", equalTo("OPEN")))
            .andExpect(jsonPath("$.elements[0].attributes.hoge", equalTo("hage")))
    }

    @Test
    fun testListTask_Empty() {
        // execution
        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.elements", hasSize<Int>(0)))
    }
}
