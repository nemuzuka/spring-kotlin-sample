package net.jp.vss.sample.usecase.users

/**
 * CreateUserUseCaseParameter の Fixture.
 */
class CreateUserUseCaseParameterFixtures {
    companion object {
        fun create() = CreateUserUseCaseParameter("USER_0001",
            "名前1", "google", "PRINCIPAL_0001")
    }
}
