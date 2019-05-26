package net.jp.vss.sample.domain.users

import net.jp.vss.sample.domain.exceptions.DuplicateException

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

    /**
     * 登録.
     *
     * @param user 対象 User
     * @param authenticatedPrincipalId 認証済みユーザ識別子
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 登録後 User
     * @throws DuplicateException 既に存在する
     */
    fun createUser(
        user: User,
        authenticatedPrincipalId: User.AuthenticatedPrincipalId,
        authorizedClientRegistrationId: User.AuthorizedClientRegistrationId,
        principal: User.Principal
    ): User
}
