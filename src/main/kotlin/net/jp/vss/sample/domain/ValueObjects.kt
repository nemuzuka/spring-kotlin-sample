package net.jp.vss.sample.domain

import net.jp.vss.sample.DatetimeUtils

/**
 * 付帯情報値オブジェクト.
 *
 * @property value JSON 文字列
 */
data class Attributes(val value: String) {
    companion object {
        fun of(value: String?): Attributes? = if (value != null) Attributes(value) else null
    }
}

/**
 * リソース付帯情報値オブジェクト.
 *
 * @property createUserCode 登録ユーザコード
 * @property createAt 登録日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property version version
 */
data class ResourceAttributes(
    val createUserCode: String,
    val createAt: Long,
    val lastUpdateUserCode: String,
    val lastUpdateAt: Long,
    val version: Long
) {
    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param createUserCode 登録ユーザコード
         * @return 登録時のインスタンス
         */
        fun buildForCreate(createUserCode: String): ResourceAttributes =
            ResourceAttributes(createUserCode = createUserCode,
                createAt = DatetimeUtils.now(),
                lastUpdateUserCode = createUserCode,
                lastUpdateAt = DatetimeUtils.now(),
                version = 0L)
    }

    /**
     * 更新時のインスタンス生成.
     *
     * @param updateUserCode 更新ユーザコード
     * @return 更新時のインスタンス
     */
    fun buildForUpdate(updateUserCode: String): ResourceAttributes =
            this.copy(lastUpdateUserCode = updateUserCode,
            lastUpdateAt = DatetimeUtils.now(),
                    version = this.version + 1)
}
