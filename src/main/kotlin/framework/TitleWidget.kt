package mw.framework

class TitleWidget(val content: String, val titleSize: Int = 1) : Widget {
    init {
        if (titleSize !in 1..6) println("title size $titleSize unsupported")
    }

    override fun getHTML(): List<String> {
        return listOf("<h$titleSize>$content</h$titleSize>")
    }
}