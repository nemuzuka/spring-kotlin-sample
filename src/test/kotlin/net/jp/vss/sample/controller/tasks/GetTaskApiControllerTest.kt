package net.jp.vss.sample.controller.tasks

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.exceptions.NotFoundException
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.usecase.tasks.GetTaskUseCase
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * GetTaskApiController のテスト.
 */
@RunWith(SpringRunner::class)
@WebMvcTest(GetTaskApiController::class)
class GetTaskApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var getTaskUseCase: GetTaskUseCase

    @Test
    @WithMockUser
    fun testGetTask() {
        // setup
        val task = TaskFixtures.create()
        whenever(getTaskUseCase.getTask(any())).thenReturn(TaskUseCaseResult.of(task))
        val taskCode = "TASK_CODE_0001"

        // execution
        mockMvc.perform(get("/api/tasks/{task_code}", taskCode)
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("task_code").value(`is`(task.taskCode.value)))

        verify(getTaskUseCase).getTask(taskCode)
    }

    @Test
    @WithMockUser
    fun testGetTask_NotFoundTask_404() {
        // setup
        val exception = NotFoundException("dummy")
        whenever(getTaskUseCase.getTask(any())).thenThrow(exception)

        // execution
        mockMvc.perform(get("/api/tasks/{task_code}", "absent_task_code")
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isNotFound)
    }
}
