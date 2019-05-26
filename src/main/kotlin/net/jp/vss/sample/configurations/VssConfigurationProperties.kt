package net.jp.vss.sample.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "vss")
class VssConfigurationProperties {

    /** フロントへリダイレクトする際の URL. */
    lateinit var redirectBaseUrl: String
}
