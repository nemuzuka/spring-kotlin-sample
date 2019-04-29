package net.jp.vss.sample.controller.exceptions

/**
 * エラー時のレスポンスを表す Exception.
 *
 * @param message メッセージ
 * @param cause Throwable
 */
open class HttpResponseException(message: String, cause: Throwable) : RuntimeException(message, cause)
