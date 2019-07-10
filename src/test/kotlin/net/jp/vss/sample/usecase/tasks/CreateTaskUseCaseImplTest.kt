package net.jp.vss.sample.usecase.tasks

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.DatetimeUtils
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.domain.tasks.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * CreateTaskUseCaseImpl のテスト.
 */
@ExtendWith(SpringExtension::class)
class CreateTaskUseCaseImplTest {

    @Mock
    private lateinit var taskRepo: TaskRepository

    @InjectMocks
    private lateinit var sut: CreateTaskUseCaseImpl

    companion object {
        const val NOW = 1546268400002L
    }

    @BeforeEach
    fun setUp() {
        DatetimeUtils.setDummyDatetimeResource(NOW)
    }

    @AfterEach
    fun tearDown() {
        DatetimeUtils.clearDummyDatetimeResource()
    }

    @Test
    fun testCreateTask() {
        // setup
        val createdTask = TaskFixtures.create()
        whenever(taskRepo.createTask(any())).thenReturn(createdTask)

        val input = CreateTaskUseCaseParameterFixtures.create()

        // execution
        val actual = sut.createTask(input)

        // verify
        assertThat(actual).isEqualTo(TaskUseCaseResult.of(createdTask))

        // org.mockito.ArgumentCaptor を使用する代わり
        argumentCaptor<Task>().apply {
            verify(taskRepo).createTask(capture())

            val capturedTask = firstValue // getValue と同意
            val expectedTask = Task(taskId = capturedTask.taskId,
                taskCode = Task.TaskCode(input.taskCode),
                status = Task.TaskStatus.OPEN,
                taskDetail = Task.TaskDetail(title = input.title,
                    content = input.content,
                    deadline = input.deadline,
                    attributes = Attributes(input.attributes!!)),
                resourceAttributes = ResourceAttributes(createUserCode = input.createUserCode,
                    createAt = NOW,
                    lastUpdateUserCode = input.createUserCode,
                    lastUpdateAt = NOW,
                    version = 0L))
            assertThat(capturedTask).isEqualTo(expectedTask)
        }
    }
}
