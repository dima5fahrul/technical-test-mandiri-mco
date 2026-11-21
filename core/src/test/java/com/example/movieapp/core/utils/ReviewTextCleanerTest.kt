package com.example.movieapp.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewTextCleanerTest {
    @Test
    fun `removes bold and italic markdown markers`() {
        val raw = "It found **a bronze statue** and *really* loved __it__, _honestly_."

        assertEquals("It found a bronze statue and really loved it, honestly.", ReviewTextCleaner.clean(raw))
    }

    @Test
    fun `keeps underscores and asterisks that are not formatting`() {
        val raw = "michael_s rated it 5 * 2 = 10 in snake_case"

        assertEquals(raw, ReviewTextCleaner.clean(raw))
    }

    @Test
    fun `removes strikethrough markers`() {
        assertEquals("bad good", ReviewTextCleaner.clean("~~bad~~ good"))
    }

    @Test
    fun `replaces markdown links with their label`() {
        assertEquals("Watch the trailer now", ReviewTextCleaner.clean("Watch the [trailer](https://youtu.be/abc) now"))
    }

    @Test
    fun `strips html tags and turns br into line breaks`() {
        assertEquals("Great\nmovie ever", ReviewTextCleaner.clean("Great<br/>movie <em>ever</em>"))
    }

    @Test
    fun `decodes common html entities`() {
        val raw = "Tom &amp; Jerry &quot;classic&quot; &#39;99 &lt;3"

        assertEquals("Tom & Jerry \"classic\" '99 <3", ReviewTextCleaner.clean(raw))
    }

    @Test
    fun `removes heading and quote markers`() {
        assertEquals("Verdict\nMust watch", ReviewTextCleaner.clean("## Verdict\n> Must watch"))
    }

    @Test
    fun `normalizes line breaks and trims extra whitespace`() {
        val raw = "  Line one   \r\n\r\n\r\n\r\nLine two\r\n   …  \r\n"

        assertEquals("Line one\n\nLine two\n…", ReviewTextCleaner.clean(raw))
    }
}
