package net.jp.vss.sample.usecase.users

import net.jp.vss.sample.domain.users.UserFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * UpdateUserUseCaseParameter のテスト.
 */
class UpdateUserUseCaseParameterTest {

    @Test
    fun buildUpdateUserTest() {
        // setup
        val user = UserFixtures.create()
        val sut = UpdateUserUseCaseParameterFixture.create()

        // execution
        val actual = sut.buildUpdateUser(user)

        // verify
        val baseUserDetail = user.userDetail
        val userDetail = baseUserDetail.copy(userName = sut.userName)
        val expected = user.copy(userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
    }
}
