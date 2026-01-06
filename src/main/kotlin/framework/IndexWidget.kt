package framework

import mw.framework.LinkWidget
import mw.framework.Widget

class IndexWidget(val index: List<LinkWidget> = listOf()) : Widget {
    override fun getHTML(): List<String> {
        val res = mutableListOf<String>()
        res += "<ul>"
        index.forEach { link ->
            res += "<li>${link.getHTML().first()}</li>"
        }
        res += "</ul>"
        return res
    }

}