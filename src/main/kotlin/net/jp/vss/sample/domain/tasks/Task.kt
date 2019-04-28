package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.Attributes
import net.jp.vss.sample.ResourceAttributes
import java.util.UUID

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
