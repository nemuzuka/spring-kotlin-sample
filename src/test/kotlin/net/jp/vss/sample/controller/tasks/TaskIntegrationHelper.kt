package net.jp.vss.sample.controller.tasks

import org.assertj.core.api.Assertions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

/**
 * Task の IntegrationTest のヘルパー.
 */
@Component
class TaskIntegrationHelper {

    companion object {
        const val PATH_BASE = "/api/tasks"
        private val log = LoggerFactory.getLogger(TaskIntegrationHelper::class.java)
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    /**
     * CreateTask 呼び出し
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createTask(parameter: CreateTaskApiParameter): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(parameter, httpHeaders)
        val response = restTemplate.exchange(PATH_BASE, HttpMethod.POST, postRequestEntity, String::class.java)
        log.info("CreateTask response={}", response)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        return response
    }
}
