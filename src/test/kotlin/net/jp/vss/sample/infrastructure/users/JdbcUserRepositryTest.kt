package net.jp.vss.sample.infrastructure.users

import net.jp.vss.sample.JdbcRepositoryUnitTest
import net.jp.vss.sample.domain.users.User
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * JdbcUserRepositry のテスト.
 */
@JdbcRepositoryUnitTest
@RunWith(SpringJUnit4ClassRunner::class)
class JdbcUserRepositryTest {

    @Autowired
    private lateinit var sut: JdbcUserRepositry

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testGetUserOrNull_Exists() {
        // setup
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("google")
        val principal = User.Principal("principal_002")

        // execution
        val actual = sut.getUserOrNull(authorizedClientRegistrationId, principal)

        // verify
        val userId = User.UserId("user_id_002")
        val userCode = User.UserCode("user_code_002")
        val userDetail = User.UserDetail("名前002")
        val expected = User(userId = userId, userCode = userCode, userDetail = userDetail)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @FlywayTest(locationsForMigrate = ["/db/fixtures_user"])
    fun testGetUserOrNull_NotExists() {
        // setup
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_002")

        // execution
        val actual = sut.getUserOrNull(authorizedClientRegistrationId, principal)

        // verify
        assertThat(actual).isNull()
    }
}
