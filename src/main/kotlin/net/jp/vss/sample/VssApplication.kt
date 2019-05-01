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

    System.getenv("PORT")?.also {
        // Heroku 対応
        // 環境変数 PORT で指定した port で bind する事
        // Dockerfile の EXPOSE は無視する
        System.getProperties()["server.port"] = it
    }

    runApplication<VssApplication>(*args)
}
