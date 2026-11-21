package com.example.movieapp.core.utils

object ReviewTextCleaner {
    private val lineBreakTag = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
    private val htmlTag = Regex("""<[^>]+>""")
    private val markdownLink = Regex("""\[([^\]]+)]\([^)]*\)""")
    private val boldOrStrike = Regex("""(\*\*|__|~~)(.+?)\1""")
    private val italicAsterisk = Regex("""(?<![\w*])\*(?!\s)(.+?)(?<!\s)\*(?![\w*])""")
    private val italicUnderscore = Regex("""(?<![\w_])_(?!\s)(.+?)(?<!\s)_(?![\w_])""")
    private val headingOrQuote = Regex("""^\s{0,3}(#{1,6}\s+|>\s?)""", RegexOption.MULTILINE)
    private val extraBlankLines = Regex("""\n{3,}""")
    private val htmlEntities =
        listOf(
            "&quot;" to "\"",
            "&#39;" to "'",
            "&apos;" to "'",
            "&lt;" to "<",
            "&gt;" to ">",
            "&nbsp;" to " ",
            "&amp;" to "&",
        )

    fun clean(raw: String): String {
        var text = raw.replace("\r\n", "\n").replace('\r', '\n')
        text = lineBreakTag.replace(text, "\n")
        text = htmlTag.replace(text, "")
        text = markdownLink.replace(text, "$1")
        text = boldOrStrike.replace(text, "$2")
        text = italicAsterisk.replace(text, "$1")
        text = italicUnderscore.replace(text, "$1")
        text = headingOrQuote.replace(text, "")
        htmlEntities.forEach { (entity, value) -> text = text.replace(entity, value) }
        text = text.lines().joinToString("\n") { it.trim() }
        return extraBlankLines.replace(text, "\n\n").trim()
    }
}
