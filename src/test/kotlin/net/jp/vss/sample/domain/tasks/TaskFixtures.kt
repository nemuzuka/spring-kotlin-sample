package net.jp.vss.sample.domain.tasks

import net.jp.vss.sample.Attributes
import net.jp.vss.sample.ResourceAttributesFixtures

/**
 * Task の Fixture.
 */
class TaskFixtures {
    companion object {
        fun create(): Task {
            val taskId = Task.TaskId("550e8400-e29b-41d4-a716-446655440000")
            val taskCode = Task.TaskCode("TASK-0001")
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
