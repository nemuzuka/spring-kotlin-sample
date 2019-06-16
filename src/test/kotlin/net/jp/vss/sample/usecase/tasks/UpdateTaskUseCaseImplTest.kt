package net.jp.vss.sample.usecase.tasks

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.jp.vss.sample.DatetimeUtils
import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskFixtures
import net.jp.vss.sample.domain.tasks.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * UpdateTaskUseCaseImpl のテスト.
 */
@RunWith(SpringJUnit4ClassRunner::class)
class UpdateTaskUseCaseImplTest {

    @Mock
    private lateinit var taskRepo: TaskRepository

    @InjectMocks
    private lateinit var sut: UpdateTaskUseCaseImpl

    companion object {
        const val NOW = 1546268400002L
    }

    @Before
    fun setUp() {
        DatetimeUtils.setDummyDatetimeResource(NOW)
    }

    @After
    fun tearDown() {
        DatetimeUtils.clearDummyDatetimeResource()
    }

    @Test
    fun testUpdateTask() {
        // setup
        val task = TaskFixtures.create()
        whenever(taskRepo.lockTask(any())).thenReturn(task)
        val updatedTask = TaskFixtures.create("UPDATED_TASK_0001") // あえて別のインスタンスにする
        whenever(taskRepo.updateTask(any())).thenReturn(updatedTask)

        val parameter = UpdateTaskUseCaseParameterFixture.create()

        // execution
        val actual = sut.updateTask(parameter)

        // verify
        assertThat(actual).isEqualTo(TaskUseCaseResult.of(updatedTask))

        verify(taskRepo).lockTask(Task.TaskCode(parameter.taskCode))

        argumentCaptor<Task>().apply {
            verify(taskRepo).updateTask(capture())
            val capturedTask = firstValue
            val expectedTask = parameter.buildUpdateTask(task)
            assertThat(capturedTask).isEqualTo(expectedTask)
        }
    }

    @Test
    fun testDone() {
        // setup
        val task = TaskFixtures.create()
        whenever(taskRepo.lockTask(any())).thenReturn(task)
        val updatedTask = TaskFixtures.create("UPDATED_TASK_0001") // あえて別のインスタンスにする
        whenever(taskRepo.updateTask(any())).thenReturn(updatedTask)

        val taskCode = "TASK_00001"
        val updateUserCode = "UPDATE_USER_0012"
        val version = 3L

        // execution
        val actual = sut.done(taskCode, version, updateUserCode)

        // verify
        assertThat(actual).isEqualTo(TaskUseCaseResult.of(updatedTask))

        verify(taskRepo).lockTask(Task.TaskCode(taskCode))

        argumentCaptor<Task>().apply {
            verify(taskRepo).updateTask(capture())
            val capturedTask = firstValue
            val expectedTask = task.done(updateUserCode)
            assertThat(capturedTask).isEqualTo(expectedTask)
        }
    }

    @Test
    fun testReopen() {
        // setup
        val task = TaskFixtures.create()
        whenever(taskRepo.lockTask(any())).thenReturn(task)
        val updatedTask = TaskFixtures.create("UPDATED_TASK_0001") // あえて別のインスタンスにする
        whenever(taskRepo.updateTask(any())).thenReturn(updatedTask)

        val taskCode = "TASK_00001"
        val updateUserCode = "UPDATE_USER_0012"
        val version = 3L

        // execution
        val actual = sut.reopen(taskCode, version, updateUserCode)

        // verify
        assertThat(actual).isEqualTo(TaskUseCaseResult.of(updatedTask))

        verify(taskRepo).lockTask(Task.TaskCode(taskCode))

        argumentCaptor<Task>().apply {
            verify(taskRepo).updateTask(capture())
            val capturedTask = firstValue
            val expectedTask = task.reopen(updateUserCode)
            assertThat(capturedTask).isEqualTo(expectedTask)
        }
    }
}
