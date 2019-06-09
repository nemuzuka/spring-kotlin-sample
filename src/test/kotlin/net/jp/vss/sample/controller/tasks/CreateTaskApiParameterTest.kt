package net.jp.vss.sample.controller.tasks

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.validation.Validation

import com.google.common.base.Strings.repeat
import org.slf4j.LoggerFactory

/**
 * CreateTaskApiParameter のテスト.
 */
class CreateTaskApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(CreateTaskApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = CreateTaskApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "taskCode",
            "title",
            "content")
        assertThat(actual).hasSize(3)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = CreateTaskApiParameterFixtures.create()

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(0)
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains() {
        // setup
        val sut = CreateTaskApiParameter(
            taskCode = repeat("x", 129),
            title = repeat("x", 257),
            content = "dummy", // dummy data
            attributes = """{"invalid":json_value}"""
        )

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(4)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "taskCode size must be between 0 and 128",
            "taskCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"",
            "title size must be between 0 and 256",
            "attributes must match json string format")
    }

    /**
     * 異常パターンのテスト.
     */
    @Test
    fun testViolatePatternConstrains_Format() {
        // setup
        val sut = CreateTaskApiParameter(
            taskCode = "タスクコード", // 正規表現外
            title = "dummy", // dummy data
            content = "dummy") // dummy data

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(1)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "taskCode must match \"[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}\"")
    }

    /**
     * newTask のテスト.
     */
    @Test
    fun testNewTask() {
        // execution
        val actual = CreateTaskApiParameter.newTask()

        // verify
        val expected = CreateTaskApiParameter(
            taskCode = "",
            title = "",
            content = "",
            attributes = null,
            deadline = null)
        assertThat(actual).isEqualTo(expected)
    }
}
