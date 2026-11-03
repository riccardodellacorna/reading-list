package com.riccardodellacorna.readinglist.search.data.remote

import com.riccardodellacorna.readinglist.search.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query


/** Interfaccia Retrofit **/
interface OpenLibraryApi {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = DEFAULT_FIELDS,
        @Query("limit") limit: Int = 20
    ): SearchResponseDto

    companion object {
        const val DEFAULT_FIELDS = "key,title,author_name,first_publish_year,cover_i"
    }
}
