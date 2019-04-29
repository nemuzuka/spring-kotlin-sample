package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.exceptions.DuplicateException

/**
 * Task を登録する UseCase.
 */
interface CreateTaskUseCase {

    /**
     * Task 登録.
     *
     * @param parameter パラメータ
     * @return 登録結果
     * @throws DuplicateException 既に存在する
     */
    fun createTask(parameter: CreateTaskUseCaseParameter): TaskUseCaseResult
}
