package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskRepositry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link UpdateTaskUseCase}
 *
 * @property taskRepo Task のリポジトリ
 */
@Service
@Transactional
class UpdateTaskUseCaseImpl(private val taskRepo: TaskRepositry) : UpdateTaskUseCase {
    override fun updateTask(parameter: UpdateTaskUseCaseParameter): TaskUseCaseResult {
        val task = taskRepo.lockTask(Task.TaskCode(parameter.taskCode))
        task.validateVersion(parameter.version)

        val updateTask = parameter.buildUpdateTask(task)
        return TaskUseCaseResult.of(taskRepo.updateTask(updateTask))
    }

    override fun done(taskCode: String, version: Long?, updateUserCode: String): TaskUseCaseResult {
        val task = taskRepo.lockTask(Task.TaskCode(taskCode))
        task.validateVersion(version)

        val updateTask = task.done(updateUserCode)
        return TaskUseCaseResult.of(taskRepo.updateTask(updateTask))
    }
}
