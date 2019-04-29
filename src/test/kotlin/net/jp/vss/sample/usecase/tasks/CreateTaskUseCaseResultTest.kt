package net.jp.vss.sample.usecase.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * CreateTaskUseCaseResult のテスト.
 */
class CreateTaskUseCaseResultTest {

    companion object {
        val objectMapper = ObjectMapper()
    }

    @Test
    fun testJsonString() {
        // setup
        val sut = CreateTaskUseCaseResultFixtures.create()

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"create_user_code":"${sut.resourceAttributesResult.createUserCode}",
                |"create_at":${sut.resourceAttributesResult.createAt},
                |"last_update_user_code":"${sut.resourceAttributesResult.lastUpdateUserCode}",
                |"last_update_at":${sut.resourceAttributesResult.lastUpdateAt},
                |"version":${sut.resourceAttributesResult.version},
                |"task_code":"${sut.taskCode}",
                |"status":"${sut.status.name}",
                |"title":"${sut.title}",
                |"content":"${sut.content}",
                |"deadline":${sut.deadline},
                |"attributes":{
                    |"hoge":"hige",
                    |"fuga":{
                        |"neko":"nyan"
                    |}
                |}
            |}
        """.trimMargin().replace("\n", "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    /**
     * null を含むプロパティの JSON 化.
     */
    @Test
    fun testJsonString_NullValue() {
        // setup
        val sut = CreateTaskUseCaseResultFixtures.create()
            .copy(deadline = null, attributes = null)

        // execution
        val actual = objectMapper.writeValueAsString(sut)

        // verify
        val expected = """
            |{
                |"create_user_code":"${sut.resourceAttributesResult.createUserCode}",
                |"create_at":${sut.resourceAttributesResult.createAt},
                |"last_update_user_code":"${sut.resourceAttributesResult.lastUpdateUserCode}",
                |"last_update_at":${sut.resourceAttributesResult.lastUpdateAt},
                |"version":${sut.resourceAttributesResult.version},
                |"task_code":"${sut.taskCode}",
                |"status":"${sut.status.name}",
                |"title":"${sut.title}",
                |"content":"${sut.content}",
                |"deadline":null,
                |"attributes":null
            |}
        """.trimMargin().replace("\n", "")
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
