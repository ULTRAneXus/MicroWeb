package mw.framework

class TextWidget(val content: List<String>): Widget {
    override fun getHTML(): List<String> {
        val result: MutableList<String> = mutableListOf()
        result += "<p>"
        result.addAll(content.map { "$it<br>" })
        result += "</p>"
        return result
    }
}