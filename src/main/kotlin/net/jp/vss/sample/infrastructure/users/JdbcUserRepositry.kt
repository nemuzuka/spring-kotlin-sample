package net.jp.vss.sample.infrastructure.users

import net.jp.vss.sample.domain.users.User
import net.jp.vss.sample.domain.users.UserRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 * RDBMS にアクセスする UserRepository の実装.
 */
@Repository
class JdbcUserRepositry(private val jdbcTemplate: JdbcTemplate) : UserRepository {

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
}
