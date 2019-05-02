package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.tasks.Task

/**
 * UpdateTaskUseCase のパラメータ.
 *
 * null を設定した項目は更新対象外です。
 *
 * - isSetDeadlineToNull を true にすると、deadline の値に関係なく、null に更新します。
 * - version を指定すると、Task の version と比較して、合致した時のみ更新を行います
 *
 * @property taskCode タスクコード
 * @property title 件名
 * @property content 内容
 * @property deadline 期日
 * @property isSetDeadlineToNull 期日を null にする場合、true
 * @property attributes 付帯情報(JSON 文字列)
 * @property version 更新前 version
 * @property updateUserCode 更新ユーザコード
 */
data class UpdateTaskUseCaseParameter(
    val taskCode: String,
    val title: String?,
    val content: String?,
    val deadline: Long?,
    val isSetDeadlineToNull: Boolean,
    val attributes: String?,
    val version: Long?,
    val updateUserCode: String
) {
    /**
     * 更新対象 Task 生成.
     *
     * パラメータを反映した Task を生成します
     *
     * @param task 更新元 Task
     * @return 更新対象 Task
     */
    fun buildUpdateTask(task: Task): Task {

        val baseTaskDetail = task.taskDetail
        val updateDeadline = if (isSetDeadlineToNull) null else {
            deadline?.let { it } ?: baseTaskDetail.deadline
        }

        val taskDetail = baseTaskDetail.copy(
            title = title?.let { it } ?: baseTaskDetail.title,
            content = content?.let { it } ?: baseTaskDetail.content,
            deadline = updateDeadline,
            attributes = attributes?.let { Attributes(it) } ?: baseTaskDetail.attributes
        )
        return task.copy(
            taskDetail = taskDetail,
            resourceAttributes = task.resourceAttributes.buildForUpdate(updateUserCode))
    }
}
