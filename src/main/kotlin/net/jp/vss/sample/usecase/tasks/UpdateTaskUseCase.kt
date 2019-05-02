package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.exceptions.UnmatchVersionException
import net.jp.vss.sample.domain.exceptions.NotFoundException

/**
 * Task を更新する UseCase.
 */
interface UpdateTaskUseCase {

    /**
     * Task 更新.
     *
     * null の項目は更新対象としません。
     * @param parameter パラメータ
     * @return 更新結果
     * @throws NotFoundException 更新対象の Task が存在しない
     * @throws UnmatchVersionException version 指定時、更新対象の Task の version と合致しない
     */
    fun updateTask(parameter: UpdateTaskUseCaseParameter): TaskUseCaseResult

    /**
     * 処理済み.
     *
     * @param taskCode タスクコード
     * @param version version
     * @param updateUserCode 更新 UserCode
     * @return 処理済み結果
     * @throws NotFoundException 処理済み対象の Task が存在しない
     * @throws UnmatchVersionException version 指定時、更新対象の Task の version と合致しない
     */
    fun done(taskCode: String, version: Long?, updateUserCode: String): TaskUseCaseResult
}
