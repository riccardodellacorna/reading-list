package com.riccardodellacorna.readinglist.core.model

data class Book(
    val id: String,
    val title: String,
    val authors: List<String>,
    val firstPublishYear: Int?,
    val coverUrl: String?
)
