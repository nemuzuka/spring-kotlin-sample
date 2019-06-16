package net.jp.vss.sample.usecase.tasks

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.domain.tasks.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * GetTaskUseCaseImpl のテスト.
 */
@RunWith(SpringJUnit4ClassRunner::class)
class GetTaskUseCaseImplTest {
    @Mock
    private lateinit var taskRepo: TaskRepository

    @InjectMocks
    private lateinit var sut: GetTaskUseCaseImpl

    @Test
    fun testGetTask() {
        // setup
        val task = TaskFixtures.create()
        whenever(taskRepo.getTask(any())).thenReturn(task)

        // execution
        val actual = sut.getTask(task.taskCode.value)

        // verify
        assertThat(actual).isEqualTo(TaskUseCaseResult.of(task))

        verify(taskRepo).getTask(task.taskCode)
    }
}
