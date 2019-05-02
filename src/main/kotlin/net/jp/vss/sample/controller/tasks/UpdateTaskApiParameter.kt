package net.jp.vss.sample.controller.tasks

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.sample.constrains.JsonStringConstrains
import net.jp.vss.sample.usecase.tasks.UpdateTaskUseCaseParameter
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * UpdateTaskController のパラメータ.
 */
data class UpdateTaskApiParameter(

    @field:Size(max = 256)
    @field:JsonProperty("title")
    val title: String? = null,

    @field:JsonProperty("content")
    val content: String? = null,

    @field:JsonProperty("deadline")
    val deadline: Long? = null,

    @field:NotNull
    @field:JsonProperty("is_set_deadline_to_null")
    val isSetDeadlineToNull: Boolean? = null,

    @JsonStringConstrains
    @field:JsonProperty("attributes")
    val attributes: String? = null
) {
    /**
     * UpdateTaskUseCaseParameter に変換.
     *
     * @param taskCode タスクコード
     * @param version version
     * @param updateUserCode 更新ユーザコード
     * @return 生成 UpdateTaskUseCaseParameter
     */
    fun toParameter(taskCode: String, version: Long?, updateUserCode: String): UpdateTaskUseCaseParameter =
        UpdateTaskUseCaseParameter(
            taskCode = taskCode,
            title = this.title,
            content = content,
            deadline = deadline,
            isSetDeadlineToNull = isSetDeadlineToNull!!,
            attributes = attributes,
            version = version,
            updateUserCode = updateUserCode)
}
