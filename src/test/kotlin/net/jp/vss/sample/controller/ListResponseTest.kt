package net.jp.vss.sample.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * ListResponse のテスト.
 */
class ListResponseTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = ListResponse(listOf("code1", "code2", "code3"))

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """{"list":["code1","code2","code3"]}"""
        assertThat(actual).isEqualTo(expected)
    }
}
