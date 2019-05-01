package net.jp.vss.sample.controller.tasks

/**
 * CreateTaskApiParameter の Fixture.
 */
class CreateTaskApiParameterFixtures {

    companion object {
        fun create(): CreateTaskApiParameter = CreateTaskApiParameter(
                taskCode = "TASK_0001",
                title = "件名1",
                content = "内容1",
                deadline = 1556417319045,
                attributes = """{"hoge":"hage"}""")
    }
}
