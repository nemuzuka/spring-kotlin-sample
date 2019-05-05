package net.jp.vss.sample

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Base64

/**
 * IntegrationTest の ヘルパー.
 *
 * @property jdbcTemplate JdbcTemplate
 * @property csrfTokenRepository CsrfTokenRepository
 */
@Component
@Transactional(readOnly = true)
class IntegrationTestHelper(
    private val jdbcTemplate: JdbcTemplate,
    private val csrfTokenRepository: CsrfTokenRepository
) {

    fun basicAuthHeaders(): HttpHeaders {
        val plainCreds = "user:password"
        val plainCredsBytes = plainCreds.toByteArray()
        val base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes)
        val base64Creds = String(base64CredsBytes)

        val headers = HttpHeaders()
        headers.add("Authorization", "Basic $base64Creds")
        return headers
    }

    fun csrfHeaders(): HttpHeaders {
        val csrfToken = csrfTokenRepository.generateToken(null)
        val headers = basicAuthHeaders()

        headers.add(csrfToken.headerName, csrfToken.token)
        headers.add("Cookie", "XSRF-TOKEN=" + csrfToken.token)
        headers.contentType = MediaType.APPLICATION_JSON

        return headers
    }
}
