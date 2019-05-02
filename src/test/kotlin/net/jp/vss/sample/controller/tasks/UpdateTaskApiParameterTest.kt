package net.jp.vss.sample.controller.tasks

import com.google.common.base.Strings
import net.jp.vss.sample.usecase.tasks.UpdateTaskUseCaseParameter
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.slf4j.LoggerFactory
import javax.validation.Validation

/**
 * UpdateTaskApiParameter の Test.
 */
class UpdateTaskApiParameterTest {

    companion object {
        private val log = LoggerFactory.getLogger(UpdateTaskApiParameterTest::class.java)
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator

    /**
     * NotNull のプロパティのテスト.
     */
    @Test
    fun testNotNullConstrains() {
        // setup
        val sut = UpdateTaskApiParameter()

        // execution
        val actual = validator.validate(sut)

        // verify
        val errors = actual.map { violation -> violation.propertyPath.toString() }.toSet()
        errors.forEach(log::info)
        Assertions.assertThat(errors).containsExactlyInAnyOrder(
            "isSetDeadlineToNull")
        Assertions.assertThat(actual).hasSize(1)
    }

    /**
     * 通常パターンのテスト.
     */
    @Test
    fun testNormalPatternConstrains() {
        // setup
        val sut = UpdateTaskApiParameterFixtures.create()

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
        val sut = UpdateTaskApiParameter(
            title = Strings.repeat("x", 257),
            isSetDeadlineToNull = false, // dummy
            attributes = """{"invalid":json_value}""")

        // execution
        val actual = validator.validate(sut)

        // verify
        assertThat(actual).hasSize(2)
        val errors = actual.map { violation -> "${violation.propertyPath} ${violation.message}" }.toSet()
        errors.forEach(log::info)
        assertThat(errors).containsExactlyInAnyOrder(
            "title size must be between 0 and 256",
            "attributes must match json string format")
    }

    @Test
    fun testToParameter() {
        // setup
        val sut = UpdateTaskApiParameterFixtures.create()
        val taskCode = "TASK_00001"
        val version = 12L
        val updateUserCode = "UPDATE_USER_0004"

        // execution
        val actual = sut.toParameter(taskCode, version, updateUserCode)

        // verify
        val expected = UpdateTaskUseCaseParameter(
            taskCode = taskCode,
            title = sut.title,
            content = sut.content,
            deadline = sut.deadline,
            isSetDeadlineToNull = sut.isSetDeadlineToNull!!,
            attributes = sut.attributes,
            version = version,
            updateUserCode = updateUserCode)
        assertThat(actual).isEqualTo(expected)
    }
}
