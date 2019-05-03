package net.jp.vss.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

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

/**
 * CORS 用の設定.
 *
 * SpringBoot なので、WebMvcConfigurer を Bean 登録
 */
@Configuration
class WebMvcConfiguration {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry
                    .addMapping("/**")
                    .allowedMethods(CorsConfiguration.ALL)
                    .allowedOrigins(CorsConfiguration.ALL)
                    .allowedHeaders(CorsConfiguration.ALL)
                    .allowCredentials(true)
            }
        }
    }
}
