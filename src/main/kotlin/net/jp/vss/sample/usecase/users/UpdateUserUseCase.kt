package net.jp.vss.sample.usecase.users

import net.jp.vss.sample.domain.exceptions.NotFoundException

/**
 * User を更新する UseCase.
 */
interface UpdateUserUseCase {

    /**
     * User 更新.
     *
     * @param parameter パラメータ
     * @return 更新結果
     * @throws NotFoundException 更新対象の User が存在しない
     */
    fun updateUser(parameter: UpdateUserUseCaseParameter): UserUseCaseResult
}
