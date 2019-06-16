package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Implements {@link ListTaskUseCase}
 *
 * @property taskRepo Task のリポジトリ
 */
@Service
@Transactional(readOnly = true)
class ListTaskUseCaseImpl(
    private val taskRepo: TaskRepository
) : ListTaskUseCase {
    override fun allTasks(): List<TaskUseCaseResult> = taskRepo.allTasks().map { TaskUseCaseResult.of(it) }.toList()
}
