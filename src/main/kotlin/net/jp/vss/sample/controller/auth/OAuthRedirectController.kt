package net.jp.vss.sample.controller.auth

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * OAuth 認証後のリダイレクト先となる Controller.
 */
@Controller
@RequestMapping("/login/oauth2/client")
class OAuthRedirectController {

    @RequestMapping(value = ["/google"], method = [RequestMethod.GET])
    fun authenticatedGoogle(): String {
        return "redirect:/#/google-user-settings"
    }
}
