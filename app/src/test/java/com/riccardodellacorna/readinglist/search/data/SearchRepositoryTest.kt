package com.riccardodellacorna.readinglist.search.data

import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.search.data.remote.OpenLibraryApi
import com.riccardodellacorna.readinglist.search.data.remote.dto.BookDto
import com.riccardodellacorna.readinglist.search.data.remote.dto.SearchResponseDto
import com.riccardodellacorna.readinglist.testutil.TestDispatcherProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SearchRepositoryTest {

    private val api = mockk<OpenLibraryApi>()
    private val repository = SearchRepository(
        api = api,
        dispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())
    )

    @Test
    fun `success maps dtos to books and discards invalid ones`() = runTest {
        coEvery { api.searchBooks("dune") } returns SearchResponseDto(
            numFound = 2,
            docs = listOf(
                BookDto(
                    key = "/works/OL1W",
                    title = "Dune"
                ),
                BookDto(
                    key = "/works/OL2W",
                    title = null
                )
            )
        )

        val result = repository.search("dune")

        assertThat(result).isEqualTo(
            AppResult.Success(
                listOf(
                    Book(
                        "OL1W",
                        "Dune",
                        emptyList(),
                        null,
                        null
                    )
                )
            )
        )
    }

    @Test
    fun `blank query returns empty list without calling the api`() = runTest {
        val result = repository.search("   ")

        assertThat(result).isEqualTo(AppResult.Success(emptyList<Book>()))
        coVerify(exactly = 0) {
            api.searchBooks(
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `query is trimmed before calling the api`() = runTest {
        coEvery { api.searchBooks("dune") } returns SearchResponseDto()

        repository.search("  dune  ")

        coVerify(exactly = 1) { api.searchBooks("dune") }
    }

    @Test
    fun `io exception becomes a network failure`() = runTest {
        coEvery {
            api.searchBooks(
                any(),
                any(),
                any()
            )
        } throws IOException("offline")

        val result = repository.search("dune")

        assertThat(result).isEqualTo(AppResult.Failure(DataError.Network))
    }

    @Test
    fun `http error becomes a server failure with the status code`() = runTest {
        val httpError = HttpException(
            Response.error<Any>(
                503,
                "".toResponseBody()
            )
        )
        coEvery {
            api.searchBooks(
                any(),
                any(),
                any()
            )
        } throws httpError

        val result = repository.search("dune")

        assertThat(result).isEqualTo(AppResult.Failure(DataError.Server(503)))
    }
}
