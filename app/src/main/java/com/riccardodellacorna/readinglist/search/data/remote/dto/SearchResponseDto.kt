package com.riccardodellacorna.readinglist.search.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val numFound: Int = 0,
    val docs: List<BookDto> = emptyList()
)

@Serializable
data class BookDto(
    val key: String,
    val title: String? = null,
    @SerialName("author_name")
    val authorNames: List<String>? = null,
    @SerialName("first_publish_year")
    val firstPublishYear: Int? = null,
    @SerialName("cover_i")
    val coverId: Int? = null
)
