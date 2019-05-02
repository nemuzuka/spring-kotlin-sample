package net.jp.vss.sample.controller.tasks

/**
 * UpdateTaskApiParameter の Fixture.
 */
class UpdateTaskApiParameterFixtures {
    companion object {
        fun create(): UpdateTaskApiParameter = UpdateTaskApiParameter(
            title = "update_件名1",
            content = "update_内容1",
            deadline = 1556817319045,
            isSetDeadlineToNull = true,
            attributes = """{"hoge":"super_hoge"}""")
    }
}
