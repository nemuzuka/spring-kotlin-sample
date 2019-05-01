package net.jp.vss.sample.domain.exceptions

/**
 * 該当レコードが存在しない時の Exception.
 *
 * @param message メッセージ
 */
class NotFoundException(message: String) : RuntimeException(message)
