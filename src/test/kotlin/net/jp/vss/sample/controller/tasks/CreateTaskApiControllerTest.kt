package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.usecase.tasks.CreateTaskUseCase
import net.jp.vss.sample.usecase.tasks.CreateTaskUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
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
    fun testCreateTask() {
        // setup
        val createdTask = TaskFixtures.create()
        whenever(createTaskUseCase.createTask(any())).thenReturn(CreateTaskUseCaseResult.of(createdTask))

        val parameter = CreateTaskApiParameterFixtures.create()
        val mapper = ObjectMapper()
        val content = mapper.writeValueAsString(parameter)

        // execution
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("task_code").value(`is`(createdTask.taskCode.value)))

        verify(createTaskUseCase).createTask(parameter.toParameter("DUMMY_USER_CODE"))
    }

    @Test
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
            .content(content))
            // verify
            .andExpect(status().isConflict)
    }
}
