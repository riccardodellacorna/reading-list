package com.riccardodellacorna.readinglist.search

import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.search.domain.ISearchRepository
import kotlinx.coroutines.delay

class FakeSearchRepository : ISearchRepository {
    var result: AppResult<List<Book>> = AppResult.Success(emptyList())
    var delayMillis = 0L
    val queries = mutableListOf<String>()

    override suspend fun search(query: String): AppResult<List<Book>> {
        queries += query
        delay(delayMillis)
        return result
    }
}
