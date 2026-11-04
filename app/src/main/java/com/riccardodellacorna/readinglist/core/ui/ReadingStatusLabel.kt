package com.riccardodellacorna.readinglist.core.ui

import com.riccardodellacorna.readinglist.core.model.ReadingStatus

fun ReadingStatus.label(): String = when (this) {
    ReadingStatus.TO_READ -> "To read"
    ReadingStatus.READING -> "Reading"
    ReadingStatus.READ -> "Read"
}
