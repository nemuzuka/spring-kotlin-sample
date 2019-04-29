package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.DatetimeUtils
import net.jp.vss.sample.domain.ResourceAttributes
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Task のテスト.
 */
class TaskTest {

    companion object {
        const val NOW = 1546268400001L
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
    fun testBuildForCreate() {
        // setup
        val taskCodeValue = "TASK-0001"
        val title = "件名001"
        val content = "内容"
        val deadline = 1557014400000L
        val attributeJsonString = """{"hoge":"hige"}"""
        val createUserCode = "CUSTOMER-0003"

        // execution
        val actual = Task.buildForCreate(taskCodeValue, title, content, deadline, attributeJsonString, createUserCode)

        // verify
        val taskId = actual.taskId // 処理内で生成する為
        val taskCode = Task.TaskCode(taskCodeValue)
        val taskDetail = Task.TaskDetail(title = title,
                content = content,
                deadline = deadline,
                attributes = Attributes(attributeJsonString))
        val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
        val expected = Task(taskId = taskId, taskCode = taskCode, status = Task.TaskStatus.OPEN,
                taskDetail = taskDetail, resourceAttributes = resourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testDone() {
        // setup
        val sut = TaskFixtures.create()
        val updateUserCode = "CUSTOMER-0004"

        // execution
        val actual = sut.done(updateUserCode)

        // verify
        val updatedResourceAttributes = sut.resourceAttributes.copy(lastUpdateAt = NOW,
                lastUpdateUserCode = updateUserCode,
                version = sut.resourceAttributes.version + 1)
        val expected = sut.copy(status = Task.TaskStatus.DONE, resourceAttributes = updatedResourceAttributes)
        assertThat(actual).isEqualTo(expected)
    }
}
