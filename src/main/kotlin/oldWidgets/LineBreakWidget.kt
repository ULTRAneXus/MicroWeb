package mw.oldWidgets

class LineBreakWidget : Widget {
    override fun getHTML(): List<String> {
        return listOf("<br>")
    }
}