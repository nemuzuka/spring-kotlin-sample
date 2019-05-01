package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.exceptions.NotFoundException

/**
 * Task を取得する UseCase.
 */
interface GetTaskUseCase {

    /**
     * Task 取得.
     *
     * @param taskCode task_code
     * @return 対象 Task
     * @throws NotFoundException 指定したTaskが存在しない
     */
    fun getTask(taskCode: String): TaskUseCaseResult
}
