package net.jp.vss.sample.domain.users

/**
 * ユーザ.
 */
data class User(
    val userId: UserId,
    val userCode: UserCode,
    val userDetail: UserDetail
) {

    /**
     * ユーザ 識別子値オブジェクト.
     *
     * @property value 値
     */
    data class UserId(val value: String)

    /**
     * ユーザコード値オブジェクト.
     *
     * @property value 値
     */
    data class UserCode(val value: String)

    /**
     * ユーザ詳細値オブジェクト.
     *
     * @property userName ユーザ名
     */
    data class UserDetail(val userName: String)

    /**
     * ユーザが認証した AuthorizedClientRegistrationId.
     *
     * @property value 値
     */
    data class AuthorizedClientRegistrationId(val value: String)

    /**
     * ユーザが認証した AuthorizedClientRegistration 上の principal.
     *
     * @property value 値
     */
    data class Principal(val value: String)
}
