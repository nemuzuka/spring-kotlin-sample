package net.jp.vss.sample.controller.users

/**
 * CreateUserApiParameter の Fixture.
 */
class CreateUserApiParameterFixtures {

    companion object {
        fun create(): CreateUserApiParameter = CreateUserApiParameter(
                userCode = "USER_0001",
                userName = "名前1")
    }
}
