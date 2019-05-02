package net.jp.vss.sample.usecase.tasks

/**
 * Task 一覧を取得する UseCase.
 */
interface ListTaskUseCase {

    /**
     * 全ての Task 取得.
     *
     * @return 対象 Task List
     */
    fun allTasks(): List<TaskUseCaseResult>
}
