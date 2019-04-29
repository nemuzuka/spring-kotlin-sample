package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.usecase.ResourceAttributesResultFixtures

/**
 * CreateTaskUseCaseResult の Fixture.
 */
class CreateTaskUseCaseResultFixtures {
    companion object {
        fun create() = CreateTaskUseCaseResult(
            taskCode = "TASK_0001",
            status = Task.TaskStatus.OPEN,
            title = "懸命な件名",
            content = "内容が内容とは言わせないよう",
            deadline = 1756268400000L,
            attributes = """{"hoge":"hige","fuga":{"neko":"nyan"}}""",
            resourceAttributesResult = ResourceAttributesResultFixtures.create()
        )
    }
}
