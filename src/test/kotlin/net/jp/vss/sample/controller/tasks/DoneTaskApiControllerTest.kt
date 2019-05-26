package net.jp.vss.sample.controller.tasks

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.exceptions.NotFoundException
import net.jp.vss.sample.domain.exceptions.UnmatchVersionException
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import net.jp.vss.sample.usecase.tasks.UpdateTaskUseCase
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
 * DoneTaskApiController のテスト.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class DoneTaskApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    @Test
    @WithMockUser
    fun testDoneTask() {
        // setup
        val updatedTask = TaskFixtures.create()
        whenever(updateTaskUseCase.done(any(), any(), any())).thenReturn(TaskUseCaseResult.of(updatedTask))

        val taskCode = "TASK_00001"

        // execution
        mockMvc.perform(post("/api/tasks/{taskCode}/_done?version=123", taskCode)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("task_code").value(`is`(updatedTask.taskCode.value)))

        verify(updateTaskUseCase).done(taskCode, 123L, "DUMMY_DONE_USER_CODE")
    }

    @Test
    @WithMockUser
    fun testDoneTask_NullVersion() {
        // setup
        val updatedTask = TaskFixtures.create()
        whenever(updateTaskUseCase.done(any(), anyOrNull(), any())).thenReturn(TaskUseCaseResult.of(updatedTask))

        val taskCode = "TASK_00001"

        // execution
        mockMvc.perform(post("/api/tasks/{taskCode}/_done", taskCode)
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("task_code").value(`is`(updatedTask.taskCode.value)))

        verify(updateTaskUseCase).done(taskCode, null, "DUMMY_DONE_USER_CODE")
    }

    @Test
    @WithMockUser
    fun testDoneTask_ConflictTaskVersion_409() {
        // setup
        val exception = UnmatchVersionException("dummy")
        whenever(updateTaskUseCase.done(any(), anyOrNull(), any())).thenThrow(exception)

        // execution
        mockMvc.perform(post("/api/tasks/dummy_0001/_done")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isConflict)
    }

    @Test
    @WithMockUser
    fun testDoneTask_NotFoundTask_404() {
        // setup
        val exception = NotFoundException("dummy")
        whenever(updateTaskUseCase.done(any(), anyOrNull(), any())).thenThrow(exception)

        // execution
        mockMvc.perform(post("/api/tasks/dummy_0001/_done")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isNotFound)
    }
}
