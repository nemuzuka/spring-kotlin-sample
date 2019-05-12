package net.jp.vss.sample.controller.auth

import net.jp.vss.sample.configurations.VssConfigurationProperties
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * OAuth 認証後のリダイレクト先となる Controller.
 */
@Controller
@RequestMapping("/login-success")
class OAuthRedirectController(private val vssConfigurationProperties: VssConfigurationProperties) {

    companion object {
        private val log = LoggerFactory.getLogger(OAuthRedirectController::class.java)
    }

    /**
     * 認証後のリダイレクト先ディスパッチ.
     *
     * 認証情報から登録済みユーザかを判定し、リダイレクトします
     * - 未登録の場合 -> ユーザ登録画面
     * - 登録済みの場合 -> TOP
     *
     * @return リダイレクト先 URL
     */
    @RequestMapping(method = [RequestMethod.GET])
    fun dispatch(): String {

        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
        val principal = authentication.principal
        log.info("{}", authentication)
        log.info("authorizedClientRegistrationId: {}", authentication.authorizedClientRegistrationId)
        log.info("principal: {}", principal.name)
        log.info("authorities: {}", authentication.authorities)
        log.info("credentials: {}", authentication.credentials)
        log.info("details: {}", authentication.details)
        log.info("redirectBaseUrl: {}", vssConfigurationProperties.redirectBaseUrl)

        return "redirect:${vssConfigurationProperties.redirectBaseUrl}/#/user-settings"
    }
}
