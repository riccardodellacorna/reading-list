package com.riccardodellacorna.readinglist.search.domain

import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.model.Book

interface ISearchRepository {
    suspend fun search(query: String): AppResult<List<Book>>
}
