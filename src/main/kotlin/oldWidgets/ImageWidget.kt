package mw.oldWidgets

class ImageWidget(private val path: String,private val alt: String) : Widget {
    override fun getHTML(): List<String> {
        return listOf("<img src=\"$path\" alt=\"$alt\"/>")
    }
}