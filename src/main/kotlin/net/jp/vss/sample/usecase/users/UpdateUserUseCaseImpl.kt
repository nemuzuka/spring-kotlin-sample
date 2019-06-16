package net.jp.vss.sample.usecase.users

import net.jp.vss.sample.domain.users.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import net.jp.vss.sample.domain.users.UserRepository

/**
 * Implements {@link UpdateUserUseCase}
 *
 * @property userRepo User のリポジトリ
 */
@Service
@Transactional
class UpdateUserUseCaseImpl(private val userRepo: UserRepository) : UpdateUserUseCase {
    override fun updateUser(parameter: UpdateUserUseCaseParameter): UserUseCaseResult {
        val user = userRepo.lockUser(User.UserCode(parameter.userCode))
        val updateUser = parameter.buildUpdateUser(user)
        return UserUseCaseResult.of(userRepo.updateUser(updateUser))
    }
}
