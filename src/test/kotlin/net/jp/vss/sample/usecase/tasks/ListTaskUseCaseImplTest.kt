package net.jp.vss.sample.usecase.tasks

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.domain.tasks.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ListTaskUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class ListTaskUseCaseImplTest {
    @Mock
    private lateinit var taskRepo: TaskRepository

    @InjectMocks
    private lateinit var sut: ListTaskUseCaseImpl

    @Test
    fun testAllTask() {
        // setup
        val task = TaskFixtures.create()
        whenever(taskRepo.allTasks()).thenReturn(listOf(task))

        // execution
        val actual = sut.allTasks()

        // verify
        assertThat(actual).isEqualTo(listOf(TaskUseCaseResult.of(task)))
        verify(taskRepo).allTasks()
    }
}
