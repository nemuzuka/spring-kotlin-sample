package net.jp.vss.sample.usecase.tasks

/**
 * CreateTaskUseCaseParameter の Fixture.
 */
class CreateTaskUseCaseParameterFixtures {
    companion object {
        fun create() = CreateTaskUseCaseParameter("TASK_0001",
        "TITLE", "Kotlin を勉強する", 1588268400001L, """{"age":30}""", "USER_0011")
    }
}
