package mw.framework

class LinkWidget(val path: String, val altText: String) : Widget {
    override fun getHTML(): List<String> {
        return listOf("<a href=\"$path\">$altText</a>")
    }
}