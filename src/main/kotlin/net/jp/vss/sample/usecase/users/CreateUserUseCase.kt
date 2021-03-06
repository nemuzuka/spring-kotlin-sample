package net.jp.vss.sample.usecase.users

import net.jp.vss.sample.domain.exceptions.DuplicateException

/**
 * User を登録する UseCase.
 */
interface CreateUserUseCase {

    /**
     * User 登録.
     *
     * @param parameter パラメータ
     * @return 登録結果
     * @throws DuplicateException 既に存在する
     */
    fun createUser(parameter: CreateUserUseCaseParameter): UserUseCaseResult
}
