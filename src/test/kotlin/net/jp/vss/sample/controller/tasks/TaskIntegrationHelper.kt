package net.jp.vss.sample.controller.tasks

import org.assertj.core.api.Assertions
import org.slf4j.LoggerFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

/**
 * Task の IntegrationTest のヘルパー.
 */
class TaskIntegrationHelper {

    companion object {
        const val PATH_BASE = "/api/tasks"
        private val log = LoggerFactory.getLogger(TaskIntegrationHelper::class.java)
    }

    /**
     * CreateTask 呼び出し
     *
     * @param restTemplate TestRestTemplate
     * @param parameter パラメータ
     * @return レスポンス
     */
    fun createTask(restTemplate: TestRestTemplate, parameter: CreateTaskApiParameter): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val postRequestEntity = HttpEntity(parameter, httpHeaders)
        val response = restTemplate.exchange(PATH_BASE, HttpMethod.POST, postRequestEntity, String::class.java)
        log.info("CreateTask response={}", response)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        return response
    }
}
