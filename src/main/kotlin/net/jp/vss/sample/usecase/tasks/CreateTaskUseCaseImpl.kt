package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskRepositry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link CreateTaskUseCase}
 *
 * @property taskRepo Task のリポジトリ
 */
@Service
@Transactional
class CreateTaskUseCaseImpl(
    private val taskRepo: TaskRepositry
) : CreateTaskUseCase {

    override fun createTask(input: CreateTaskUseCase.Input): Task {
        val task = Task.buildForCreate(taskCodeValue = input.taskCodeValue,
            title = input.title,
            content = input.content,
            deadline = input.deadline,
            attributeJsonString = input.attributeJsonString,
            createUserCode = input.createUserCode)
        return taskRepo.createTask(task)
    }
}
