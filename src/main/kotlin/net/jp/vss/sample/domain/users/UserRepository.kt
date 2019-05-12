package net.jp.vss.sample.domain.users

/**
 * User のリポジトリ.
 */
interface UserRepository {

    /**
     * 認証情報からの取得.
     *
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 該当ユーザ(存在しない場合、null)
     */
    fun getUserOrNull(
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User?
}
