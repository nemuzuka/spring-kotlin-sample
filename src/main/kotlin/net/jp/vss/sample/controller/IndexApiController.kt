package net.jp.vss.sample.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

/**
 * 共通の ApiController.
 *
 * @property session HttpSession
 */
@RestController
@RequestMapping("/api")
@Validated
class IndexApiController(
    private val session: HttpSession
) {

    /**
     * init.
     *
     * TODO これはそのうち削除する
     * @return レスポンス
     */
    @RequestMapping(method = [RequestMethod.GET], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun init(): ResponseEntity<String> {
        session.setAttribute("DUMMY", "")
        return ResponseEntity.ok("")
    }

    /**
     * init.
     *
     * @return レスポンス
     */
    @RequestMapping(value = ["/_health"], method = [RequestMethod.GET], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun healthCheck(): ResponseEntity<String> {
        return ResponseEntity.ok("It's work!")
    }
}
