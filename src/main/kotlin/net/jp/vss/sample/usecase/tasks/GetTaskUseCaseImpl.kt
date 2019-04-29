package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.domain.tasks.TaskRepositry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link GetTaskUseCase}
 *
 * @property taskRepo Task のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class GetTaskUseCaseImpl(
    private val taskRepo: TaskRepositry
) : GetTaskUseCase {
    override fun getTask(taskCode: String): TaskUseCaseResult =
        TaskUseCaseResult.of(taskRepo.getTask(Task.TaskCode(taskCode)))
}
