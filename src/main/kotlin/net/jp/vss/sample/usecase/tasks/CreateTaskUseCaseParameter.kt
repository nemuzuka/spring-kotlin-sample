package net.jp.vss.sample.usecase.tasks

/**
 * CreateTaskUseCase のパラメータ.
 *
 * @property taskCode タスクコード
 * @property title 件名
 * @property content 内容
 * @property deadline 期日
 * @property attributes 付帯情報(JSON 文字列)
 * @property createUserCode 登録ユーザコード
 */
data class CreateTaskUseCaseParameter(
    val taskCode: String,
    val title: String,
    val content: String,
    val deadline: Long?,
    val attributes: String?,
    val createUserCode: String
)
