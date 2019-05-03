package net.jp.vss.sample.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


/**
 * TOP ページを表示する Controller.
 */
@Controller
class RootController {
    @GetMapping("/")
    fun index()="index"
}
