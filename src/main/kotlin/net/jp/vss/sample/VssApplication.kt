package net.jp.vss.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Application Main.
 */
@SpringBootApplication
class VssApplication

/**
 * メイン処理.
 */
fun main(args: Array<String>) {
    runApplication<VssApplication>(*args)
}
