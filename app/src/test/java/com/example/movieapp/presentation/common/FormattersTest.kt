package com.example.movieapp.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Locale

class FormattersTest {
    @Test
    fun `year is taken from release date`() {
        assertEquals("2021", Formatters.year("2021-09-15"))
    }

    @Test
    fun `year is null for missing or malformed date`() {
        assertNull(Formatters.year(null))
        assertNull(Formatters.year("soon"))
    }

    @Test
    fun `display date formats iso dates and timestamps`() {
        assertEquals("Sep 15, 2021", Formatters.displayDate("2021-09-15", Locale.US))
        assertEquals("Jan 2, 2024", Formatters.displayDate("2024-01-02T10:00:00.000Z", Locale.US))
    }

    @Test
    fun `display date is null for invalid input`() {
        assertNull(Formatters.displayDate(null, Locale.US))
        assertNull(Formatters.displayDate("2021-13-45", Locale.US))
    }

    @Test
    fun `runtime is shown in hours and minutes`() {
        assertEquals("2h 16m", Formatters.runtime(136))
        assertEquals("2h", Formatters.runtime(120))
        assertEquals("45m", Formatters.runtime(45))
        assertNull(Formatters.runtime(null))
    }

    @Test
    fun `rating uses one decimal`() {
        assertEquals("7.8", Formatters.rating(7.849))
        assertEquals("0.0", Formatters.rating(0.0))
    }
}
