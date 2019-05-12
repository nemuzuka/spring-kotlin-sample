package net.jp.vss.sample.controller.tasks

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.usecase.tasks.ListTaskUseCase
import net.jp.vss.sample.usecase.tasks.TaskUseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * ListTaskApiController のテスト.
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class ListTaskApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var listTaskUseCase: ListTaskUseCase

    @Test
    @WithMockUser
    fun testListTask() {
        // setup
        val task = TaskFixtures.create()
        whenever(listTaskUseCase.allTasks()).thenReturn(listOf(TaskUseCaseResult.of(task)))

        // execution
        mockMvc.perform(get("/api/tasks/")
            .contentType(MediaType.APPLICATION_JSON))
            // verify
            .andExpect(status().isOk)
            .andExpect(jsonPath("elements[0].task_code").value(`is`(task.taskCode.value)))

        verify(listTaskUseCase).allTasks()
    }
}
