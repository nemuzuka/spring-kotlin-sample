package net.jp.vss.sample.usecase.tasks

/**
 * UpdateTaskUseCaseParameter の Fixture.
 */
class UpdateTaskUseCaseParameterFixture {

    companion object {
        fun create() = UpdateTaskUseCaseParameter("TASK_0001",
            "TITLE", "Kotlin を勉強する", 1588268400001L, false, """{"age":30}""", 3L, "USER_0012")

        fun createNullValue(isSetDeadlineToNull: Boolean) = UpdateTaskUseCaseParameter("TASK_0002",
            null, null, null, isSetDeadlineToNull, null, 4L, "USER_0013")
    }
}
