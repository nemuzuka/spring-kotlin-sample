package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.domain.Attributes
import net.jp.vss.sample.domain.ResourceAttributesFixtures

/**
 * Task の Fixture.
 */
class TaskFixtures {
    companion object {
        fun create() = create("TASK-0001")

        fun create(taskCodeValue: String): Task {
            val taskId = Task.TaskId("550e8400-e29b-41d4-a716-446655440000")
            val taskCode = Task.TaskCode(taskCodeValue)
            val taskDetail = Task.TaskDetail(title = "TASK-0001の件名",
                content = "頑張ってテストを書く",
                deadline = 1546732800000L,
                attributes = Attributes("""{"attri":"bute"}"""))
            val resourceAttributes = ResourceAttributesFixtures.create()
            return Task(taskId = taskId, taskCode = taskCode, status = Task.TaskStatus.OPEN,
                taskDetail = taskDetail, resourceAttributes = resourceAttributes)
        }
    }
}
