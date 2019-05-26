package net.jp.vss.sample.infrastructure.users

import net.jp.vss.sample.JdbcRepositoryUnitTest
import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.users.User
import net.jp.vss.sample.domain.users.UserFixtures
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.test.annotation.FlywayTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.UUID

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

    @Test
    @FlywayTest
    fun testCreateUser() {
        // setup
        val authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString())
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_001")
        val user = UserFixtures.create()

        // execution
        val actual = sut.createUser(user = user,
            authorizedClientRegistrationId = authorizedClientRegistrationId,
            authenticatedPrincipalId = authenticatedPrincipalId, principal = principal)

        // verify
        assertThat(actual).isNotNull.isEqualTo(user)
        assertThat(actual).isEqualTo(sut.getUserOrNull(user.userCode))
        assertThat(actual).isEqualTo(sut.getUserOrNull(authorizedClientRegistrationId, principal))
    }

    @Test
    @FlywayTest
    fun testCreateUser_AlreadyExistUserCode_DE() {
        // setup
        val authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString())
        val authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("facebook")
        val principal = User.Principal("principal_001")
        val user = UserFixtures.create()
        sut.createUser(user = user, authorizedClientRegistrationId = authorizedClientRegistrationId,
            authenticatedPrincipalId = authenticatedPrincipalId, principal = principal)

        // execution
        val actual = Assertions.catchThrowable {
            sut.createUser(user = user.copy(userId = User.UserId(UUID.randomUUID().toString())),
                authorizedClientRegistrationId = User.AuthorizedClientRegistrationId("google"),
                authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString()),
                principal = User.Principal("principal_002"))
        }

        // verify
        assertThat(actual).isInstanceOfSatisfying(DuplicateException::class.java) { e ->
            assertThat(e.message).isEqualTo("User(${user.userCode.value}) は既に存在しています")
        }
    }
}
