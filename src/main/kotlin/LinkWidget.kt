package mw

class LinkWidget(private val path: String, private val alt: String) : Widget {
    override fun getHTML(): List<String> {
        return listOf("<a href=\"$path\">$alt</a>")
    }
}