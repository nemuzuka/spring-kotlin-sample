package net.jp.vss.sample.usecase.tasks

import net.jp.vss.sample.domain.tasks.Task
import net.jp.vss.sample.exception.DuplicateException

/**
 * Task を登録する UseCase.
 */
interface CreateTaskUseCase {

    /**
     * Task 登録.
     *
     * @param input Input Data
     * @return 登録 Task
     * @throws DuplicateException 既に存在する
     */
    fun createTask(input: Input): Task

    /**
     * Input Data.
     *
     * @property taskCodeValue タスクコード文字列
     * @property title 件名
     * @property content 内容
     * @property deadline 期日
     * @property attributeJsonString 付帯情報(JSON 文字列)
     * @property createUserCode 登録ユーザコード
     */
    data class Input(
        val taskCodeValue: String,
        val title: String,
        val content: String,
        val deadline: Long?,
        val attributeJsonString: String?,
        val createUserCode: String
    )
}
