package net.jp.vss.sample.usecase.users

import net.jp.vss.sample.domain.users.User
import net.jp.vss.sample.domain.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Implements {@link CreateUserUseCase}
 *
 * @property userRepo User のリポジトリ
 */
@Service
@Transactional
class CreateUserUseCaseImpl(
    private val userRepo: UserRepository
) : CreateUserUseCase {

    override fun createUser(parameter: CreateUserUseCaseParameter): UserUseCaseResult {
        val user = User.buildForCreate(parameter.userCode, parameter.userName)
        val createdUser = userRepo.createUser(user = user,
            authenticatedPrincipalId = User.AuthenticatedPrincipalId(UUID.randomUUID().toString()),
            authorizedClientRegistrationId = User.AuthorizedClientRegistrationId(
                parameter.authorizedClientRegistrationId),
            principal = User.Principal(parameter.principal))
        return UserUseCaseResult.of(createdUser)
    }
}
