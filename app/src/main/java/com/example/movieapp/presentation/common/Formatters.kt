package com.example.movieapp.presentation.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Formatters {
    private const val ISO_DATE_LENGTH = 10

    fun year(date: String?): String? = date?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }

    fun displayDate(
        date: String?,
        locale: Locale = Locale.getDefault(),
    ): String? {
        if (date == null || date.length < ISO_DATE_LENGTH) return null
        return try {
            LocalDate
                .parse(date.take(ISO_DATE_LENGTH))
                .format(DateTimeFormatter.ofPattern("MMM d, yyyy", locale))
        } catch (_: DateTimeParseException) {
            null
        }
    }

    fun runtime(minutes: Int?): String? {
        if (minutes == null) return null
        val hours = minutes / 60
        val remainder = minutes % 60
        return when {
            hours == 0 -> "${remainder}m"
            remainder == 0 -> "${hours}h"
            else -> "${hours}h ${remainder}m"
        }
    }

    fun rating(value: Double): String = String.format(Locale.US, "%.1f", value)
}
