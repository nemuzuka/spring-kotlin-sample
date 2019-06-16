package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.domain.exceptions.NotFoundException
import net.jp.vss.sample.domain.exceptions.DuplicateException

/**
 * Task のリポジトリ.
 */
interface TaskRepository {
    /**
     * 登録.
     *
     * @param task 対象 Task
     * @return 登録後 Task
     * @throws DuplicateException 既に存在する
     */
    fun createTask(task: Task): Task

    /**
     * 取得.
     *
     * @param taskCode タスクコード
     * @return 該当 Task
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun getTask(taskCode: Task.TaskCode): Task

    /**
     * lock して取得.
     *
     * @param taskCode タスクコード
     * @return 該当 Task
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun lockTask(taskCode: Task.TaskCode): Task

    /**
     * 更新.
     *
     * version をインクリメントして Task を更新します
     * @param task 対象 Task
     * @return 更新後 Task
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun updateTask(task: Task): Task

    /**
     * 全件取得.
     *
     * @return 該当レコード
     */
    fun allTasks(): List<Task>
}
