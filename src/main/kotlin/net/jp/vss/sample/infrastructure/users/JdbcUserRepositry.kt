package net.jp.vss.sample.infrastructure.users

import net.jp.vss.sample.domain.exceptions.DuplicateException
import net.jp.vss.sample.domain.users.User
import net.jp.vss.sample.domain.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする UserRepository の実装.
 */
@Repository
class JdbcUserRepositry(private val jdbcTemplate: JdbcTemplate) : UserRepository {

    companion object {
        private val log = LoggerFactory.getLogger(JdbcUserRepositry::class.java)
    }

    private val rowMapper = RowMapper { rs, _ ->
        val userId = User.UserId(rs.getString("user_id"))
        val userCode = User.UserCode(rs.getString("user_code"))
        val userDetail = User.UserDetail(rs.getString("user_name"))
        User(userId = userId, userCode = userCode, userDetail = userDetail)
    }

    override fun getUserOrNull(
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User? {
        val sql = """
            | SELECT
            |   u.user_id,
            |   u.user_code,
            |   u.user_name
            | FROM
            |   authenticated_principals ap
            |   INNER JOIN users u ON ap.authenticated_principal_id = u.authenticated_principal_id
            | WHERE
            |   ap.principal = ?
            |   AND ap.authorized_client_registration_id = ?
        """.trimMargin()
        return jdbcTemplate.query(sql, rowMapper, principal.value, authorizedClientRegistrationId.value).firstOrNull()
    }

    fun getUserOrNull(userCode: User.UserCode): User? {
        val sql = """
            | SELECT
            |   u.user_id,
            |   u.user_code,
            |   u.user_name
            | FROM
            |   users u
            | WHERE
            |   u.user_code = ?
        """.trimMargin()
        return jdbcTemplate.query(sql, rowMapper, userCode.value).firstOrNull()
    }

    override fun createUser(
        user: User,
        authenticatedPrincipalId: User.AuthenticatedPrincipalId,
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User {

        try {

            val insertAuthenticatedPrincipalsSql = """
                | INSERT INTO authenticated_principals(
                |   authenticated_principal_id,
                |   principal,
                |   authorized_client_registration_id
                | ) values (?, ?, ?)
            """.trimMargin()
            jdbcTemplate.update(insertAuthenticatedPrincipalsSql,
                authenticatedPrincipalId.value, principal.value, authorizedClientRegistrationId.value)

            val insertUserSql = """
                | INSERT INTO users(
                |   user_id,
                |   user_code,
                |   authenticated_principal_id,
                |   user_name
                | ) values (?, ?, ?, ?)
            """.trimMargin()
            jdbcTemplate.update(insertUserSql,
                user.userId.value, user.userCode.value, authenticatedPrincipalId.value,
                user.userDetail.userName)
            return user
        } catch (e: DuplicateKeyException) {
            log.info("Duplicate key: {}", e.message, e)
            val message = "User(${user.userCode.value}) は既に存在しています"
            throw DuplicateException(message)
        }
    }
}
