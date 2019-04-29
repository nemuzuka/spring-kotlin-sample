package net.jp.vss.sample.controller.tasks

/**
 * CreateTaskApiParameter の Fixture.
 */
class CreateTaskApiParameterFixtures {

    companion object {
        fun create(): CreateTaskApiParameter = CreateTaskApiParameter(
                taskCodeValue = "TASK_0001",
                title = "件名1",
                content = "内容1",
                deadline = 1556417319045,
                attributeJsonString = """{"hoge":"hage"}""")
    }
}
