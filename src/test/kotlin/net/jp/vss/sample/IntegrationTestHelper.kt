package net.jp.vss.sample

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * IntegrationTest の ヘルパー.
 *
 * @property jdbcTemplate JdbcTemplate
 */
@Component
@Transactional(readOnly = true)
class IntegrationTestHelper(private val jdbcTemplate: JdbcTemplate) {
    fun getJdbcTemplate() = jdbcTemplate
}
