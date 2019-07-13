package net.jp.vss.sample.infrastructure.tasks

import net.jp.vss.sample.domain.tasks.Task

/**
 * TaskEntity の Fixture.
 */
class TaskEntityFixtures {
    companion object {
        fun create() = create("TASK-0001")

        fun create(taskCode: String): TaskEntity = TaskEntity(taskId = "550e8400-e29b-41d4-a716-446655440000",
            taskCode = taskCode,
            status = Task.TaskStatus.OPEN.name,
            title = "TASK-0001の件名",
            contentValue = "頑張ってテストを書く",
            deadline = 1546732800000L,
            attributes = """{"attri":"bute"}""",
            createUserCode = "CREATE_USER_0001",
            createAt = 1546732800001L,
            lastUpdateUserCode = "UPDATE_USER_0002",
            lastUpdateAt = 1546732800002L,
            versionNo = 123)
    }
}
