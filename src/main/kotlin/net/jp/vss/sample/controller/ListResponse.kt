package net.jp.vss.sample.controller

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * List レスポンス.
 *
 * @param T 型
 * @property list レスポンス対象 List
 */
data class ListResponse <T> (
    @field:JsonProperty("list")
    val list: List<T>
)
