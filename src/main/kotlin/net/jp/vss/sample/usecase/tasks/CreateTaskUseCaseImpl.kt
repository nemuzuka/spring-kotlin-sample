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

    override fun createTask(parameter: CreateTaskUseCaseParameter): CreateTaskUseCaseResult {
        val task = Task.buildForCreate(taskCodeValue = parameter.taskCode,
            title = parameter.title,
            content = parameter.content,
            deadline = parameter.deadline,
            attributeJsonString = parameter.attributes,
            createUserCode = parameter.createUserCode)
        return CreateTaskUseCaseResult.of(taskRepo.createTask(task))
    }
}
