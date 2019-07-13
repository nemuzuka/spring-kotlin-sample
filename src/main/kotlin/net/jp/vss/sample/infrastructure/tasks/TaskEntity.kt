package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributes
import net.jp.vss.sample.domain.tasks.Task
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.Version

/**
 * Task Entity.
 *
 * @property taskId タスク識別子
 * @property taskCode タスクコード
 * @property status ステータス
 * @property title 件名
 * @property contentValue 内容
 * @property deadline 期限
 * @property attributes 付帯情報
 * @property createUserCode 生成ユーザコード
 * @property createAt 生成日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property versionNo バージョン
 */
@Entity(immutable = true)
@Table(name = "tasks")
data class TaskEntity(
    @Id
    @Column(name = "task_id")
    val taskId: String,

    @Column(name = "task_code")
    val taskCode: String,

    @Column(name = "status")
    val status: String,

    @Column(name = "title")
    val title: String,

    @Column(name = "content_value")
    val contentValue: String,

    @Column(name = "deadline")
    val deadline: Long?,

    @Column(name = "attributes")
    val attributes: String?,

    @Column(name = "create_user_code")
    val createUserCode: String,

    @Column(name = "create_at")
    val createAt: Long,

    @Column(name = "last_update_user_code")
    val lastUpdateUserCode: String,

    @Column(name = "last_update_at")
    val lastUpdateAt: Long,

    @Column(name = "version_no")
    @Version
    val versionNo: Long
) {
    /**
     * Task 変換
     *
     * @return 変換後 Task
     */
    fun toTask(): Task {
        val taskId = Task.TaskId(taskId)
        val taskCode = Task.TaskCode(taskCode)
        val status = Task.TaskStatus.valueOf(status)
        val taskDetail = Task.TaskDetail(title = title,
            content = contentValue,
            deadline = deadline,
            attributes = Attributes.of(attributes))
        val resourceAttributes = ResourceAttributes(createUserCode = createUserCode,
            createAt = createAt,
            lastUpdateUserCode = lastUpdateUserCode,
            lastUpdateAt = lastUpdateAt,
            version = versionNo)
        return Task(taskId = taskId, taskCode = taskCode, status = status, taskDetail = taskDetail,
            resourceAttributes = resourceAttributes)
    }
}
