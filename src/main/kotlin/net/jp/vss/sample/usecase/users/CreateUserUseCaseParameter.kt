package net.jp.vss.sample.usecase.users

/**
 * CreateUserUseCase のパラメータ
 *
 * @property userCode ユーザコード
 * @property userName ユーザ名
 * @property authorizedClientRegistrationId AuthorizedClientRegistrationId
 * @property principal Principal
 */
data class CreateUserUseCaseParameter(
    val userCode: String,
    val userName: String,
    val authorizedClientRegistrationId: String,
    val principal: String
)
