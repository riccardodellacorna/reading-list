package com.riccardodellacorna.readinglist.search.data

import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.common.DispatcherProvider
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.search.data.remote.OpenLibraryApi
import com.riccardodellacorna.readinglist.search.data.remote.dto.toDomain
import com.riccardodellacorna.readinglist.search.domain.ISearchRepository
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class SearchRepository(
    private val api: OpenLibraryApi,
    private val dispatchers: DispatcherProvider,
) : ISearchRepository {

    override suspend fun search(query: String): AppResult<List<Book>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return AppResult.Success(emptyList())

        return withContext(dispatchers.io) {
            try {
                AppResult.Success(api.searchBooks(trimmed).docs.toDomain())
            } catch (e: IOException) {
                AppResult.Failure(DataError.Network)
            } catch (e: HttpException) {
                AppResult.Failure(DataError.Server(e.code()))
            } catch (e: SerializationException) {
                AppResult.Failure(DataError.Unknown)
            }
        }
    }
}
