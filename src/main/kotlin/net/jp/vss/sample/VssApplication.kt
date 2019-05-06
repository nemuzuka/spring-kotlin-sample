package net.jp.vss.sample

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.boot.web.servlet.ServletContextInitializer
import javax.servlet.SessionTrackingMode

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

/**
 * Security に関する 設定.
 */
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/css/**", "/js/**", "/img/**", "/", "/index.html", "/favicon.ico")
            .permitAll()
            .anyRequest().authenticated()

        http.csrf().csrfTokenRepository(getCsrfTokenRepository())

        http.formLogin().disable()
        http.httpBasic().disable()
    }

    @Bean
    fun getCsrfTokenRepository(): CsrfTokenRepository {
        // Javascript から取得できるように Httponly を false にする
        val tokenRepository: CookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
        tokenRepository.cookiePath = "/"
        return tokenRepository
    }
}

@Bean
fun servletContextInitializer(@Value("\${secure.cookie}") secure: Boolean): ServletContextInitializer {

    return ServletContextInitializer { servletContext ->
        servletContext.sessionCookieConfig.isHttpOnly = false
        servletContext.sessionCookieConfig.isSecure = secure
        servletContext.setSessionTrackingModes(setOf(SessionTrackingMode.COOKIE))
    }
}
