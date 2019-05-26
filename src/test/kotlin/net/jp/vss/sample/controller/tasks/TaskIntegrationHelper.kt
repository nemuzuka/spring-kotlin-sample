package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

/**
 * Task の IntegrationTest のヘルパー.
 */
@Component
class TaskIntegrationHelper {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val CREATE_TASK_PATH = "/api/tasks"
    }

    /**
     * CreateTask 呼び出し.
     *
     * @param mockMvc MockMvc
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createTask(mockMvc: MockMvc, parameter: CreateTaskApiParameter): MvcResult {

        val content = objectMapper.writeValueAsString(parameter)
        val response = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_TASK_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andReturn()
        assertThat(response.response.status).isEqualTo(HttpStatus.OK.value())
        return response
    }
}
