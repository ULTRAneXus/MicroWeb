package mw

class TextWidget(private val title: String?, private val content: List<String>) : Widget {
    override fun getHTML(): List<String> {
        val result: MutableList<String> = mutableListOf()
        title?.let { result += "<h1>$title</h1>" }
        result += "<p>"
        for (line in content) {
            result += (line.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&apos;") + "<br>")
        }
        result += "</p>"
        return result
    }
}