package net.jp.vss.sample.exception

import java.lang.RuntimeException

/**
 * 既に存在する場合の Exception.
 *
 * @param message メッセージ
 */
class DuplicateException(message: String) : RuntimeException(message)
