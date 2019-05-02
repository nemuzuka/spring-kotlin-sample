package net.jp.vss.sample.domain

import net.jp.vss.sample.DatetimeUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * ResourceAttributes のテスト.
 */
class ResourceAttributesTest {

    companion object {
        const val NOW = 1546268400000L
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
        val createUserCode = "CUSTOMER_USER_0001"

        // execution
        val actual = ResourceAttributes.buildForCreate("CUSTOMER_USER_0001")

        // verify
        val expected = ResourceAttributes(
            createUserCode = createUserCode,
            createAt = NOW,
            lastUpdateUserCode = createUserCode,
            lastUpdateAt = NOW,
            version = 0L)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testBuildForUpdate() {
        // setup
        val updateUserCode = "CUSTOMER_USER_0002"
        val sut = ResourceAttributesFixtures.create()

        // execution
        val actual = sut.buildForUpdate(updateUserCode)

        // verify
        val expected = sut.copy(lastUpdateUserCode = updateUserCode,
                lastUpdateAt = DatetimeUtils.now())
        assertThat(actual).isEqualTo(expected)
    }
}
