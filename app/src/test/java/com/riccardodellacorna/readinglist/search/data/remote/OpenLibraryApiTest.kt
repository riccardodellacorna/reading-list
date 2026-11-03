package com.riccardodellacorna.readinglist.search.data.remote

import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.network.createJson
import com.riccardodellacorna.readinglist.core.network.createOpenLibraryApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

/**
 * Test con MockWebServer:
 * MockWebServer è un piccolo server HTTP locale: tu decidi cosa risponde e poi puoi ispezionare la richiesta che il client ha inviato.
 * È un test di integrazione leggero: usa Retrofit, OkHttp e il serializer veri, senza rete reale.
 *
 * Cosa ti insegna:
 * - runTest esegue le funzioni suspend nei test, senza dispatcher reali
 * - server.enqueue(...) programma le risposte, in ordine
 * - server.takeRequest() verifica ciò che l’app invia, non solo ciò che riceve
 * - runCatching cattura l’eccezione attesa dentro una coroutine, dove assertThrows non può essere usato
 */

class OpenLibraryApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: OpenLibraryApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = createOpenLibraryApi(
            baseUrl = server.url("/").toString(),
            client = OkHttpClient(),
            json = createJson()
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `parses a successful response`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "numFound": 1,
                      "docs": [
                        {
                          "key": "/works/OL893415W",
                          "title": "Dune",
                          "author_name": ["Frank Herbert"],
                          "first_publish_year": 1965,
                          "cover_i": 12345,
                          "unknown_field": "ignored"
                        }
                      ]
                    }
                    """.trimIndent()
                )
        )

        val response = api.searchBooks("dune")

        assertThat(response.numFound).isEqualTo(1)
        assertThat(response.docs).hasSize(1)
        val doc = response.docs.first()
        assertThat(doc.key).isEqualTo("/works/OL893415W")
        assertThat(doc.title).isEqualTo("Dune")
        assertThat(doc.authorNames).containsExactly("Frank Herbert")
        assertThat(doc.coverId).isEqualTo(12345)
    }

    @Test
    fun `parses documents with missing optional fields`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{ "numFound": 1, "docs": [ { "key": "/works/OL1W" } ] }"""
            )
        )

        val doc = api.searchBooks("anything").docs.first()

        assertThat(doc.title).isNull()
        assertThat(doc.authorNames).isNull()
        assertThat(doc.firstPublishYear).isNull()
        assertThat(doc.coverId).isNull()
    }

    @Test
    fun `sends the expected request`() = runTest {
        server.enqueue(MockResponse().setBody("""{ "numFound": 0, "docs": [] }"""))

        api.searchBooks(query = "the hobbit", limit = 10)

        val request = server.takeRequest()
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl!!.encodedPath).isEqualTo("/search.json")
        assertThat(request.requestUrl!!.queryParameter("q")).isEqualTo("the hobbit")
        assertThat(request.requestUrl!!.queryParameter("limit")).isEqualTo("10")
        assertThat(request.requestUrl!!.queryParameter("fields"))
            .isEqualTo(OpenLibraryApi.DEFAULT_FIELDS)
    }

    @Test
    fun `server error is thrown as HttpException`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        val result = runCatching { api.searchBooks("dune") }

        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
        assertThat((error as HttpException).code()).isEqualTo(500)
    }
}
