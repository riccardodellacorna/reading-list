package com.riccardodellacorna.readinglist.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 *  Room non sa salvare una List<String>, quindi serve un TypeConverter. L’enum invece lo salva da solo, per nome.
 *  Attenzione: se in futuro rinomini un valore dell’enum, i dati già salvati si rompono. È un classico caso di migrazione.
 **/

class Converters {
    private val serializer = ListSerializer(String.serializer())

    @TypeConverter
    fun fromAuthors(authors: List<String>): String =
        Json.encodeToString(serializer, authors)

    @TypeConverter
    fun toAuthors(value: String): List<String> =
        Json.decodeFromString(serializer, value)
}
