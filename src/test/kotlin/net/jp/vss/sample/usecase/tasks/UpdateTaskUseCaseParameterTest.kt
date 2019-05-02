package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.DatetimeUtils
import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.tasks.TaskFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * UpdateTaskUseCaseParameter のテスト.
 */
class UpdateTaskUseCaseParameterTest {

    companion object {
        const val NOW = 1545368400001L
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
    fun buildUpdateTaskTest() {
        // setup
        val task = TaskFixtures.create()
        val sut = UpdateTaskUseCaseParameterFixture.create()

        // execution
        val actual = sut.buildUpdateTask(task)

        // verify
        val baseTaskDetail = task.taskDetail
        val updateDeadline = sut.deadline
        val taskDetail = baseTaskDetail.copy(
            title = sut.title!!,
            content = sut.content!!,
            deadline = updateDeadline,
            attributes = Attributes(sut.attributes!!)
        )
        val expected = task.copy(
            taskDetail = taskDetail,
            resourceAttributes = task.resourceAttributes.buildForUpdate(sut.updateUserCode))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun buildUpdateTaskTest_NullValue() {
        // setup
        val task = TaskFixtures.create()
        val sut = UpdateTaskUseCaseParameterFixture.createNullValue(false)

        // execution
        val actual = sut.buildUpdateTask(task)

        // verify
        val baseTaskDetail = task.taskDetail
        val taskDetail = baseTaskDetail.copy(
            title = baseTaskDetail.title,
            content = baseTaskDetail.content,
            deadline = baseTaskDetail.deadline,
            attributes = baseTaskDetail.attributes
        )
        val expected = task.copy(
            taskDetail = taskDetail,
            resourceAttributes = task.resourceAttributes.buildForUpdate(sut.updateUserCode))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun buildUpdateTaskTest_SetDeadlineToNull() {
        // setup
        val task = TaskFixtures.create()
        val sut = UpdateTaskUseCaseParameterFixture.createNullValue(true)

        // execution
        val actual = sut.buildUpdateTask(task)

        // verify
        val baseTaskDetail = task.taskDetail
        val taskDetail = baseTaskDetail.copy(
            title = baseTaskDetail.title,
            content = baseTaskDetail.content,
            deadline = null, // null を設定する事
            attributes = baseTaskDetail.attributes
        )
        val expected = task.copy(
            taskDetail = taskDetail,
            resourceAttributes = task.resourceAttributes.buildForUpdate(sut.updateUserCode))
        assertThat(actual).isEqualTo(expected)
    }
}
