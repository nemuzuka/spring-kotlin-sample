package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import java.util.UUID
import net.jp.vss.sample.domain.exceptions.UnmatchVersionException

/**
 * タスク.
 *
 * @property taskId タスク識別子
 * @property taskCode タスクコード
 * @property status ステータス
 * @property taskDetail タスク詳細
 * @property resourceAttributes リソース詳細情報
 */
data class Task(
    val taskId: TaskId,
    val taskCode: TaskCode,
    val status: TaskStatus,
    val taskDetail: TaskDetail,
    val resourceAttributes: ResourceAttributes
) {

    companion object {
        /**
         * 登録時のインスタンス生成.
         *
         * @param taskCodeValue タスクコード文字列
         * @param title 件名
         * @param content 内容
         * @param deadline 期日
         * @param attributeJsonString 付帯情報
         * @param createUserCode 登録ユーザコード
         * @return 登録時のTask
         */
        fun buildForCreate(
            taskCodeValue: String,
            title: String,
            content: String,
            deadline: Long?,
            attributeJsonString: String?,
            createUserCode: String
        ): Task {
            val taskId = TaskId(UUID.randomUUID().toString())
            val taskCode = TaskCode(taskCodeValue)
            val taskDetail = TaskDetail(title = title,
                    content = content,
                    deadline = deadline,
                    attributes = Attributes.of(attributeJsonString))
            val resourceAttributes = ResourceAttributes.buildForCreate(createUserCode)
            return Task(taskId = taskId, taskCode = taskCode, status = TaskStatus.OPEN, taskDetail = taskDetail,
                    resourceAttributes = resourceAttributes)
        }
    }

    /**
     * 処理済み.
     *
     * @param updateUserCode 更新ユーザコード
     * @return 処理済みの Task
     */
    fun done(updateUserCode: String): Task {
        val updatedResourceAttributes = this.resourceAttributes.buildForUpdate(updateUserCode)
        return this.copy(status = TaskStatus.DONE, resourceAttributes = updatedResourceAttributes)
    }

    /**
     * 再オープン.
     *
     * @param updateUserCode 更新ユーザコード
     * @return 再オープンした Task
     */
    fun reopen(updateUserCode: String): Task {
        val updatedResourceAttributes = this.resourceAttributes.buildForUpdate(updateUserCode)
        return this.copy(status = TaskStatus.OPEN, resourceAttributes = updatedResourceAttributes)
    }

    /**
     * version 比較.
     *
     * 比較対象 version が null でない場合、本インスタンスの version と比較します
     * @param version 比較対象 version
     * @throws UnmatchVersionException 比較した結果、version が異なる時
     */
    fun validateVersion(version: Long?) = this.resourceAttributes.validateVersion(version)

    /**
     * タスク 識別子値オブジェクト.
     *
     * @property value 値
     */
    data class TaskId(val value: String)

    /**
     * タスクコード値オブジェクト.
     *
     * @property value
     */
    data class TaskCode(val value: String)

    /**
     * タスク詳細値オブジェクト.
     *
     * @property title 件名
     * @property content 内容
     * @property deadline 期日
     * @property attributes 付帯情報
     */
    data class TaskDetail(
        val title: String,
        val content: String,
        val deadline: Long?,
        val attributes: Attributes?
    )

    /**
     * Task のステータス.
     */
    enum class TaskStatus {
        /** Open. */
        OPEN,
        /** 処理済み. */
        DONE
    }
}
