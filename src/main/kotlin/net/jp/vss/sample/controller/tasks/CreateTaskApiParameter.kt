package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.sample.constrains.JsonStringConstrains
import net.jp.vss.sample.usecase.tasks.CreateTaskUseCase
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * CreateTaskController のパラメータ.
 *
 * data class の場合、既存の奴は `@field:NotNull` と指定する必要がある
 */
data class CreateTaskApiParameter(

    @field:NotNull
    @field:Pattern(regexp = "[a-zA-Z0-9][-a-zA-Z0-9_]{0,127}")
    @field:Size(max = 128)
    @field:JsonProperty("task_code_value")
    val taskCodeValue: String? = null,

    @field:NotNull
    @field:Size(max = 256)
    @field:JsonProperty("title")
    val title: String? = null,

    @field:NotNull
    @field:JsonProperty("content")
    val content: String? = null,

    @field:JsonProperty("deadline")
    val deadline: Long? = null,

    @JsonStringConstrains // kotlin で作成したので field は不要
    @field:JsonProperty("attribute_json_string")
    val attributeJsonString: String? = null
) {
    /**
     * CreateTaskUseCase.Input に変換.
     *
     * @param createCustomerCode 登録ユーザコード
     */
    fun toInput(createCustomerCode: String): CreateTaskUseCase.Input = CreateTaskUseCase.Input(
        taskCodeValue = taskCodeValue!!,
        title = title!!,
        content = content!!,
        attributeJsonString = attributeJsonString,
        deadline = deadline,
        createUserCode = createCustomerCode)
}
