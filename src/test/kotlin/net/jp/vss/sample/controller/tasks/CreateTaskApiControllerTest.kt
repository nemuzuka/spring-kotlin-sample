package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.usecase.tasks.CreateTaskUseCase
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * CreateTaskApiController のテスト.
 */
@RunWith(SpringRunner::class)
@WebMvcTest(CreateTaskApiController::class)
class CreateTaskApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var createTaskUseCase: CreateTaskUseCase

    @Test
    @WithMockUser // authorizeRequests を通す為
    fun testCreateTask() {
        // setup
        val createdTask = TaskFixtures.create()
        whenever(createTaskUseCase.createTask(any())).thenReturn(TaskUseCaseResult.of(createdTask))

        val parameter = CreateTaskApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF を有効にしているので Test 時にも設定
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("task_code").value(`is`(createdTask.taskCode.value)))

        verify(createTaskUseCase).createTask(parameter.toParameter("DUMMY_USER_CODE"))
    }

    @Test
    @WithMockUser
    fun testCreateTask_ConflictTaskCode_409() {
        // setup
        val exception = DuplicateException("dummy")
        whenever(createTaskUseCase.createTask(any())).thenThrow(exception)

        val parameter = CreateTaskApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .content(content))
            // verify
            .andExpect(status().isConflict)
    }

    /**
     * CSRF Token 未設定.
     */
    @Test
    @WithMockUser
    fun testCreateTask_NotCsrfToken_403() {
        // setup
        val parameter = CreateTaskApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isForbidden)
    }
}
