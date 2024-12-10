package mw

/**
 * index should be list of pairs name:link to page
 */
class IndexWidget(private val index: List<Pair<String?, String>>) : Widget {
    override fun getHTML(): List<String> {
        val result: MutableList<String> = mutableListOf()
        result += "<ul>"
        for (i in index) {
            result += "<li><a href=\"${i.second}\">${i.first ?: i.second}</a></li>"
        }
        result += "</ul>"
        return result
    }
}