package com.riccardodellacorna.readinglist.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Tempo e thread sono dipendenze nascoste: se il codice chiama System.currentTimeMillis() o Dispatchers.IO direttamente,
 * nei test non le controlli. Iniettandole, nei test passi un orologio finto e un dispatcher di test.
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
}
