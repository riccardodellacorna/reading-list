package com.riccardodellacorna.readinglist.core.common

sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val error: DataError) : AppResult<Nothing>
}

sealed interface DataError {
    data object Network : DataError
    data class Server(val code: Int) : DataError
    data object Unknown : DataError
}
