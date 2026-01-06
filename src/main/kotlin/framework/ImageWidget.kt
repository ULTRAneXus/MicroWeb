package mw.framework

class ImageWidget(val path: String, val altText: String) : Widget {
    override fun getHTML(): List<String> {
        return listOf("<img src=\"$path\" alt=\"$altText\"/>")
    }
}