package net.jp.vss.sample.controller.users

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.sample.usecase.users.UpdateUserUseCaseParameter
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * UpdateUserController のパラメータ.
 */
data class UpdateUserApiParameter(

    @field:NotNull
    @field:Size(max = 128)
    @field:JsonProperty("user_name")
    val userName: String? = null
) {
    /**
     * UpdateUserUseCaseParameter に変換.
     *
     * @param userCode ユーザコード
     * @return 生成 UpdateUserUseCaseParameter
     */
    fun toParameter(userCode: String): UpdateUserUseCaseParameter =
        UpdateUserUseCaseParameter(
            userCode = userCode,
            userName = this.userName!!)
}
