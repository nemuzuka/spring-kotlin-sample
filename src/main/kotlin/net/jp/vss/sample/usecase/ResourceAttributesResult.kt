package net.jp.vss.sample.usecase

import com.fasterxml.jackson.annotation.JsonProperty
import net.jp.vss.sample.domain.ResourceAttributes

/**
 * リソース付帯情報の結果.
 *
 * @property createUserCode 登録ユーザコード
 * @property createAt 登録日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property version version
 */
data class ResourceAttributesResult(
    @field:JsonProperty("create_user_code")
    val createUserCode: String,

    @field:JsonProperty("create_at")
    val createAt: Long,

    @field:JsonProperty("last_update_user_code")
    val lastUpdateUserCode: String,

    @field:JsonProperty("last_update_at")
    val lastUpdateAt: Long,

    @field:JsonProperty("version")
    val version: Long
) {
    companion object {
        /**
         * ResourceAttributes からのインスタンス生成.
         *
         * @param resourceAttributes 対象 ResourceAttributes
         * @return 生成インスタンス
         */
        fun of(resourceAttributes: ResourceAttributes) =
            ResourceAttributesResult(createUserCode = resourceAttributes.createUserCode,
                createAt = resourceAttributes.createAt,
                lastUpdateUserCode = resourceAttributes.lastUpdateUserCode,
                lastUpdateAt = resourceAttributes.lastUpdateAt,
                version = resourceAttributes.version)
    }
}
